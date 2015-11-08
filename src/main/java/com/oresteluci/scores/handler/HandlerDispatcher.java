package com.oresteluci.scores.handler;

import com.oresteluci.scores.controller.ScoreController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * @author Oreste Luci
 */
public class HandlerDispatcher implements HttpHandler {

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";

    public static final String LOGIN_REGEX_PATH = "/([0-9]+)/login$";
    public static final String SCORE_REGEX_PATH = "/([0-9]+)/score$";
    public static final String HIGH_SCORE_LIST_REGEX_PATH = "/([0-9]+)/highscorelist";

    private ScoreController scoreController;

    public HandlerDispatcher() {
        // Creating once instance of ScoreController
        this.scoreController = new ScoreController();
    }

    /**
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

                scoreController.login(httpExchange);

            } else if (requestPath.matches(HandlerDispatcher.SCORE_REGEX_PATH)
                    && HandlerDispatcher.REQUEST_METHOD_POST.equalsIgnoreCase(requestMethod)) {

                scoreController.score(httpExchange);

            } else if (requestPath.matches(HandlerDispatcher.HIGH_SCORE_LIST_REGEX_PATH)
                    && HandlerDispatcher.REQUEST_METHOD_GET.equalsIgnoreCase(requestMethod)) {

                scoreController.highScore(httpExchange);

            } else {

                notFound(httpExchange);
            }

        }

    }

    private void notFound(HttpExchange httpExchange) throws IOException {

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.close();
    }
}
