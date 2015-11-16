package com.oresteluci.scores.controller;

import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;
import com.oresteluci.scores.service.ScoreService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oresteluci.scores.handler.HandlerDispatcher.HIGH_SCORE_LIST_REGEX_PATH;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Oreste Luci
 */
@AutoBean
public class HighScoreController extends AbstractController {

    @AutoInject
    private ScoreService scoreService;

    /**
     * Reads the level id from the path and returns the 15 users with the highest scores.
     * Path parameters: /<levelid>/highscorelist
     * Response: CSV with <userid>=<score>
     *
     * @param httpExchange
     * @throws IOException
     */
    public void execute(HttpExchange httpExchange) throws IOException {

        Integer levelId = null;

        Pattern p = Pattern.compile(HIGH_SCORE_LIST_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting the levelId
        if (m.find()) {
            levelId = new Integer(m.group(1));
        }

        // Getting high scores
        List<UserScore> scores = scoreService.getHighestScores(levelId);

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
}
