package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.dao.ScoreDAO;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Service that contains the main business logic.
 * This class also controls the thread locks.
 *
 * @author Oreste Luci
 */
@AutoBean
public class ScoreService {

    private final Lock lock = new ReentrantLock();

    private static final Logger log = Logger.getLogger(ScoreService.class.getName());

    @AutoInject
    private ScoreDAO sessionKeyDAO;

    /**
     * Creates a session key for the given user.
     *
     * @param userId
     * @return
     */
    public String login(Integer userId) {

        // Creates random session key
        String sessionKey = generateRandomSessionKey();

        // Determining expiry date
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, ApplicationConfig.SESSION_KEY_TIMEOUT_MINUTES);

        lock.lock();
        try {

            // saving session key
            sessionKeyDAO.saveSessionKey(userId, sessionKey, expiryDate.getTime());

        } finally {
            lock.unlock();
        }

        return sessionKey;
    }

    /**
     * Adds a score to the user for the given level. If the session key has expired it does not add it.
     *
     * @param levelId
     * @param sessionKey
     * @param score
     */
    public void addScore(Integer levelId, String sessionKey, Integer score) {

        // Getting sessionKey expiry date
        Long sessionKeyExpiryDate =  sessionKeyDAO.getSessionKeyExpiry(sessionKey);

        if (sessionKeyExpiryDate != null) {

            Calendar cal = Calendar.getInstance();

            // Determining if sessionKey has expired
            if (cal.getTime().getTime() <= sessionKeyExpiryDate) {

                lock.lock();
                try {

                    Integer userId = sessionKeyDAO.getUserIdFromSessionKey(sessionKey);

                    if (userId != null) {

                        UserScore userScore = sessionKeyDAO.getUserLevelScore(userId, levelId);

                        // If no previous score present it sets given score as current score
                        if (userScore == null) {

                            userScore = new UserScore(levelId, userId, BigInteger.valueOf(score));

                        } else {

                            // Adding given core to previous score
                            userScore.setScore(userScore.getScore().add(BigInteger.valueOf(score.intValue())));
                        }

                        sessionKeyDAO.saveScore(userScore);
                    }

                } finally {
                    lock.unlock();
                }

            } else {
                // If sessionKey is expired then remove it from storage.
                sessionKeyDAO.removeSessionKey(sessionKey);
            }

        } else {
            log.fine("SessionKey does not exist: " + sessionKey);
        }
    }

    /**
     * Returns a list of users with the highest scores for the given level.
     * The list size is limited by @see com.oresteluci.scores.config.ApplicationConfig.SCORE_LIST_SIZE
     *
     * @param levelId
     * @return
     */
    public List<UserScore> getHighestScores(Integer levelId) {

        return sessionKeyDAO.getLevelHighScores(levelId, ApplicationConfig.SCORE_LIST_SIZE);
    }

    /**
     * Utility method to create a random session key.
     * It is based on the universally unique identifier logic provided by java.
     * It strips the - character from the generated UUID.
     *
     * @return
     */
    private String generateRandomSessionKey() {
        return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }
}
