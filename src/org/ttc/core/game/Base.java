package org.ttc.core.game;

import org.newdawn.slick.Graphics;
import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import static org.ttc.core.game.Unit.*;

/**
 *
 * @author yew_mentzaki
 */
public class Base {

    int x, y;
    int power = 255;
    Player owner;
    Room room;

    public Base(Room room, int x, int y, Player owner) {
        this.room = room;
        this.x = x;
        this.y = y;
        this.owner = owner;
    }

    public void tick() {
        for (Unit unit : room.units()) {
            double d = sqrt(pow(unit.x - x, 2) + pow(unit.y - y, 2));
            if (d < 160) {
                if (unit.owner != owner) {
                    if (power > 0) {
                        power--;
                    } else {
                        owner = unit.owner;
                    }
                }
                if (unit.owner == owner) {
                    if (power < 255) {
                        power++;
                    }
                }
            }
        }
    }

    public final void render(Graphics g) {
        glTranslated(x, y, 0);
        base.draw(-base.getWidth() / 4, -base.getHeight() / 4, base.getWidth() / 2, base.getHeight() / 2);
        float a = (float) power / 255;
        if (owner != null) {
            baseColor.setImageColor(a * owner.color.r, a * owner.color.g, a * owner.color.b);
        }else{
            baseColor.setImageColor(0, 0, 0);
        }
        baseColor.draw(-base.getWidth() / 4, -base.getHeight() / 4, base.getWidth() / 2, base.getHeight() / 2);
        glTranslated(-x, -y, 0);
    }

}
