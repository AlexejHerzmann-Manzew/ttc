
package org.ttc.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import static org.ttc.client.V2.gui;

/**
 *
 * @author Юрий
 */
public class Switcher  extends Button {

        public boolean value;
        int wp = 1;
        Image picture[];
        boolean bp;
        int sx;

        public Switcher(int x, int y, boolean value, String picture) {
            super(x, y, 0, "", null);
            try {
                this.x = x;
                this.y = y;
                if (value) {
                    this.color = new Color(0, 255, 0);
                    sx = 52;
                } else {
                    this.color = new Color(255, 0, 0);
                    sx = 0;
                }
                this.picture = new Image[2];
                this.value = value;

                this.picture[0] = new Image(picture + "_enabled.png");
                this.picture[1] = new Image(picture + "_disabled.png");

            } catch (Exception ex) {
                Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void render() {
            int x = this.x + Display.getWidth() / 2;
            int y = this.y;
            int w = 100;

            if (value) {
                if (color.r - 0.1f >= 0) {
                    color.r -= 0.1f;
                    color.g += 0.1f;
                }
                sx = (52 + sx + sx) / 3;
            } else {
                if (color.r + 0.1f <= 1) {
                    color.r += 0.1f;
                    color.g -= 0.1f;
                }
                sx = (0 + sx + sx) / 3;
            }

            gui[4].setImageColor(color.r, color.g, color.b);
            gui[4].draw(x - w / 2, y);
            picture[0].draw(x - w / 2 + 25 - picture[0].getWidth() / 2, y + 25 - picture[0].getHeight() / 2);
            picture[1].draw(x + w / 2 - 25 - picture[0].getWidth() / 2, y + 25 - picture[0].getHeight() / 2);
            gui[3].draw(x - w / 2 + sx, y);
            int mx = Mouse.getX();
            int my = Display.getHeight() - Mouse.getY();

            if (Math.abs(mx - x) < w / 2 && Math.abs((y + 25) - my) < 25) {
                if (wp < 16) {
                    wp *= 2;
                }
                if (bp && !Mouse.isButtonDown(0)) {
                    value = !value;
                    click();
                }
            } else {
                if (wp > 1) {
                    wp /= 2;
                }
            }
            bp = Mouse.isButtonDown(0);
        }

        @Override
        public void click() {

        }
    }
