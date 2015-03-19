package org.ttc.core.server;

import java.util.ArrayList;
import org.sparkle.janette.server.ServerConnection;

/**
 *
 * @author yew_mentzaki
 */
public class Server {
    public static ServerHandler waiting = null;
    /*
    public static void main(String[] args) {
        ServerConnection sc = new ServerConnection(20150, ServerHandler.class, 100);
        try {
            sc.open();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    */
}
