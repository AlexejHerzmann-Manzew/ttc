package org.ttc.core.game;

import java.io.Serializable;

/**
 *
 * @author yew_mentzaki
 */
public class Order implements Serializable{
    int uid, tx, ty;

    public Order(int uid, int tx, int ty) {
        this.uid = uid;
        this.tx = tx;
        this.ty = ty;
    }
    
}
