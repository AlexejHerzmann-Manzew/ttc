package org.ttc.core.game;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
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
    
    public byte[] getUnit(int index){
        return SerializationUtils.serialize(units()[index]);
    }
    
    public void setUnit(byte[] bytes){
        Unit unit = (Unit)SerializationUtils.deserialize(bytes);
        for (Unit u : units()) {
            if(u.index==unit.index){
               /*
                public int index = indexounit++; //serial number
    public double x, y, //coords
            dx, dy, //delta coords (x2 - x1)
            tx, ty, //target coords
            a, da, ta, //angle, delta angle, target angle
            ha, hda, hta, //head angle etc.
            td; //target distance
    public int reload, ammo, hp, timer, ai;
    public transient Player owner; //owner of this unit
    public transient Unit target; //enemy
    public transient Room room; //game room
                       */
                u.x = unit.x;
                u.y = unit.y;
                u.dx = unit.dx;
                u.dy = unit.dy;
                u.tx = unit.tx;
                u.ty = unit.ty;
                u.a = unit.a;
                u.ta = unit.ta;
                u.da = unit.da;
                u.ha = unit.ha;
                u.hta = unit.hta;
                u.hda = unit.hda;
                u.reload = unit.reload;
                u.ammo = unit.ammo;
                u.hp = unit.hp;
                u.timer = unit.timer;
                u.ai = unit.ai;
                break;
            }
        }
    }

    public Room(int players) {
        for (int i = 0; i < 8; i++) {
            bases[i] = new Base(this, (int)(cos((float) i / 4 * Math.PI)*1000), (int)(sin((float) i / 4 * Math.PI)*1000), null);
        }
        for (int i = 0; i < players; i++) {
            this.players[i] = new Player("none", 0);
        }
        for (int i = 0; i < players; i++) {
            for (int j = 0; j < 15; j++) {
                units.add(new Unit(this, 0, 0, i));
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
                base.tick();
                base.render(g);
            }
            for (Unit unit : units()) {
                unit.tick();
                unit.render(g);
            }
            for (Bullet bullet : bullets()) {
                bullet.tick();
                bullet.render(g);
            }
            for (Bullet bullet : bullets()) {
                if (bullet.t >= 8) {
                    bullets.remove(bullet);
                }
            }
            GL11.glTranslated(cx, cy, 0);

        } catch (Exception e) {
        }
    }

}
