package com.oresteluci.scores.dao;

import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserScoreComparator;
import com.oresteluci.scores.domain.UserSessionKey;
import com.oresteluci.scores.injection.AutoBean;

import java.util.*;

/**
 * Stores the data for the Session Keys, Scores and User IDs.
 *
 * It uses Maps to store the data.
 *
 * @author Oreste Luci
 */
@AutoBean
public class ScoreDAO {

    /**
     * Map that keeps the list of sessionKeys fro avery user.
     */
    private Map<Integer,List<String>> userSessionKeyMap = new HashMap<>();

    /**
     * Map that keeps the relationship between a sessionKey and the expiry date and user id.
     * Makes lookup by sessionKey a lot faster.
     */
    private Map<String,UserSessionKey> sessionKeyTimeMap = new HashMap<>();


    /**
     * Map that stores the list of user scores for each level. Using and ordered Set to keep scores.
     */
    private Map<Integer,TreeSet<UserScore>> levelScoreMap = new HashMap<>();

    /**
     * Saves a session key with the given expiry date for the specified user.
     *
     * @param userId
     * @param sessionKey
     * @param expiryDate
     */
    public void saveSessionKey(Integer userId, String sessionKey, Date expiryDate) {

        // Are there any sessions for this user?
        List<String> sessionKeys = userSessionKeyMap.get(userId);

        if (sessionKeys == null) {
            // If not, create an empty list
            sessionKeys = new ArrayList<String>();
        }

        // Add the new session to the list
        sessionKeys.add(sessionKey);

        // Add/Update session key list
        userSessionKeyMap.put(userId, sessionKeys);

        UserSessionKey userSessionKey = new UserSessionKey(userId, sessionKey, expiryDate.getTime());

        // Insert user session details in session key map
        sessionKeyTimeMap.put(sessionKey, userSessionKey);
    }

    /**
     * Saves a score to storage.
     *
     * @param userScore
     */
    public void saveScore(UserScore userScore) {

        // Obtains scores for the given level
        TreeSet<UserScore> userScoreSet = levelScoreMap.get(userScore.getLevelId());

        boolean addToMap = false;
        if (userScoreSet == null) {
            // If no scores for level present
            userScoreSet = new TreeSet<>(new UserScoreComparator());
            addToMap = true;
        }

        // add score to list
        userScoreSet.add(userScore);

        if (addToMap) {
            // if no scores for level existed the it needs to be inserted in map
            levelScoreMap.put(userScore.getLevelId(), userScoreSet);
        }
    }

    /**
     * Returns a UserId given a Session Key.
     *
     * @param sessionKey
     * @return
     */
    public Integer getUserIdFromSessionKey(String sessionKey) {

        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        if (userSessionKey != null) {
            return userSessionKey.getUserId();
        } else {
            return null;
        }
    }

    /**
     * Returns a Session Key expiry date.
     *
     * @param sessionKey
     * @return
     */
    public Long getSessionKeyExpiry(String sessionKey) {

        Long sessionKeyExpiryDate = null;

        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        if (userSessionKey != null) {
            sessionKeyExpiryDate = userSessionKey.getExpiryDate();
        }

        return sessionKeyExpiryDate;
    }

    /**
     * Returns a User/Session details given a Session Key.
     * @param sessionKey
     * @return
     */
    private  UserSessionKey getUserSessionByKey(String sessionKey) {
        return sessionKeyTimeMap.get(sessionKey);
    }

    /**
     * Removes the given session Key form storage.
     *
     * @param sessionKey
     */
    public void removeSessionKey(String sessionKey) {

        // Get user associated with sessionKey
        UserSessionKey userSessionKey = getUserSessionByKey(sessionKey);

        // Get sessionKey list for the user
        List<String> sessionKeys = userSessionKeyMap.get(userSessionKey.getUserId());

        // Remove sessionKey form list
        sessionKeys.remove(sessionKey);

        // Also remove from sessionLey map
        sessionKeyTimeMap.remove(sessionKey);
    }

    /**
     * Returns a the list of users with the highest scores for the given level.
     *
     * @param levelId
     * @return
     */
    public List<UserScore> getLevelHighScores(Integer levelId, int listSize) {

        // Get ordered list of scores for the given level
        TreeSet<UserScore> userScoresSet = levelScoreMap.get(levelId);

        if (userScoresSet == null) {
            return null;
        }

        List<UserScore> highScoresList = new ArrayList<>(listSize);

        // Since list is in ascending order must iterate in descending order and get the top scores.
        int count = 0;
        for (UserScore userScore : userScoresSet.descendingSet()) {

            if (++count > listSize) {
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
