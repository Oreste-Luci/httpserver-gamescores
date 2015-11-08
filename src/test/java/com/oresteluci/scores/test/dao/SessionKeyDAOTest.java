package com.oresteluci.scores.test.dao;

import com.oresteluci.scores.dao.ScoreDAO;
import com.oresteluci.scores.domain.UserScore;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Oreste Luci
 */
public class SessionKeyDAOTest {

    @Test
    public void saveSessionKeyTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        Integer userId = 10;
        String sessionKey = "SESSION1";
        Date expiryDate = new Date();

        sessionKeyDAO.saveSessionKey(userId, sessionKey, expiryDate);

        Long savedExpiry = sessionKeyDAO.getSessionKeyExpiry(sessionKey);

        assertTrue(expiryDate.getTime() == savedExpiry.longValue());
    }

    @Test
    public void removeSessionKeyTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        Integer userId = 10;
        String sessionKey = "SESSION1";
        Date expiryDate = new Date();

        sessionKeyDAO.saveSessionKey(userId, sessionKey, expiryDate);

        sessionKeyDAO.removeSessionKey(sessionKey);

        Long savedExpiry = sessionKeyDAO.getSessionKeyExpiry(sessionKey);

        assertTrue(savedExpiry == null);
    }

    @Test
    public void saveScoreTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        int levelId = 100;
        int userId = 10;
        int score = 2000;

        UserScore userScore = new UserScore(levelId,userId, BigInteger.valueOf(score));

        sessionKeyDAO.saveScore(userScore);

        List<UserScore> scores = sessionKeyDAO.getLevelHighScores(levelId, 15);

        if (scores == null || scores.size() != 1) {

            assertTrue(false);

        } else {

            UserScore userScore2 = scores.get(0);
            assertTrue(equalUserScore(userScore,userScore2));
        }
    }

    @Test
    public void getUserLevelScoreTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        int levelId = 100;
        int userId = 10;
        int score = 2000;

        UserScore userScore = new UserScore(levelId,userId, BigInteger.valueOf(score));

        sessionKeyDAO.saveScore(userScore);

        UserScore userScore2 = sessionKeyDAO.getUserLevelScore(userId, levelId);

        assertTrue(equalUserScore(userScore,userScore2));
    }

    @Test
    public void getUserIdFromSessionKeyTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        Integer userId = 10;
        String sessionKey = "SESSION1";
        Date expiryDate = new Date();

        sessionKeyDAO.saveSessionKey(userId, sessionKey, expiryDate);

        Integer resturnedUserId = sessionKeyDAO.getUserIdFromSessionKey(sessionKey);

        assertTrue(userId.intValue() == resturnedUserId.intValue());
    }

    @Test
    public void getSessionKeyExpiryTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        Integer userId = 10;
        String sessionKey = "SESSION1";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);

        sessionKeyDAO.saveSessionKey(userId, sessionKey, cal.getTime());

        Long time = sessionKeyDAO.getSessionKeyExpiry(sessionKey);

        assertTrue(time != null);
    }

    @Test
    public void getLevelHighScoresTest() {

        ScoreDAO sessionKeyDAO = new ScoreDAO();

        int levelid = 100;

        for (int i=0; i<16; i++) {

            UserScore userScore = new UserScore(levelid,10 + i, BigInteger.valueOf(1000*i));
            sessionKeyDAO.saveScore(userScore);
        }

        List<UserScore> scores = sessionKeyDAO.getLevelHighScores(levelid, 15);

        if (scores == null || scores.size() != 15) {

            assertTrue(false);

        } else {

            boolean correct = true;

            int i = 15;
            for (UserScore userScore : scores) {

                if (!(userScore.getScore().compareTo(BigInteger.valueOf(1000+i)) == 0)
                        && !(userScore.getUserId().intValue() == 10 + i) ) {
                    correct = false;
                }

                i--;
            }

            assertTrue(correct);
        }
    }

    private boolean equalUserScore(UserScore userScore1, UserScore userScore2) {

        return userScore1.getLevelId().intValue() == userScore2.getLevelId().intValue()
                && userScore1.getUserId().intValue() == userScore2.getUserId().intValue()
                && userScore1.getScore().compareTo(userScore2.getScore()) == 0;
    }
}
