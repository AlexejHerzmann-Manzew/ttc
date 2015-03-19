package org.ttc.core.game;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.lwjgl.opengl.GL11.glTranslated;
import org.newdawn.slick.Graphics;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author Whizzpered
 */
public class Shell extends Bullet{
    
    public Shell(double x, double y, double dx, double dy, Player target, Room room){
        super(x,y,dx,dy,target,room);
    }
    
    @Override
    public void render(Graphics g){
        glTranslated(x, y, 0);
            explosion[t].setImageColor(1, 0.5f, 0);
            explosion[t].draw(-explosion[t].getWidth() / 8, -explosion[t].getHeight() / 8, explosion[t].getWidth() / 4, explosion[t].getHeight() / 4);
        glTranslated(-x, -y, 0);
    }
    
    @Override
    public void tick(){
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
                    if (unit.owner != owner && unit.hp > 0 && sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2)) < size / 2) {
                        unit.hp -= damage;
                        t = 1;
                        break;
                    }
                }
            }
    }}
