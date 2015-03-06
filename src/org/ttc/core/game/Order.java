package org.ttc.core.game;

import java.io.Serializable;

/**
 *
 * @author yew_mentzaki
 */
public class Order implements Serializable{
    int uid, tx, ty, x, y;
    double a, ha;

    public Order(int uid, int tx, int ty, int x, int y, double a, double ha) {
        this.uid = uid;
        this.tx = tx;
        this.ty = ty;
        this.x = x;
        this.y = y;
        this.a = a;
        this.ha = ha;
    }

    
    
}
