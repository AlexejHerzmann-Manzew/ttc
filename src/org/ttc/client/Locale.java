
package org.ttc.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import static org.ttc.client.V2.currentLocale;

/**
 *
 * @author Юрий
 */
public class Locale extends Button {

        Image localeIcons[], smallIcons[], back, small;

        public Locale(int x, int y) {
            super(x, y, 69, null, null);
            try {
                localeIcons = new Image[]{
                    new Image("textures/gui/locale_eng.png"),
                    new Image("textures/gui/locale_deu.png"),
                    new Image("textures/gui/locale_rus.png")
                };
                smallIcons = new Image[]{
                    new Image("textures/gui/locale_eng_small.png"),
                    new Image("textures/gui/locale_deu_small.png"),
                    new Image("textures/gui/locale_rus_small.png")
                };
                back = new Image("textures/gui/locale.png");
                small = new Image("textures/gui/locale_small.png");
            } catch (SlickException ex) {
                Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void render() {

            back.draw(x, y);
            int sec = currentLocale+1;
            if(sec >= 3)sec-=3;
            int thi = currentLocale+2;
            if(thi >= 3)thi-=3;
            smallIcons[sec].draw(x + 4, y + 4, 16, 12);
            smallIcons[thi].draw(x + 4, y + 18, 16, 12);
            small.draw(x + 4, y + 4);
            small.draw(x + 4, y + 18);
            localeIcons[currentLocale].draw(x+wp-1, y);
            

            int mx = Mouse.getX();
            int my = Display.getHeight() - Mouse.getY();

            if (Math.abs((x + wp/2)+35 - mx) < (w + wp) / 2 && Math.abs((y + 25) - my) < 25) {
                if (wp < 16) {
                    wp *= 2;
                }
                if (bp && !Mouse.isButtonDown(0)) {

                    currentLocale++;
                    if (currentLocale >= 3) {
                        currentLocale -= 3;
                    }
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

