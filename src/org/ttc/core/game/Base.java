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
            if(unit.hp < 0)continue;
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
            if (d < 320) {
                if (unit.owner == owner) {
                    if (unit.hp < Unit.maxHp) {
                        unit.hp++;
                    }
                }
            }
        }
    }

    public final void render(Graphics g) {
        glTranslated(x, y, 0);
        baseImage.draw(-baseImage.getWidth() / 4, -baseImage.getHeight() / 4, baseImage.getWidth() / 2, baseImage.getHeight() / 2);
        float a = (float) power / 255;
        if (owner != null) {
            baseColor.setImageColor(a * owner.color.r, a * owner.color.g, a * owner.color.b);
        } else {
            baseColor.setImageColor(0, 0, 0);
        }
        baseColor.draw(-baseImage.getWidth() / 4, -baseImage.getHeight() / 4, baseImage.getWidth() / 2, baseImage.getHeight() / 2);
        glTranslated(-x, -y, 0);
    }
    public final void renderInterface(Graphics g){
        double x = this.x, y = this.y;
        glTranslated(x/20+75, y/20+75, 0);
        float a = (float) power / 255;
        if (owner != null) {
            basePicture.setImageColor(a * owner.color.r, a * owner.color.g, a * owner.color.b);
        } else {
            basePicture.setImageColor(0, 0, 0);
        }
        basePicture.draw(-basePicture.getWidth() / 4, -basePicture.getHeight() / 4, basePicture.getWidth() / 2, basePicture.getHeight() / 2);
        glTranslated(-x/20-75, -y/20-75, 0);
    }

}
