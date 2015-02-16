package org.ttc.core.game;

import java.io.Serializable;
import org.newdawn.slick.Color;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author yew_mentzaki
 */
public class Bullet implements Serializable {

    public Bullet(double x, double y, double dx, double dy, Player owner, Room room) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.owner = owner;
        this.room = room;
    }

    double x, y, dx, dy;
    Player owner;
    Room room;
    int t = 0, t2 = 255;

    public void render(Graphics g) {
        glTranslated(x, y, 0);
        
        explosion[t].draw(-explosion[t].getWidth() / 8, -explosion[t].getHeight() / 8, explosion[t].getWidth() / 4, explosion[t].getHeight() / 4);

        glTranslated(-x, -y, 0);
    }

    public void tick() {
        t2--;
        if (t2 <= 0) {
            t += 1;
        }
        if (t > 0 & t < 8) {
            t++;
        } else if (t == 0) {
            x += dx;
            y += dy;
            for (Unit unit : room.units()) {
                if (unit.owner != owner && sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size/2) {
                    unit.hp -= damage;
                    t = 1;
                    break;
                }
            }
        }
    }
}
