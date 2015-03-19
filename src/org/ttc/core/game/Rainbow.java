package org.ttc.core.game;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author Whizzpered
 */
public class Rainbow extends Bullet{
    
    public Rainbow(double x, double y, double dx, double dy, Player target, Room room){
        super(x,y,dx,dy,target,room);
    }
    
    @Override
    public void render(Graphics g) {
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
    
    @Override
    public void tick() {
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
    }
}
