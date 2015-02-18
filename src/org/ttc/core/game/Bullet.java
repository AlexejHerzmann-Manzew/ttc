package org.ttc.core.game;

import java.io.Serializable;
import org.newdawn.slick.Color;

import static java.lang.Math.*;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author yew_mentzaki
 */
public class Bullet implements Serializable {

    public Bullet(int type, double x, double y, double dx, double dy, Player owner, Room room) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.owner = owner;
        this.room = room;
    }

    double x, y, dx, dy;
    int type;
    Player owner;
    Room room;
    int t = 0, t2 = 255;

    public void render(Graphics g) {
        if (type == 0 | type == 3) {
            glTranslated(x, y, 0);
            explosion[t].setImageColor(1, 1, 1);
            explosion[t].draw(-explosion[t].getWidth() / 8, -explosion[t].getHeight() / 8, explosion[t].getWidth() / 4, explosion[t].getHeight() / 4);
            glTranslated(-x, -y, 0);
        } else if (type == 1) {
            glTranslated(x, y, 0);
            explosion[t].setImageColor(0, 1, 0);
            explosion[t].draw(-explosion[t].getWidth() / 6, -explosion[t].getHeight() / 6, explosion[t].getWidth() / 3, explosion[t].getHeight() / 3);
            glTranslated(-x, -y, 0);
        } else if (type == 2) {
            rainbow.getTexture().bind();
            glColor4f(1, 1, 1, max((float) t2 / 255f, 0));
            glBegin(GL_POLYGON);
            glTexCoord2d(0, 1);
            glVertex2d(x + r.nextInt(5) - 2, y + r.nextInt(5) - 2);
            glTexCoord2d(0, 0);
            glVertex2d(x + r.nextInt(5) - 2, y + r.nextInt(5) - 2);
            glTexCoord2d(1, 0);
            glVertex2d(x + dx * 44 + r.nextInt(61) - 30, y + dy * 45 + r.nextInt(61) - 30);
            glTexCoord2d(1, 1);
            glVertex2d(x + dx * 45 + r.nextInt(61) - 30, y + dy * 44 + r.nextInt(61) - 30);
            glEnd();
        }
    }

    public void tick() {
        if (type == 2) {
            if (t2 == 255) {
                ArrayList<Unit> at = new ArrayList<Unit>();
                for (int i = 0; i < 45; i++) {
                    for (Unit unit : room.units()) {
                        if (at.contains(unit)) {
                            continue;
                        }
                        if (unit.owner != owner && sqrt(pow(unit.x - (x + dx * i), 2) + pow(unit.y - (y + dy * i), 2)) < size / 2) {
                            unit.hp -= damage;
                            at.add(unit);
                            break;
                        }
                    }
                }
            }
            t2 -= 5;
        } else if (type == 1) {
            t2--;
            if (t2 % 2 == 0) {
                return;
            }
            if (t2 <= 0) {
                t += 1;
            }
            if (t > 0 & t < 8) {
                t++;
            } else if (t == 0) {
                x += dx;
                y += dy;
                for (Unit unit : room.units()) {
                    if (unit.owner != owner && sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size / 2) {
                        unit.hp -= damage;
                        t = 1;
                    }
                }
            }
        } else {
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
                    if (unit.owner != owner && sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size / 2) {
                        unit.hp -= damage;
                        t = 1;
                        break;
                    }
                }
            }
        }
    }
}
