package org.ttc.core.game;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.glTranslated;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author Юрий
 */
public class Acid extends Bullet{
    public Acid( double x, double y, double dx, double dy, Player owner, Room room) {
        super(x,y,dx,dy,owner,room);
    }
    
    @Override
    public void render(Graphics g){
        glTranslated(x, y, 0);
            explosion[t].setImageColor(0, 1, 0);
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
                for (Unit unit : room.units()) {
                    if (sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size) {
                        unit.hp -= damage / 4;
                    }
                }
            } else if (t == 0) {
                x += dx;
                y += dy;
                for (Unit unit : room.units()) {
                    if (unit.owner != owner && sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size / 2) {
                        unit.hp -= damage * 3;
                        t = 1;
                    }
                }
            }
    }
}
