package org.ttc.core.game;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.glTranslated;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.damage;
import static org.ttc.core.game.Unit.explosion;
import static org.ttc.core.game.Unit.size;

/**
 *
 * @author Whizzpered
 */
public class Plazma extends Bullet{
    
    public Plazma(double x, double y, double dx, double dy, Player target, Room room){
        super(x,y,dx,dy,target,room);
    }
    
    @Override
    public void render(Graphics g) {
        glTranslated(x, y, 0);
            explosion[t].setImageColor(0, 1f, 1);
            explosion[t].draw(-explosion[t].getWidth() / 6, -explosion[t].getHeight() / 6, explosion[t].getWidth() / 3, explosion[t].getHeight() / 3);
        glTranslated(-x, -y, 0);
    }
    
    @Override
    public void tick() {
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
                Unit target = null;
                double dist = 10000;
                for (Unit unit : room.units()) {
                    if (unit.owner != owner && unit.hp > 0) {
                        double distance = sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2));
                        if (distance < size / 2) {
                            unit.hp -= damage;
                            t = 1;
                            break;
                        } else if (distance < dist) {
                            dist = distance;
                            target = unit;
                        }
                    }
                }
                if (target != null) {
                    double a = atan2(target.y - y, target.x - x);
                    dx = (dx * 12 + (cos(a) * 12))/13;
                    dy = (dy * 12 + (sin(a) * 12))/13;
                }
            }
    }
}
