package com.oresteluci.scores.handler;

import com.oresteluci.scores.controller.HighScoreController;
import com.oresteluci.scores.controller.LoginController;
import com.oresteluci.scores.controller.UserScoreController;
import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Determines which controller method to call by analyzing the HttpExchange object.
 *
 * @author Oreste Luci
 */
public @AutoBean class HandlerDispatcher implements HttpHandler {

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";

    /**
     * Valid Paths REGEXs
     */
    public static final String LOGIN_REGEX_PATH = "/([0-9]+)/login$";
    public static final String SCORE_REGEX_PATH = "/([0-9]+)/score$";
    public static final String HIGH_SCORE_LIST_REGEX_PATH = "/([0-9]+)/highscorelist";

    @AutoInject
    private LoginController loginController;

    @AutoInject
    private UserScoreController userScoreController;

    @AutoInject
    private HighScoreController highScoreController;

    /**
     * Analyzes the path and request method to determine which controller function to call.
     * If none matches then it puts a 404 in the response.
     *
     * @param httpExchange
     * @throws IOException
     */
    public void handle(HttpExchange httpExchange) throws IOException {

        if (httpExchange == null) {
            throw new IllegalArgumentException("HttpExchange parameter cannot be null");
        }

        URI requestURI = httpExchange.getRequestURI();
        String requestMethod = httpExchange.getRequestMethod();

        if (requestURI != null) {

            String requestPath = requestURI.getPath();

            if (requestPath.matches(HandlerDispatcher.LOGIN_REGEX_PATH)
                    && HandlerDispatcher.REQUEST_METHOD_GET.equalsIgnoreCase(requestMethod)) {

                loginController.execute(httpExchange);

            } else if (requestPath.matches(HandlerDispatcher.SCORE_REGEX_PATH)
                    && HandlerDispatcher.REQUEST_METHOD_POST.equalsIgnoreCase(requestMethod)) {

                userScoreController.execute(httpExchange);

            } else if (requestPath.matches(HandlerDispatcher.HIGH_SCORE_LIST_REGEX_PATH)
                    && HandlerDispatcher.REQUEST_METHOD_GET.equalsIgnoreCase(requestMethod)) {

                highScoreController.execute(httpExchange);

            } else {

                notFound(httpExchange);
            }

        }

    }

    /**
     * Utility method to build the 404 response
     * @param httpExchange
     * @throws IOException
     */
    private void notFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.close();
    }
}
