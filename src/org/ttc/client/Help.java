package org.ttc.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author yew_mentzaki
 */
public class Help {

    private static Image exmaples[] = new Image[4];
    private static Image tank, border;

    public static void load() {
        try {
            for (int i = 0; i < exmaples.length; i++) {

                exmaples[i] = new Image("textures/examples/example" + (i + 1) + ".png");

            }
            tank = new Image("textures/examples/icon.png");
            border = new Image("textures/examples/border.png");
        } catch (SlickException ex) {
            Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    static int slide = 1;
    static boolean space = false;

    public static void render(Graphics g) {
        if(space&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            slide++;
            if(slide==exmaples.length+1){
                slide = 0;
            }
        }
        space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        if (slide > 0) {
            g.setColor(new Color(0, 0, 0, 0.5f));
            g.fillRect(0, Display.getHeight() / 2 + 20, Display.getWidth(), Display.getHeight() / 2 - 20);
            border.draw(0, Display.getHeight() / 2, Display.getWidth(), 20);
            tank.draw(80, Display.getHeight() / 2 - 120);
            V2.fontRender.drawString(V2.localeText("example"+slide), 50, Display.getHeight() / 2 + 60, Color.white, Display.getWidth() - 450, true);
            exmaples[slide-1].draw(Display.getWidth()-350, Display.getHeight() / 2);
            V2.fontRender.drawString(V2.localeText("space"), Display.getWidth() - V2.fontRender.getWidth(V2.localeText("space"))  - 50, Display.getHeight() - 40, Color.white);
        }
    }
}
