package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.dao.ScoreDAO;
import com.oresteluci.scores.domain.UserScore;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * @author Oreste Luci
 */
public class ScoreService {

    private final Lock lock = new ReentrantLock();

    private static final Logger log = Logger.getLogger(ScoreService.class.getName());

    private ScoreDAO sessionKeyDAO;

    public ScoreService() {
        this.sessionKeyDAO = new ScoreDAO();
    }

    public String login(Integer userId) {

        String sessionKey = generateRandomSessionKey();

        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, ApplicationConfig.SESSION_KEY_TIMEOUT_MINUTES);

        lock.lock();
        try {

            sessionKeyDAO.saveSessionKey(userId, sessionKey, expiryDate.getTime());

        } finally {
            lock.unlock();
        }

        return sessionKey;
    }

    public void addScore(Integer levelId, String sessionKey, Integer score) {

        Long sessionKeyExpiryDate =  sessionKeyDAO.getSessionKeyExpiry(sessionKey);

        if (sessionKeyExpiryDate != null) {

            Calendar cal = Calendar.getInstance();

            if (cal.getTime().getTime() <= sessionKeyExpiryDate) {

                lock.lock();
                try {

                    Integer userId = sessionKeyDAO.getUserIdFromSessionKey(sessionKey);

                    if (userId != null) {

                        UserScore userScore = sessionKeyDAO.getUserLevelScore(userId, levelId);

                        if (userScore == null) {
                            userScore = new UserScore(levelId, userId, BigInteger.valueOf(score));
                        } else {
                            userScore.setScore(userScore.getScore().add(BigInteger.valueOf(score.intValue())));
                        }

                        sessionKeyDAO.saveScore(userScore);
                    }

                } finally {
                    lock.unlock();
                }

            } else {

                sessionKeyDAO.removeSessionKey(sessionKey);
            }

        } else {
            log.fine("SessionKey does not exist: " + sessionKey);
        }
    }

    private String generateRandomSessionKey() {
        return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }

    public List<UserScore> getHighestScores(Integer levelId) {

        return sessionKeyDAO.getLevelHighScores(levelId);
    }
}
