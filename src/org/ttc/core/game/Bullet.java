package org.ttc.core.game;

import java.io.Serializable;
import org.newdawn.slick.Graphics;

/**
 *
 * @author yew_mentzaki & whizzpered
 */
public class Bullet implements Serializable {

    public Bullet( double x, double y, double dx, double dy, Player owner, Room room) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.owner = owner;
        this.room = room;
    }

    double x, y, dx, dy;
    int type;
    Player owner;
    Room room;
    int t = 0, t2 = 255;

    public void render(Graphics g) {
       
    }

    public void tick() {
        
    }
}
