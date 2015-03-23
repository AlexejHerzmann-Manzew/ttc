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

    public Unit(Room room, double x, double y, Base owner) {
        this.base = owner;
        this.owner = this.base.owner;
        x = this.base.x;
        y = this.base.y;
        this.x = x;
        this.y = y;
        this.tx = x;
        this.ty = y;
        this.hp = maxHp;
        this.room = room;
        this.index = room.serialnumber++;
        a = r.nextDouble() * PI * 2;
        ha = r.nextDouble() * PI * 2;
        ai = r.nextInt(2);
        type = r.nextInt(4);
    }

    //Unit's fields:
    public int index; //serial number
    public double x, y, //coords
            tx, ty, //target coords
            a, ta, //angle, delta angle, target angle
            ha, hta, //head angle etc.
            td; //target distance
    public int reload, ammo, hp, timer, ai, type;
    public transient int lagg; //laggs' level
    public transient Player owner; //owner of this unit
    public transient Unit target; //enemy
    public transient Room room; //game room
    public transient Base base; //unit base
    //Type's fields:
    static public double speed = 2, turnSpeed = 0.05, headTurnSpeed = 0.1;
    static public int reloadTime = 35, reloadAmmoTime = 100, ammoSize = 10, damage = 150, maxHp = 1000, size = 64;
    static public Bullet bulletPrototype;
    static public Image bodyImage, iconImage[], headImage[], head2Image[], baseImage, baseColor, explosion[];
    static public Image rainbow, basePicture, tankPicture;

    static Random r = new Random();

    static public void initGraphics() {
        try {
            bodyImage = new Image("textures/body.png");
            basePicture = new Image("textures/base_picture.png");
            tankPicture = new Image("textures/tank_picture.png");
            headImage = new Image[4];
            head2Image = new Image[4];
            iconImage = new Image[6];
            rainbow = new Image("textures/rainbow.png");
            headImage[0] = new Image("textures/head.png");
            headImage[1] = new Image("textures/head_acid.png");
            headImage[2] = new Image("textures/head_rainbow.png");
            headImage[3] = new Image("textures/head_tesla.png");
            iconImage[0] = new Image("textures/icon_random.png");
            iconImage[1] = new Image("textures/icon.png");
            iconImage[2] = new Image("textures/icon_acid.png");
            iconImage[3] = new Image("textures/icon_rainbow.png");
            iconImage[4] = new Image("textures/icon_tesla.png");
            iconImage[5] = new Image("textures/icon_arrow.png");
            head2Image[0] = new Image("textures/head2.png");
            head2Image[1] = new Image("textures/head2_acid.png");
            head2Image[2] = new Image("textures/head2_rainbow.png");
            head2Image[3] = new Image("textures/head2_tesla.png");
            Room.grass = new Image("textures/grass.png");

            baseImage = new Image("textures/base.png");
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
        owner = base.owner;
        if (hp > 0) {
            live();
            if (timer >= 255) {
                move();
                turn();
                attack();
                if (owner.ai) {
                    ai();
                }
            }
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
            hp = maxHp;
            x = this.base.x + r.nextInt(300) - 150;
            y = this.base.y + r.nextInt(300) - 150;
            tx = x;
            ty = y;
            a = r.nextDouble() * PI * 2;
            ha = r.nextDouble() * PI * 2;
            type = owner.type();
            ai = r.nextInt(2);
        }
    }

    public void ai() {
        if (r.nextInt(100) == 0) {
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
                if (unit.owner == owner) {
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
                switch (type) {
                    case (0):
                        room.bullets.add(new Shell(x, y, cos(ha) * 10, sin(ha) * 10, owner, room));
                        break;
                    case (1):
                        room.bullets.add(new Acid(x, y, cos(ha) * 10, sin(ha) * 10, owner, room));
                        break;
                    case (2):
                        room.bullets.add(new Rainbow(x, y, cos(ha) * 10, sin(ha) * 10, owner, room));
                        break;
                    case (3):
                        room.bullets.add(new Plazma(x, y, cos(ha) * 10, sin(ha) * 10, owner, room));
                        break;

                }
                reload = reloadTime;
            }
            reload--;
        }

    }

    public final void render(Graphics g) {
        double x = this.x, y = this.y, a = this.a, ha = this.ha;
        glTranslated(x, y, 0);

        if (hp < maxHp & hp > 0) {
            g.setColor(Color.green);
            g.fillRect(-bodyImage.getWidth() / 4, -bodyImage.getWidth() / 4 - 5, (float) (bodyImage.getWidth() / 2) * (float) ((float) hp / (float) maxHp), 5);

            g.setColor(Color.black);
            g.drawRect(-bodyImage.getWidth() / 4, -bodyImage.getWidth() / 4 - 5, bodyImage.getWidth() / 2, 5);
        }
        glRotated((a) / PI * 180, 0, 0, 1);
        if (hp <= 0 && timer < 30) {
            explosion[timer / 3].setImageColor(1, 0.5f, 0.0f);
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

    public final void renderInterface(Graphics g) {
        double x = this.x, y = this.y, a = this.a;
        glTranslated(x / 20 + 75, y / 20 + 75, 0);
        glRotated(a / PI * 180 + 45, 0, 0, 1);
        tankPicture.setImageColor(owner.color.r, owner.color.g, owner.color.b, max(min((float) timer / 255f, 255), 0));
        tankPicture.draw(-tankPicture.getWidth() / 4, -tankPicture.getHeight() / 4, tankPicture.getWidth() / 2, tankPicture.getHeight() / 2);
        glRotated(a / PI * 180 + 45, 0, 0, -1);
        glTranslated(-x / 20 - 75, -y / 20 - 75, 0);
    }

    public void renderBody(Graphics g) {
        //Setting teamcolor:
        bodyImage.setImageColor(owner.color.r, owner.color.g, owner.color.b, (float) timer / 255f);
        bodyImage.draw(-bodyImage.getWidth() / 4, -bodyImage.getHeight() / 4, bodyImage.getWidth() / 2, bodyImage.getHeight() / 2);
    }

    public void renderHead(Graphics g) {
        headImage[type].setImageColor(owner.color.r, owner.color.g, owner.color.b, (float) timer / 255f);
        headImage[type].draw(-bodyImage.getWidth() / 4, -bodyImage.getHeight() / 4, bodyImage.getWidth() / 2, bodyImage.getHeight() / 2);
        head2Image[type].draw(-bodyImage.getWidth() / 4, -bodyImage.getHeight() / 4, bodyImage.getWidth() / 2, bodyImage.getHeight() / 2);
    }

}
