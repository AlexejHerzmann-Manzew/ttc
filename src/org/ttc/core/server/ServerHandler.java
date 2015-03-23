package org.ttc.core.server;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.newdawn.slick.Color;
import org.sparkle.jbind.JBinD;
import org.sparkle.jbind.JBinDException;
import org.sparkle.jbind.Part;
import org.sparkle.jcfg.JCFG;
import org.ttc.core.game.Order;
import org.ttc.core.game.Room;

/**
 *
 * @author yew_mentzaki
 */
public class ServerHandler extends org.sparkle.janette.server.ServerHandler {

    static Random r = new Random();
    Room room;
    int player;
    Color color = Color.white;
    ServerHandler opponent;

    public ServerHandler() {
        if (Server.waiting == null) {
            System.out.println("New player connected and waiting.");
            Server.waiting = this;
        } else {
            System.out.println("New player connected. Starting game in new room.");
            opponent = Server.waiting;
            Server.waiting = null;

            room = new Room(2);
            opponent.room = room;

            player = r.nextInt(2);
            opponent.player = 1 - player;

            room.players[0].ai = false;
            room.players[1].ai = false;
            room.players[0].color = Color.white;
            room.players[1].color = Color.white;

        }
    }

    @Override
    public JBinD out() {
        try {
        JBinD bind = new JBinD();
        JCFG cfg = new JCFG();
        cfg.set("status", "waiting");
        if (room == null) {
            try {
                bind.addPart(new Part("message", cfg));
            } catch (JBinDException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return bind;
        } else {
            cfg.set("status", "playing");
            cfg.set("player", player);
            Color color = room.players[0].color;
            String color_line = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            cfg.set("p0c", color_line);
            color = room.players[1].color;
            color_line = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            cfg.set("p1c", color_line);
            try {
                bind.addPart(new Part("message", cfg));
                for (int i = 0; i < room.units().length; i++) {
                    byte[] unit = room.getUnit(i);

                    bind.addPart(new Part("unit_" + i, unit));
                }
            } catch (JBinDException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return bind;
        }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void in(JBinD data) {
        try{
        JCFG message = data.getPart("message").getDataAsJCFG();
        color = Color.decode("#"+message.get("color").getValueAsString());
        if (room != null) {
            room.players[player].color = color;
            Order[] orders = new Order[message.get("orders").getValueAsInteger()];
            for (int i = 0; i < orders.length; i++) {
                orders[i] = (Order) SerializationUtils.deserialize(data.getPart("order_" + i).getDataAsByteArray());
            }
            room.setOrders(orders);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
