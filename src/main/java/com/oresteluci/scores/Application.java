package com.oresteluci.scores;

import com.oresteluci.scores.injection.ServerApplication;

/**
 * Starting point of the application
 *
 * @author Oreste Luci
 */
public class Application {

    /**
     * Everything starts here. Starts the server.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ServerApplication.run("com.oresteluci.scores","com.oresteluci.scores.server.ServerImpl",args);
    }
}
