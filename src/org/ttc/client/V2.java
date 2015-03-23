package org.ttc.client;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.sparkle.fontrender.FontRender;
import org.sparkle.janette.client.ClientConnection;
import org.sparkle.jcfg.JCFG;
import org.sparkle.jcfg.Parser;
import org.sparkle.jcfg.Writer;
import org.ttc.core.game.Room;
import org.ttc.core.game.Unit;

/**
 *
 * @author yew_mentzaki & whizzpered
 */
public class V2 {
    public static String localeText(String text){
        return locales[currentLocale].get(text).getValueAsString();
    }
    private static abstract class button {

        int x, y, w, wp = 1;
        String text;
        Color color;
        boolean bp;

        public button(int x, int y, int w, String text, Color c) {
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
            String text = locales[currentLocale].get(this.text).getValueAsString();
            fontRender.drawString(text, x - fontRender.getWidth(text) / 2, y + 3, Color.black);
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
    private static int currentLocale;
    private static JCFG locales[] = new JCFG[3];
    static int escTimer = 0;

    private static class locale extends button {

        Image localeIcons[], smallIcons[], back, small;

        public locale(int x, int y) {
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

    private static class switcher extends button {

        public boolean value;
        int wp = 1;
        Image picture[];
        boolean bp;
        int sx;

        public switcher(int x, int y, boolean value, String picture) {
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

    public static int display_width, display_height;
    static Room room;
    static FontRender fontRender;
    static Image gui[] = new Image[5];
    static ClientConnection cc;
    static boolean changeColor, waiting;

    public static void exit() {
        File cfg = new File("conf.cfg");
        conf.set("locale", currentLocale);
        conf.set("w", Display.getWidth());
        conf.set("h", Display.getHeight());
        conf.set("x", Display.getX());
        conf.set("y", Display.getY());
        try {
            Writer.writeToFile(conf, cfg);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        System.exit(0);
    }
    static JCFG conf = new JCFG();

    public static void setUpRender() {
        Thread renderingThread;
       
        new Timer().schedule(new TimerTask() {
            public void run() {
                
            }
        },0,10);
        
        
        renderingThread = new Thread("Main Rendering Thread") {

            @Override
            public void run() {
                try {
                    Graphics gr = new Graphics();
                    int w = 800, h = 600, x = 0, y = 0;
                    File cfg = new File("conf.cfg");
                    if (cfg.exists()) {
                        conf = Parser.parse(cfg);
                        w = conf.get("w").getValueAsInteger();
                        h = conf.get("h").getValueAsInteger();
                        x = conf.get("x").getValueAsInteger();
                        y = conf.get("y").getValueAsInteger();
                        currentLocale = conf.get("locale").getValueAsInteger(); 
                    } else {
                        conf.set("music", true);
                        conf.set("sound", true);
                        String colorExamples[] = new String[]{
                            "#ff0034",
                            "#ee9900",
                            "#f6ef00",
                            "#4cbb17",
                            "#33cbff",
                            "#0000ff",
                            "#ff5daf",
                            "#904602"
                        };
                        conf.set("color", colorExamples[new Random().nextInt(8)]);
                    }
                    Display.setDisplayMode(new DisplayMode(w, h));
                    Display.setLocation(x, y);
                    Display.setTitle("Tactical Tank Competitions");
                    Display.setResizable(true);

                    try {
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error!\n" + ex.toString());
                        System.exit(1);
                    }
                    Display.setIcon(new ByteBuffer[]{
                        new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("textures/icons/icon.png")), false, false, null),
                        new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("textures/icons/icon.png")), false, false, null)
                    });
                    Display.create(new PixelFormat(0, 4, 0, 4));
                    Image logo = new Image("textures/logo.png"),
                            loading = new Image("textures/loading.png");

                    {
                        display_width = Display.getWidth();
                        display_height = Display.getHeight();
                        GL11.glClearColor(1, 1, 1, 1);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glMatrixMode(GL11.GL_PROJECTION);
                        GL11.glLoadIdentity();
                        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glLoadIdentity();
                        logo.draw(display_width / 2 - logo.getWidth() / 2, display_height / 2 - logo.getHeight() / 2);
                        loading.draw(10, display_height - 35);
                        Display.update();
                    }
                    Unit.initGraphics();
                    Font font = Font.createFont(Font.TRUETYPE_FONT, new File("textures/squarea.ttf"));
                    font = font.deriveFont(20f).deriveFont(1);
                    fontRender = FontRender.getTextRender(font);
                    gui[0] = new Image("textures/gui/button_left.png");
                    gui[1] = new Image("textures/gui/button_center.png");
                    gui[2] = new Image("textures/gui/button_right.png");
                    gui[3] = new Image("textures/gui/button_trackbar.png");
                    gui[4] = new Image("textures/gui/button_trackbar2.png");
                    gui[1].setFilter(GL11.GL_NEAREST);
                    ArrayList<button> buttons = new ArrayList<button>();

                    ArrayList<button> colors = new ArrayList<button>();

                    ArrayList<button> waitbuttons = new ArrayList<button>();

                    buttons.add(new button(0, 300, 400, "singl", new Color(0, 255, 0)) {

                        @Override
                        public void click() {
                            Random rand = new Random();
                            room = new Room(4);
                            room.player = 0;
                            for (int i = 0; i < 4; i++) {
                                room.players[i].name = "AI";
                                room.players[i].color = new Color(rand.nextInt());
                                room.players[i].ai = true;
                            }
                            room.players[0].ai = false;
                            room.players[0].color = Color.decode(conf.get("color").getValueAsString());
                        }

                    });

                    buttons.add(new button(0, 370, 400, "multi", new Color(0, 155, 255)) {

                        @Override
                        public void click() {
                            /*
                             cc = new ClientConnection("93.170.104.17", 20150, ClientHandler.class, 100);
                             try {
                             cc.connect();
                             } catch (InstantiationException ex) {
                             Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
                             } catch (IllegalAccessException ex) {
                             Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
                             } catch (IOException ex) {
                             Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             waiting = true;
                             */
                        }

                    });

                    buttons.add(new locale(10, 10));

                    buttons.add(new button(0, 440, 150, "chang", Color.decode(conf.get("color").getValueAsString())) {

                        @Override
                        public void render() {
                            color = Color.decode(conf.get("color").getValueAsString());
                            super.render(); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void click() {
                            changeColor = true;
                        }

                    });

                    buttons.add(new button(0, 510, 400, "exit", new Color(255, 55, 0)) {

                        @Override
                        public void click() {
                            exit();
                        }

                    });

                    waitbuttons.add(new button(0, 270, 400, "exit", new Color(255, 55, 0)) {

                        @Override
                        public void click() {
                            cc.disconnect();
                            waiting = false;
                        }

                    });

                    
                    new Timer().schedule(new TimerTask() {
                            public void run() {
                            if (escTimer > 0) {
                            escTimer--;
                        }}},0,30);
                    final Music nowPlaying = new Music("music/Steve_Combs_Five.ogg");

                    nowPlaying.loop();
                    nowPlaying.setVolume(conf.get("music").getValueAsBoolean() ? 1 : 0);

                    buttons.add(new switcher(165, 440, conf.get("sound").getValueAsBoolean(), "textures/icons/sound") {

                        @Override
                        public void click() {
                            conf.set("sound", value);
                        }

                    });

                    buttons.add(new switcher(-165, 440, conf.get("music").getValueAsBoolean(), "textures/icons/music") {

                        @Override
                        public void click() {
                            nowPlaying.setVolume(value ? 1 : 0);
                            conf.set("music", value);
                        }

                    });

                    {
                        Object colorExamples[] = new Object[]{
                            "red", Color.decode("#ff0034"),
                            "orange", Color.decode("#ee9900"),
                            "yellow", Color.decode("#f6ef00"),
                            "green", Color.decode("#4cbb17"),
                            "aqua", Color.decode("#33cbff"),
                            "blue", Color.decode("#0000ff"),
                            "pink", Color.decode("#ff5daf"),
                            "choco", Color.decode("#904602")
                        };
                        for (int i = 0; i < 16; i += 2) {
                            colors.add(new button(0, 100 + 30 * i, 400, colorExamples[i].toString(), (Color) colorExamples[i + 1]) {

                                @Override
                                public void click() {
                                    conf.set("color", String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                                    changeColor = false;
                                }

                            });
                        }
                    }
                    Help.load();
                    while (!Display.isCloseRequested()) {
                        display_width = Display.getWidth();
                        display_height = Display.getHeight();
                        GL11.glClearColor(1, 1, 1, 1);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glMatrixMode(GL11.GL_PROJECTION);
                        GL11.glLoadIdentity();
                        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glLoadIdentity();
                        glColor4f(1, 1, 1, 1);
                        if (room != null) {
                            room.render(room.player);
                            if (escTimer == 0 && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                                escTimer = 10;
                                if (room.t.isRunning()) {
                                    room.t.stop();
                                } else {
                                    room.t.start();
                                }
                            }
                        } else {
                            for (x = -1; x <= Display.getWidth() / 128 + 1; x += 1) {
                                for (y = -1; y <= Display.getHeight() / 128 + 1; y += 1) {
                                    Room.grass.draw(x * 128, y * 128);
                                }
                            }
                            fontRender.drawString("0.8 alpha", 95, 25, Color.white);
                            fontRender.drawString("Yew_Mentzaki", 25, 60, Color.white);
                            fontRender.drawString("Whizzpered", 25, 85, Color.white);
                            fontRender.drawString("Steve Combs", 25, 110, Color.white);
                        }
                        if (room == null || !room.t.isRunning()) {
                            gr.setColor(new Color(0, 0, 0, 125));
                            gr.fillRect(0, 0, Display.getWidth(), Display.getHeight());
                            if (waiting) {
                                for (button b : waitbuttons) {
                                    b.render();
                                }
                            } else {
                                if (changeColor) {
                                    for (button b : colors) {
                                        b.render();
                                    }
                                } else {
                                    logo.draw(display_width / 2 - logo.getWidth() / 4, 100, logo.getWidth() / 2, logo.getHeight() / 2);
                                    for (button b : buttons) {
                                        b.render();
                                    }
                                }
                            }
                        }
                        Display.update();
                        Display.sync(1000);
                        if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
                            GL11.glReadBuffer(GL11.GL_FRONT);
                            int width = Display.getWidth();
                            int height = Display.getHeight();
                            int bpp = 4;
                            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
                            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                            File file = new File("screenshots/screenshot " + new Date().toGMTString() + ".png"); // The file to save to.
                            file.mkdirs();
                            String format = "PNG";
                            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                            for (x = 0; x < width; x++) {
                                for (y = 0; y < height; y++) {
                                    int i = (x + (width * y)) * bpp;
                                    int r = buffer.get(i) & 0xFF;
                                    int g = buffer.get(i + 1) & 0xFF;
                                    int b = buffer.get(i + 2) & 0xFF;
                                    image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                                }
                            }

                            try {
                                ImageIO.write(image, format, file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    exit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        renderingThread.setPriority(Thread.MAX_PRIORITY);
        renderingThread.start();
        try {
            locales[0] = Parser.parse(new File("locale/en_US.locale"));
            locales[1] = Parser.parse(new File("locale/de_DE.locale"));
            locales[2] = Parser.parse(new File("locale/ru_RU.locale"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(V2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setUpNatives() {
        if (!new File("native").exists()) {
            JOptionPane.showMessageDialog(null, "Error!\nNative libraries not found!");
            System.exit(1);
        }
        try {

            System.setProperty("java.library.path", new File("native").getAbsolutePath());

            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);

            try {
                fieldSysPath.set(null, null);
            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(null, "Error!\n" + ex.toString());
                System.exit(1);
            } catch (IllegalAccessException ex) {
                JOptionPane.showMessageDialog(null, "Error!\n" + ex.toString());
                System.exit(1);
            }
        } catch (NoSuchFieldException ex) {
            JOptionPane.showMessageDialog(null, "Error!\n" + ex.toString());
            System.exit(1);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(null, "Error!\n" + ex.toString());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            setUpNatives();
            setUpRender();
        } else if (args.length == 1) {

        }
    }
}
