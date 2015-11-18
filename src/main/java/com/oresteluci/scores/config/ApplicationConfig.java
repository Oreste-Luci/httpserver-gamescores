package com.oresteluci.scores.config;

import com.oresteluci.scores.injection.AutoBean;

/**
 * Contains default configuration parameters for the application.
 *
 * @author Oreste Luci
 */
@AutoBean
public class ApplicationConfig {

    private static final int SERVER_DEFAULT_PORT = 8080;
    private static final int SCORE_LIST_SIZE = 15;
    private static final int SESSION_KEY_TIMEOUT_MINUTES = 10;
    private static final int HTTP_EXECUTOR_DEFAULT_FIXED_THREAD_POOL_SIZE = 10;

    public static int getServerDefaultPort() {
        return SERVER_DEFAULT_PORT;
    }

    public static int getScoreListSize() {
        return SCORE_LIST_SIZE;
    }

    public static int getSessionKeyTimeoutMinutes() {
        return SESSION_KEY_TIMEOUT_MINUTES;
    }

    public static int getHttpExecutorDefaultFixedThreadPoolSize() {
        return HTTP_EXECUTOR_DEFAULT_FIXED_THREAD_POOL_SIZE;
    }
}
