package com.oresteluci.scores.test.service;

import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.service.ScoreService;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Oreste Luci
 */
public class ScoreServiceTests {

    @Test
    public void loginTest() {

        ScoreService scoreService = new ScoreService();

        String sessionKey = scoreService.login(100);

        assertTrue(sessionKey != null);
    }

    @Test
    public void addScoreTest() {

        ScoreService scoreService = new ScoreService();

        String sessionKey = scoreService.login(100);

        scoreService.addScore(1000, sessionKey, 500);
    }

    @Test
    public void getHighestScoresTest() {

        ScoreService scoreService = new ScoreService();

        int levelId = 100;
        int score = 500;

        String sessionKey = scoreService.login(levelId);

        scoreService.addScore(levelId,sessionKey,score);

        List<UserScore> userScores = scoreService.getHighestScores(levelId);

        if (userScores == null || userScores.size() != 1) {

            assertTrue(false);

        } else {

            UserScore userScore = userScores.get(0);

            assertTrue(userScore.getLevelId().intValue() == levelId
                    && userScore.getScore().compareTo(BigInteger.valueOf(score)) == 0);
        }
    }
}
