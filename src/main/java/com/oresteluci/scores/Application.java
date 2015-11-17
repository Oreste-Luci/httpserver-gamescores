package com.oresteluci.scores;

import com.oresteluci.scores.injection.ServerInitialization;

/**
 * Starting point of the application
 *
 * @author Oreste Luci
 */
public class Application {

    /**
     * Everything starts here
     *
     * @param args program parameters
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ServerInitialization.run("com.oresteluci.scores", "com.oresteluci.scores.server.ServerImpl", args);
    }
    
}
