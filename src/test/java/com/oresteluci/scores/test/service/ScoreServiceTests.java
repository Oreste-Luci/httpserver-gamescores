package com.oresteluci.scores.test.service;

import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.service.LoginService;
import com.oresteluci.scores.service.ScoreService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oreste Luci
 */
public class ScoreServiceTests {

    @Test
    public void addScoreTest() {

        ScoreService scoreService = new ScoreService();

        String sessionKey = "ABC";
        int levelId = 100;
        int score = 2000;
        int userId = 55;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        LoginService loginService = mock(LoginService.class);
        when(loginService.getUserSessionByKey(sessionKey)).thenReturn(new UserSession(userId, sessionKey, cal.getTime()));

        ReflectionTestUtils.setField(scoreService,"loginService",loginService);

        assertTrue(scoreService.addScore(levelId, sessionKey, score));
    }

    @Test
    public void addScoreExpiredSessionKeyTest() {

        ScoreService scoreService = new ScoreService();

        String sessionKey = "ABC";
        int levelId = 100;
        int score = 2000;
        int userId = 55;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);

        LoginService loginService = mock(LoginService.class);
        when(loginService.getUserSessionByKey(sessionKey)).thenReturn(null);

        ReflectionTestUtils.setField(scoreService, "loginService", loginService);

        boolean added = scoreService.addScore(levelId, sessionKey, score);

        assertFalse(added);
    }

    @Test
    public void getHighestScoresTest() {

        ScoreService scoreService = new ScoreService();

        String sessionKey = "ABC";
        int levelId = 100;
        int score = 500;
        int userId = 55;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        LoginService loginService = mock(LoginService.class);
        when(loginService.getUserSessionByKey(sessionKey)).thenReturn(new UserSession(userId, sessionKey, cal.getTime()));

        ReflectionTestUtils.setField(scoreService,"loginService",loginService);

        scoreService.addScore(levelId, sessionKey, score);

        List<UserScore> userScores = scoreService.getHighestScores(levelId);

        if (userScores == null || userScores.size() != 1) {

            assertTrue(false);

        } else {

            UserScore userScore = userScores.get(0);

            assertTrue(userScore.getLevelId() == levelId
                    && userScore.getScore().compareTo(BigInteger.valueOf(score)) == 0);
        }
    }

    @Test
    public void getHighestScoresEmptyTest() {

        ScoreService scoreService = new ScoreService();

        int levelId = 100;

        List<UserScore> userScores = scoreService.getHighestScores(levelId);

        assertTrue(userScores == null);
    }
}
