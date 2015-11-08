package com.oresteluci.scores.dao;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserScoreComparator;
import com.oresteluci.scores.domain.UserSessionKey;

import java.util.*;

/**
 * @author Oreste Luci
 */
public class ScoreDAO {

    private Map<Integer,List<String>> userSessionKeyMap = new HashMap<>();
    private Map<String,UserSessionKey> sessionKeyTimeMap = new HashMap<>();
    private Map<Integer,TreeSet<UserScore>> levelScoreMap = new HashMap<>();

    public void saveSessionKey(Integer userId, String sessionKey, Date expiryDate) {

        List<String> sessionKeys = userSessionKeyMap.get(userId);

        if (sessionKeys == null) {
            sessionKeys = new ArrayList<String>();
        }

        sessionKeys.add(sessionKey);

        userSessionKeyMap.put(userId, sessionKeys);

        UserSessionKey userSessionKey = new UserSessionKey(userId, sessionKey, expiryDate.getTime());

        sessionKeyTimeMap.put(sessionKey, userSessionKey);
    }

    public void saveScore(UserScore userScore) {

        TreeSet<UserScore> userScoreSet = levelScoreMap.get(userScore.getLevelId());

        boolean addToMap = false;
        if (userScoreSet == null) {
            userScoreSet = new TreeSet<>(new UserScoreComparator());
            addToMap = true;
        }

        userScoreSet.add(userScore);

        if (addToMap) {
            levelScoreMap.put(userScore.getLevelId(), userScoreSet);
        }
    }

    public Integer getUserIdFromSessionKey(String sessionKey) {

        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        if (userSessionKey != null) {
            return userSessionKey.getUserId();
        } else {
            return null;
        }
    }

    public Long getSessionKeyExpiry(String sessionKey) {

        Long sessionKeyExpiryDate = null;

        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        if (userSessionKey != null) {
            sessionKeyExpiryDate = userSessionKey.getExpiryDate();
        }

        return sessionKeyExpiryDate;
    }

    private  UserSessionKey getUserSessionByKey(String sessionKey) {
        return sessionKeyTimeMap.get(sessionKey);
    }

    public void removeSessionKey(String sessionKey) {

        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        List<String> sessionKeys = userSessionKeyMap.get(userSessionKey.getUserId());

        sessionKeys.remove(sessionKey);

        sessionKeyTimeMap.remove(sessionKey);
    }

    public List<UserScore> getLevelHighScores(Integer levelId) {

        TreeSet<UserScore> userScoresSet = levelScoreMap.get(levelId);

        if (userScoresSet == null) {
            return null;
        }

        List<UserScore> highScoresList = new ArrayList<>(ApplicationConfig.SCORE_LIST_SIZE);

        int count = 0;
        for (UserScore userScore : userScoresSet.descendingSet()) {

            if (++count > ApplicationConfig.SCORE_LIST_SIZE) {
                break;
            }

            highScoresList.add(userScore);
        }

        return highScoresList;
    }

    /**
     * Finds a UserScore by the given parameters
     *
     * @param userId User identifier
     * @param levelId Level identifier
     * @return UserScore given the UserID and the LevelID. Null if none found.
     */
    public UserScore getUserLevelScore(Integer userId, Integer levelId) {

        TreeSet<UserScore> userScoreSet = levelScoreMap.get(levelId);

        if (userScoreSet != null) {

            for (UserScore userScore : userScoreSet) {

                if (userScore.getUserId().intValue() == userId.intValue()) {
                    return userScore;
                }
            }
        }

        return null;
    }
}
