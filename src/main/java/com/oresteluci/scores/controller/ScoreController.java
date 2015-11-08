package com.oresteluci.scores.controller;

import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.service.ScoreService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oresteluci.scores.handler.HandlerDispatcher.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Controller for reading HttpExchange requests, calling appropriate service methods and putting response in HttpExchange.
 *
 * @author Oreste Luci
 */
public class ScoreController {

    private ScoreService sessionKeyService;

    public ScoreController() {
        this.sessionKeyService = new ScoreService();
    }

    /**
     * Reads user id path param and returns new Session Key.
     * Path parameters: /<userid>/login
     * Response: return the new Session Key in the body.
     *
     * @param httpExchange
     * @throws IOException
     */
    public void login(HttpExchange httpExchange) throws IOException {

        Integer userId = null;

        Pattern p = Pattern.compile(LOGIN_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting user id from path
        if (m.find()) {
            userId = new Integer(m.group(1));
        }

        String sessionKey = sessionKeyService.login(userId);

        // Returning session key
        createResponse(httpExchange, HTTP_OK, sessionKey);
    }

    /**
     * Reads score parameters and adds score to user for the given level.
     * Path parameters: /<levelid>/score?sessionkey=<sessionkey>
     *
     * @param httpExchange
     * @throws IOException
     */
    public void score(HttpExchange httpExchange) throws IOException {

        Integer levelId = null;

        Pattern p = Pattern.compile(SCORE_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting levelId from path
        if (m.find()) {
            levelId = new Integer(m.group(1));
        }

        // Getting session key query param
        Map<String, String> parameters = queryToMap(httpExchange.getRequestURI().getQuery());
        String sessionKey = parameters.get("sessionkey");

        // Getting score from body
        String body = convertStreamToString(httpExchange.getRequestBody());

        // Adding score to user for the current level
        sessionKeyService.addScore(levelId, sessionKey, new Integer(body));

        createResponse(httpExchange, HTTP_OK, "");
    }

    /**
     * Reads the level id from the path and returns the 15 users with the highest scores.
     * Path parameters: /<levelid>/highscorelist
     * Response: CSV with <userid>=<score>
     *
     * @param httpExchange
     * @throws IOException
     */
    public void highScore(HttpExchange httpExchange) throws IOException {

        Integer levelId = null;

        Pattern p = Pattern.compile(HIGH_SCORE_LIST_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting the levelId
        if (m.find()) {
            levelId = new Integer(m.group(1));
        }

        // Getting high scores
        List<UserScore> scores = sessionKeyService.getHighestScores(levelId);

        // Building response
        StringBuilder response = new StringBuilder("");

        int count = 0;
        for (UserScore userScore : scores) {

            count++;

            response.append(userScore.getUserId());
            response.append("=");
            response.append(userScore.getScore());

            if (count < scores.size()) {
                response.append(",");
            }
        }

        // Writing response
        createResponse(httpExchange, HTTP_OK, response.toString());
    }

    /**
     * Utility method to write response.
     *
     * @param httpExchange
     * @param statusCode
     * @param responseBody
     * @throws IOException
     */
    private void createResponse(HttpExchange httpExchange, int statusCode, String responseBody) throws IOException {

        int responseLength = 0;

        if (responseBody != null) {
            responseLength = responseBody.length();
        }

        httpExchange.sendResponseHeaders(HTTP_OK, responseLength);
        OutputStream os = httpExchange.getResponseBody();

        if (responseLength > 0) {
            os.write(responseBody.getBytes());
        }

        os.close();
    }

    /**
     * Utility method to extract query parameters.
     *
     * @param query
     * @return
     */
    private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    /**
     * Utility method to convert InputStream to String.
     *
     * @param is
     * @return
     */
    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is);
        return s.hasNext() ? s.next() : "";
    }
}
