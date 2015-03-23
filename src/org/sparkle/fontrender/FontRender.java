/*
 * Copyright (C) 2014 yew_mentzaki
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sparkle.fontrender;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 *
 * @author yew_mentzaki
 */
public final class FontRender {

    private final Texture letter[] = new Texture[Short.MAX_VALUE];
    private final Font f;
    private static ArrayList<FontRender> textRender = new ArrayList<FontRender>();

    public static FontRender getTextRender(String name, int style, int size) {
        return getTextRender(new Font(name, style, size));
    }

    public static FontRender getTextRender(Font f) {
        for (FontRender text : textRender) {
            if (text.f.getName().equals(f.getName()) && text.f.getStyle() == f.getStyle() && text.f.getSize() == f.getSize()) {
                return text;
            }
        }
        return new FontRender(f);
    }

    private FontRender(Font f) {
        this.f = f;
        init();
        textRender.add(this);
    }

    private void init() {
        getWidth("ABCCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄabcdefghijklmnopqrstuvwxyzüöäß`'[](){}<>:,-‒.?\";/|\\!@#$%^&*_+-*=§АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя");
    }

    private Texture letter(char i) {
        if (letter[(int) i] != null) {
            return letter[(int) i];
        } else {
            /*
             Making empty buffer to get character's width.
             */
            BufferedImage im = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
            Graphics g = im.getGraphics();
            FontMetrics fm = g.getFontMetrics(f);
            int w = fm.charWidth(i);
            int h = fm.getHeight() * 5 / 4;
            /*
             Making the java.awt.BufferedImage with character.
             */
            if (w == 0) {
                w = 1;
            }
            im = new BufferedImage(w, h*2, BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D g2 = (Graphics2D) im.getGraphics();
            g2.setFont(f);
            g2.setColor(java.awt.Color.WHITE);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.drawString(i + "", 0, h);

            {
                /*
                 Converting image to the Texture.
                 */
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(im, "png", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    Texture t = TextureLoader.getTexture("PNG", is);
                    letter[(int) i] = t;
                    return t;
                } catch (IOException ex) {
                    System.out.println("\"" + i + "\" isn't loaded.");
                }
                return null;

            }
        }
    }

    public Font getFont() {
        return f;
    }

    public int getWidth(String text) {
        if(text==null)return 0;
        int width = 0;
        int w = 0;
        for (char c : text.toCharArray()) {
            Texture t = letter(c);

            if (t == null) {
                continue;
            }
            w += t.getImageWidth();
            if (c == '\n') {
                w = 0;
            }
            width = Math.max(w, width);
        }
        return width;
    }

    public void drawString(String text, int x, int y, Color color, int maxWidth, boolean WordWrap) {
        if(text==null)return;
        color.bind();
        int sx = x;
        if (WordWrap) {
            int spacewidth = getWidth(" ");
            for (String tx : text.split(" ")) {
                int wordwidth = getWidth(tx);
                if (x + wordwidth > sx + maxWidth) {
                    y += f.getSize() * 5 / 4;
                    x = sx;
                }
                for (char c : tx.toCharArray()) {
                    Texture t = letter(c);
                    if (t == null) {
                        continue;
                    }
                    if (c == '\n' || x > sx + t.getWidth() + maxWidth) {
                        y += f.getSize() * 5 / 4;
                        x = sx;
                    }
                    t.bind();
                    glBegin(GL_QUADS);
                    glTexCoord2f(0, 0);
                    glVertex2i(x, y);
                    glTexCoord2f(t.getWidth(), 0);
                    glVertex2i(x + t.getImageWidth(), y);
                    glTexCoord2f(t.getWidth(), t.getHeight());
                    glVertex2i(x + t.getImageWidth(), y + t.getImageHeight());
                    glTexCoord2f(0, t.getHeight());
                    glVertex2i(x, y + t.getImageHeight());
                    glEnd();
                    x += t.getImageWidth();
                }
                x += spacewidth;
            }

        } else {
            for (char c : text.toCharArray()) {
                Texture t = letter(c);
                if (t == null) {
                    continue;
                }
                if (c == '\n' || x > sx + t.getWidth() + maxWidth) {
                    y += (int) ((float) f.getSize() * 1.25f);
                    x = sx;
                }
                t.bind();
                glBegin(GL_QUADS);
                glTexCoord2f(0, 0);
                glVertex2i(x, y);
                glTexCoord2f(t.getWidth(), 0);
                glVertex2i(x + t.getImageWidth(), y);
                glTexCoord2f(t.getWidth(), t.getHeight());
                glVertex2i(x + t.getImageWidth(), y + t.getImageHeight());
                glTexCoord2f(0, t.getHeight());
                glVertex2i(x, y + t.getImageHeight());
                glEnd();
                x += t.getImageWidth();
            }
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void drawString(String text, int x, int y, Color color) {
        if(text==null)return;
        color.bind();
        int sx = x;
        for (char c : text.toCharArray()) {
            Texture t = letter(c);
            if (c == '\n') {
                y += f.getSize();
                x = sx;
            }
            if (t == null) {
                continue;
            }
            t.bind();
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2i(x, y);
            glTexCoord2f(t.getWidth(), 0);
            glVertex2i(x + t.getImageWidth(), y);
            glTexCoord2f(t.getWidth(), t.getHeight());
            glVertex2i(x + t.getImageWidth(), y + t.getImageHeight());
            glTexCoord2f(0, t.getHeight());
            glVertex2i(x, y + t.getImageHeight());
            glEnd();
            x += t.getImageWidth();
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
