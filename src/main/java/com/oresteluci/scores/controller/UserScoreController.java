package com.oresteluci.scores.controller;

import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;
import com.oresteluci.scores.service.ScoreService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oresteluci.scores.handler.HandlerDispatcher.SCORE_REGEX_PATH;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Oreste Luci
 */
public @AutoBean class UserScoreController extends AbstractController {

    @AutoInject
    private ScoreService scoreService;

    /**
     * Reads score parameters and adds score to user for the given level.
     * Path parameters: /<levelid>/score?sessionkey=<sessionkey>
     *
     * @param httpExchange
     * @throws IOException
     */
    public void execute(HttpExchange httpExchange) throws IOException {

        int levelId = -1;

        Pattern p = Pattern.compile(SCORE_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting levelId from path
        if (m.find()) {
            levelId = Integer.parseInt(m.group(1));
        }

        if (levelId == -1) {
            return;
        }

        // Getting session key query param
        Map<String, String> parameters = queryToMap(httpExchange.getRequestURI().getQuery());
        String sessionKey = parameters.get("sessionkey");

        // Getting score from body
        String body = convertStreamToString(httpExchange.getRequestBody());

        // Adding score to user for the current level
        scoreService.addScore(levelId, sessionKey, Integer.parseInt(body));

        createResponse(httpExchange, HTTP_OK, "");
    }
}
