package org.ttc.core.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import jdk.nashorn.internal.runtime.regexp.joni.EncodingHelper;
import org.apache.commons.lang.SerializationUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import static org.ttc.core.game.Unit.indexounit;

/**
 *
 * @author yew_mentzaki
 */
public class Room {

    ArrayList<Unit> units = new ArrayList<Unit>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    Base[] bases = new Base[8];
    public Player[] players = new Player[8];
    Image grass;
    public int player;

    public Unit[] units() {
        Unit[] u = new Unit[units.size()];
        try {
            for (int i = 0; i < units.size(); i++) {
                u[i] = units.get(i);
            }
        } catch (Exception e) {
        }
        return u;
    }

    public Bullet[] bullets() {
        Bullet[] b = new Bullet[bullets.size()];
        try {
            for (int i = 0; i < bullets.size(); i++) {
                b[i] = bullets.get(i);
            }
        } catch (Exception e) {
        }
        return b;
    }

    public byte[] getUnit(int index) {
        return SerializationUtils.serialize(units()[index]);
    }

    public boolean setUnit(byte[] bytes) {
        Unit unit = (Unit) SerializationUtils.deserialize(bytes);
        for (Unit u : units()) {
            if (u.index == unit.index) {
                /*
                 if ((abs(u.x - unit.x) > 50 && abs(u.y - unit.y) > 50)) {
                 if (u.lagg < 3) {
                 u.lagg++;
                 System.out.println("X: " + abs(u.x - unit.x));
                 System.out.println("Y: " + abs(u.y - unit.y));
                 return false;
                 } else {
                 u.lagg = 0;
                 }
                 if (u.timer > 100) {
                 return false;
                 }
                 }*/
                u.x = unit.x;
                u.y = unit.y;
                u.tx = unit.tx;
                u.ty = unit.ty;
                u.a = unit.a;
                u.ta = unit.ta;
                u.ha = unit.ha;
                u.hta = unit.hta;
                u.type = unit.type;
                u.reload = unit.reload;
                u.ammo = unit.ammo;
                u.hp = unit.hp;
                u.timer = unit.timer;
                u.ai = unit.ai;
                u.lagg = 0;
                return true;
            }
        }
        return false;
    }

    public Room(int players) {
        for (int i = 0; i < players; i++) {
            this.players[i] = new Player("none", 0);
        }
        for (int i = 0; i < 8; i++) {
            bases[i] = new Base(this, (int) (cos((float) i / 4 * Math.PI) * 1000), (int) (sin((float) i / 4 * Math.PI) * 1000), this.players[i / (8 / players)]);
        }

        for (int i = 0; i < players; i++) {
            for (int j = 0; j < 15; j++) {
                Unit unit = new Unit(this, 0, 0, i);
                units.add(unit);
                if(j==0){
                    this.players[i].camerax = (int) (unit.x - 400);
                    this.players[i].cameray = (int) (unit.y - 300);
                }
            }
        }
        t.start();
    }
    public Timer t = new Timer(10, new ActionListener() {

        public void actionPerformed(ActionEvent e) {

            for (Base base : bases) {
                base.tick();
            }
            for (Unit unit : units()) {
                unit.tick();
            }
            for (Bullet bullet : bullets()) {
                bullet.tick();

            }
            for (Bullet bullet : bullets()) {
                if (bullet.t >= 8|bullet.t2 <= 0) {
                    bullets.remove(bullet);
                }
            }
        }
    });

    public Order[] getOrders(){
        Order[] order = new Order[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            Unit u = selected.get(i);
            order[i] = new Order(u.index, (int)u.tx, (int)u.ty);
        }
        return order;
    }
    public void setOrders(Order[] orders){
        for (Order o : orders) {
            for (Unit u : units()) {
                if(u.index == o.uid){
                    u.tx = o.tx;
                    u.ty = o.ty;
                }
            }
        }
    }
    
    public void initGraphics() {
        try {
            grass = new Image("textures/grass.png");

        } catch (SlickException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
        Unit.initGraphics();
    }
    private Graphics g = new Graphics();
    private ArrayList<Unit> selected = new ArrayList<Unit>();

    public void render(int player) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
            players[player].camerax -= 5;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
            players[player].camerax += 5;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
            players[player].cameray -= 5;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
            players[player].cameray += 5;
        }

        try {
            int cx = players[player].camerax;
            int cy = players[player].cameray;

            for (int x = cx / 128 - 1; x <= cx / 128 + Display.getWidth() / 128 + 1; x += 1) {
                for (int y = cy / 128 - 1; y <= cy / 128 + Display.getHeight() / 128 + 1; y += 1) {
                    grass.draw(x * 128 - cx, y * 128 - cy);
                }
            }

            GL11.glTranslated(-cx, -cy, 0);

            for (Base base : bases) {
                base.render(g);
            }
            if (Mouse.isButtonDown(1)) {
                players[player].camerax -= Mouse.getDX();
                players[player].cameray += Mouse.getDY();
            }
            if (!Mouse.isButtonDown(0)) {
                selected.clear();
            }
            int mx = players[player].camerax + Mouse.getX();
            int my = players[player].cameray + Display.getHeight() - Mouse.getY();
            for (Unit unit : units()) {
                unit.render(g);
                if (unit.owner == players[player] && Mouse.isButtonDown(0) && !selected.contains(unit)) {

                    if (sqrt(pow(unit.x - mx, 2) + pow(unit.y - my, 2)) < 100) {
                        selected.add(unit);
                    }
                }
                if (unit.timer < 10 && selected.contains(unit)) {
                    selected.remove(unit);
                }
            }
            for (Unit unit : selected) {
                g.setColor(Color.yellow);
                g.drawLine(mx, my, (int) unit.x, (int) unit.y);
                unit.tx = mx;
                unit.ty = my;
            }
            for (Bullet bullet : bullets()) {
                bullet.render(g);
            }
            GL11.glTranslated(cx, cy, 0);

        } catch (Exception e) {
        }
    }

}
