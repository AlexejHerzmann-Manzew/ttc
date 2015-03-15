package org.ttc.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.newdawn.slick.Color;
import org.sparkle.jbind.JBinD;
import org.sparkle.jbind.JBinDException;
import org.sparkle.jbind.Part;
import org.sparkle.jcfg.JCFG;
import org.ttc.core.game.*;
import org.ttc.core.server.ServerHandler;

/**
 *
 * @author yew_mentzaki
 */
public class ClientHandler extends org.sparkle.janette.client.ClientHandler {

    public ClientHandler() {
    }

    public JBinD out() {
        JBinD bind = new JBinD();
        JCFG cfg = new JCFG();
        Color color = Color.decode(V2.conf.get("color").getValueAsString());
        cfg.set("color", String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
        cfg.set("orders", 0);
        if (V2.room == null) {
            try {
                bind.addPart(new Part("message", cfg));
            } catch (JBinDException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return bind;
        } else {
            Order[] orders = V2.room.getOrders();
            cfg.set("orders", orders.length);
            try {
                bind.addPart(new Part("message", cfg));
                for (int i = 0; i < orders.length; i++) {
                    bind.addPart(new Part("order_" + i, SerializationUtils.serialize(orders[i])));
                }
            } catch (JBinDException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return bind;
        }
    }
    
    @Override
    public void in(JBinD data) {
        JCFG message = data.getPart("message").getDataAsJCFG();
        if (message.get("status").getValueAsString().equals("waiting")) {
            return;
        } else if (message.get("status").getValueAsString().equals("playing")) {
            if (V2.room == null) {
                V2.room = new Room(2);
                V2.waiting = false;
                System.out.println("Created room");
            }
            V2.room.player = message.get("player").getValueAsInteger();
            V2.room.players[0].color = Color.decode("#"+message.get("p0c").getValueAsString());
            V2.room.players[1].color = Color.decode("#"+message.get("p1c").getValueAsString());
            V2.room.players[0].ai = false;
            V2.room.players[1].ai = false;
            for (int i = 0; i < V2.room.units().length; i++) {
                V2.room.setUnit(i, data.getPart("unit_" + i).getDataAsByteArray());
            }
        }
    }
    
}
