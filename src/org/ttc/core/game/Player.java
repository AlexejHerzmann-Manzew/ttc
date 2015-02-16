package org.ttc.core.game;

import java.io.Serializable;
import java.util.Random;
import org.newdawn.slick.Color;

/**
 *
 * @author yew_mentzaki
 */
public class Player implements Serializable{

    public Player(String name, int color) {
        this.name = name;
        this.color = new Color(color);
        this.camerax = 0;
        this.cameray = 0;
    }
    public String name;
    public Color color = Color.decode("#ffffff");
    int camerax, cameray;
}
