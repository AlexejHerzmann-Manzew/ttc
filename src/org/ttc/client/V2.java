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
    static int currentLocale;
    static JCFG locales[] = new JCFG[3];
    static int escTimer = 0;

    
    

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
                    ArrayList<Button> buttons = new ArrayList<Button>();

                    ArrayList<Button> colors = new ArrayList<Button>();

                    ArrayList<Button> waitbuttons = new ArrayList<Button>();

                    buttons.add(new Button(0, 300, 400, "singl", new Color(0, 255, 0)) {

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

                    buttons.add(new Button(0, 370, 400, "multi", new Color(0, 155, 255)) {

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

                    buttons.add(new Locale(10, 10));

                    buttons.add(new Button(0, 440, 150, "chang", Color.decode(conf.get("color").getValueAsString())) {

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

                    buttons.add(new Button(0, 510, 400, "exit", new Color(255, 55, 0)) {

                        @Override
                        public void click() {
                            exit();
                        }

                    });

                    waitbuttons.add(new Button(0, 270, 400, "exit", new Color(255, 55, 0)) {

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
                        }}},0,10);
                    final Music nowPlaying = new Music("music/Steve_Combs_Five.ogg");

                    nowPlaying.loop();
                    nowPlaying.setVolume(conf.get("music").getValueAsBoolean() ? 1 : 0);

                    buttons.add(new Switcher(165, 440, conf.get("sound").getValueAsBoolean(), "textures/icons/sound") {

                        @Override
                        public void click() {
                            conf.set("sound", value);
                        }

                    });

                    buttons.add(new Switcher(-165, 440, conf.get("music").getValueAsBoolean(), "textures/icons/music") {

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
                            colors.add(new Button(0, 100 + 30 * i, 400, colorExamples[i].toString(), (Color) colorExamples[i + 1]) {

                                @Override
                                public void click() {
                                    conf.set("color", String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                                    changeColor = false;
                                }

                            });
                        }
                    }

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
                            fontRender.drawString("0.7.2 alpha", 95, 25, Color.white);
                            fontRender.drawString("Yew_Mentzaki", 25, 60, Color.white);
                            fontRender.drawString("Whizzpered", 25, 85, Color.white);
                            fontRender.drawString("Steve Combs", 25, 110, Color.white);
                        }
                        if (room == null || !room.t.isRunning()) {
                            gr.setColor(new Color(0, 0, 0, 125));
                            gr.fillRect(0, 0, Display.getWidth(), Display.getHeight());
                            if (waiting) {
                                for (Button b : waitbuttons) {
                                    b.render();
                                }
                            } else {
                                if (changeColor) {
                                    for (Button b : colors) {
                                        b.render();
                                    }
                                } else {
                                    logo.draw(display_width / 2 - logo.getWidth() / 4, 100, logo.getWidth() / 2, logo.getHeight() / 2);
                                    for (Button b : buttons) {
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
