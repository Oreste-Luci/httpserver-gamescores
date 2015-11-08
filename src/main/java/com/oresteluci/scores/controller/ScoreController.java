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
 * @author Oreste Luci
 */
public class ScoreController {

    private ScoreService sessionKeyService;

    public ScoreController() {
        this.sessionKeyService = new ScoreService();
    }

    public void login(HttpExchange httpExchange) throws IOException {

        Integer userId = null;

        Pattern p = Pattern.compile(LOGIN_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        if (m.find()) {
            userId = new Integer(m.group(1));
        }

        String response = sessionKeyService.login(userId);

        createResponse(httpExchange, HTTP_OK, response);
    }

    public void score(HttpExchange httpExchange) throws IOException {

        Integer levelId = null;

        Pattern p = Pattern.compile(SCORE_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        if (m.find()) {
            levelId = new Integer(m.group(1));
        }

        Map<String, String> parameters = queryToMap(httpExchange.getRequestURI().getQuery());

        String sessionKey = parameters.get("sessionkey");

        String body = convertStreamToString(httpExchange.getRequestBody());

        sessionKeyService.addScore(levelId, sessionKey, new Integer(body));

        createResponse(httpExchange, HTTP_OK, "");
    }

    public void highScore(HttpExchange httpExchange) throws IOException {

        Integer levelId = null;

        Pattern p = Pattern.compile(HIGH_SCORE_LIST_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        if (m.find()) {
            levelId = new Integer(m.group(1));
        }

        List<UserScore> scores = sessionKeyService.getHighestScores(levelId);

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

        createResponse(httpExchange, HTTP_OK, response.toString());
    }

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

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is);
        return s.hasNext() ? s.next() : "";
    }
}
