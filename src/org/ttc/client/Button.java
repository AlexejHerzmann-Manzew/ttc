
package org.ttc.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import static org.ttc.client.V2.gui;

/**
 *
 * @author Юрий Whizzpered
 */
public abstract class Button {

        int x, y, w, wp = 1;
        String text;
        Color color;
        boolean bp;
        V2 v2 = new V2();

        public Button(int x, int y, int w, String text, Color c) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.text = text;
            this.color = c;
        }
        public void render() {
            int x = this.x + Display.getWidth() / 2;
            int y = this.y;
            int w = this.w + wp;
            gui[0].setImageColor(color.r, color.g, color.b);
            gui[1].setImageColor(color.r, color.g, color.b);
            gui[2].setImageColor(color.r, color.g, color.b);
            gui[0].draw(x - w / 2 - 16, y);
            gui[1].draw(x - w / 2, y, w, 50);
            gui[2].draw(x + w / 2, y);
            String text = v2.locales[v2.currentLocale].get(this.text).getValueAsString();
            v2.fontRender.drawString(text, x - v2.fontRender.getWidth(text) / 2, y + 3, Color.black);
            int mx = Mouse.getX();
            int my = Display.getHeight() - Mouse.getY();

            if (Math.abs(mx - x) < (w + 32) / 2 && Math.abs((y + 25) - my) < 25) {
                if (wp < 16) {
                    wp *= 2;
                }
                if (bp && !Mouse.isButtonDown(0)) {
                    click();
                }
            } else {
                if (wp > 1) {
                    wp /= 2;
                }
            }
            bp = Mouse.isButtonDown(0);
        }

        abstract public void click();
    }
