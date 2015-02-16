package org.ttc.core.game;

import java.io.Serializable;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

/**
 *
 * @author yew_mentzaki
 */
public class Unit implements Serializable, Cloneable {

    public Unit(Room room, double x, double y, int owner) {
        int base = r.nextInt(8);
        this.owner = room.players[owner];
        for (int i = 0; i < 16 && (room.bases[base].owner != this.owner); i++) {
            base = r.nextInt(8);
        }
        x = room.bases[base].x;
        y = room.bases[base].y;
        this.x = x;
        this.y = y;
        this.tx = x;
        this.ty = y;
        this.hp = maxHp;
        this.room = room;
        a = r.nextDouble() * PI * 2;
        ha = r.nextDouble() * PI * 2;
        ai = r.nextInt(2);
    }

    //Unit's fields:
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

    //Type's fields:
    static public double speed = 2, turnSpeed = 0.05, headTurnSpeed = 0.1;
    static public int reloadTime = 35, reloadAmmoTime = 100, ammoSize = 10, damage = 150, maxHp = 1000, size = 64;
    static public Bullet bulletPrototype;
    static public Image bodyImage, headImage, base, baseColor, explosion[];
    
    static Random r = new Random();
    static int indexounit;
    
    static public void initGraphics() {
        try {
            bodyImage = new Image("textures/body.png");
            headImage = new Image("textures/head.png");
            base = new Image("textures/base.png");
            baseColor = new Image("textures/base_color.png");

            explosion = new Image[10];
            for (int i = 1; i <= 10; i++) {
                explosion[i - 1] = new Image("textures/explosion/f" + i + ".png");
            }
        } catch (SlickException ex) {
            ex.printStackTrace();
        }
    }

    public void tick() {

        if (hp > 0) {
            live();
            move();
            turn();
            attack();
            ai();
        } else {
            death();
        }
    }

    public void death() {
        if (timer == 255) {
            timer = 0;
        } else if (timer < 30) {
            timer++;
        } else if (timer == 30) {

            int base = r.nextInt(8);
            for (int i = 0; i < 16 && (room.bases[base].owner != owner); i++) {
                base = r.nextInt(8);
                if (i == 15) {
                    return;
                }
            }
            hp = 1;
            x = room.bases[base].x;
            y = room.bases[base].y;
            tx = r.nextInt(300) - 150;
            ty = r.nextInt(300) - 150;
            ai = r.nextInt(2);
        }
    }

    public void ai() {
        if (ai == 0) {
            double dist = 5000;
            Base base = null;
            for (Base b : room.bases) {
                if (b.owner == owner) {
                    continue;
                }
                double d = sqrt(pow(b.x - x, 2) + pow(b.y - y, 2));
                if (d < dist) {
                    base = b;
                    dist = d;
                }
            }
            if (base != null) {
                tx = base.x;
                ty = base.y;
            }
        } else {
            for (Unit unit : room.units()) {
                double d = sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2));
                if ((target == null || d < td) & unit.hp > 0 && unit.owner != owner) {
                    target = unit;
                    tx = unit.x;
                    ty = unit.y;
                    td = d;
                }
            }
        }
    }

    public void live() {
        if (timer < 255) {
            timer++;
            hp = maxHp;
        }
        if (hp < maxHp) {
            hp++;
        }
        for (Unit unit : room.units()) {
            double d = sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2));
            if (d < size) {
                double a = atan2(unit.y - y, unit.x - x);
                unit.x += cos(a) * (size - d) / 2;
                unit.y += sin(a) * (size - d) / 2;
                x -= cos(a) * (size - d) / 2;
                y -= sin(a) * (size - d) / 2;
            }
            if (unit == target) {
                td = d;
                if (d > 600 || hp <= 0) {
                    target = null;
                }
            }
            if (unit.timer == 255 && (target == null || d < td) && unit.hp > 0 && d <= 600 && unit.owner != owner) {
                target = unit;
                td = d;
            }
        }
    }

    public void move() {
        if (abs(x - tx) > size | abs(y - ty) > size) {
            x += cos(a) * speed;
            y += sin(a) * speed;
        }
    }

    public void turn() {
        ta = atan2(ty - y, tx - x);
        if (a < -PI) {
            a += 2 * PI;
        }
        if (a > +PI) {
            a -= 2 * PI;
        }
        if (a != ta) {
            if (abs(ta - a) > turnSpeed) {
                int v = (abs(ta - a) <= 2 * PI - abs(ta - a)) ? 1 : -1;

                if (ta < a) {
                    a -= turnSpeed * v;
                } else if (ta > a) {
                    a += turnSpeed * v;
                }
            } else {
                a = ta;
            }
        }
    }

    public void attack() {
        if (target == null) {
            hta = ta;
        } else {
            hta = atan2(target.y - y, target.x - x);
        }

        if (ha < -PI) {
            ha += 2 * PI;
        }
        if (ha > +PI) {
            ha -= 2 * PI;
        }
        if (ha != hta) {
            if (abs(hta - ha) > headTurnSpeed) {
                int v = (abs(hta - ha) <= 2 * PI - abs(hta - ha)) ? 1 : -1;

                if (hta < ha) {
                    ha -= headTurnSpeed * v;
                } else if (hta > a) {
                    ha += headTurnSpeed * v;
                }
            } else {

                ha = hta;
            }
        }
        if (ha == hta && target != null) {
            if (reload == 0) {
                room.bullets.add(new Bullet(x, y, cos(ha) * 10, sin(ha) * 10, owner, room));
                reload = reloadTime;
            }
            reload--;
        }

    }

    public final void render(Graphics g) {
        glTranslated(x, y, 0);

        if (hp < maxHp & hp > 0) {
            g.setColor(Color.green);
            g.fillRect(-bodyImage.getWidth() / 4, -bodyImage.getWidth() / 4 - 5, (float) (bodyImage.getWidth() / 2) * (float) ((float) hp / (float) maxHp), 5);

            g.setColor(Color.black);
            g.drawRect(-bodyImage.getWidth() / 4, -bodyImage.getWidth() / 4 - 5, bodyImage.getWidth() / 2, 5);
        }
        glRotated((a) / PI * 180, 0, 0, 1);
        if (hp <= 0 && timer < 30) {
            explosion[timer / 3].draw(-explosion[timer / 30].getWidth() / 4, -explosion[timer / 30].getHeight() / 4, explosion[timer / 30].getWidth() / 2, explosion[timer / 30].getHeight() / 2);
        }
        if (hp > 0) {
            renderBody(g);
        }
        glRotated((ha - a) / PI * 180, 0, 0, 1);
        if (hp > 0) {
            renderHead(g);
        }
        glRotated((-ha) / PI * 180, 0, 0, 1);

        glTranslated(-x, -y, 0);
    }

    public void renderBody(Graphics g) {
        //Setting teamcolor:
        bodyImage.setImageColor(owner.color.r, owner.color.g, owner.color.b, (float) timer / 255f);
        bodyImage.draw(-bodyImage.getWidth() / 4, -bodyImage.getHeight() / 4, bodyImage.getWidth() / 2, bodyImage.getHeight() / 2);
    }

    public void renderHead(Graphics g) {
        headImage.setImageColor(owner.color.r, owner.color.g, owner.color.b, (float) timer / 255f);
        headImage.draw(-bodyImage.getWidth() / 4, -bodyImage.getHeight() / 4, bodyImage.getWidth() / 2, bodyImage.getHeight() / 2);
    }

}
