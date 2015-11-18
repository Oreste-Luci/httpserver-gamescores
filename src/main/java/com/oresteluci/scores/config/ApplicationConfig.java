package com.oresteluci.scores.config;

import com.oresteluci.scores.injection.AutoComponent;

/**
 * Contains default configuration parameters for the application.
 *
 * @author Oreste Luci
 */
@AutoComponent
public class ApplicationConfig {

    private int SERVER_DEFAULT_PORT = 8080;
    private int SCORE_LIST_SIZE = 15;
    private int SESSION_KEY_TIMEOUT_MINUTES = 10;
    private int HTTP_EXECUTOR_DEFAULT_FIXED_THREAD_POOL_SIZE = 10;

    public int getServerDefaultPort() {
        return SERVER_DEFAULT_PORT;
    }

    public int getScoreListSize() {
        return SCORE_LIST_SIZE;
    }

    public int getSessionKeyTimeoutMinutes() {
        return SESSION_KEY_TIMEOUT_MINUTES;
    }

    public int getHttpExecutorDefaultFixedThreadPoolSize() {
        return HTTP_EXECUTOR_DEFAULT_FIXED_THREAD_POOL_SIZE;
    }
}
