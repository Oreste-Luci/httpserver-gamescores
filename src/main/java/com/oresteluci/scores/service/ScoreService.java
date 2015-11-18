package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.injection.AutoComponent;
import com.oresteluci.scores.injection.AutoInject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Oreste Luci
 */
public @AutoComponent class ScoreService {

    @AutoInject
    private LoginService loginService;

    @AutoInject
    private ApplicationConfig applicationConfig;

    // Two maps are used to de-normalize the store UserScore. One for searching by levelId the other by levelId and UserId.
    /**
     * Map that stores the list of user scores for each level. Using and ordered Set to keep scores.
     */
    private Map<Integer,List<UserScore>> levelScoreMap = new ConcurrentHashMap<>();
    /**
     * Map that stores UserScore by level and user.
     */
    private Map<String,UserScore> userScoreMap = new ConcurrentHashMap<>();

    // Defining fair lock
    Lock lock = new ReentrantLock(true);

    /**
     * Adds a score to the user for the given level.
     *
     * @param levelId Identifies the level
     * @param sessionKey Identifies the valid user session
     * @param score Score to be added
     * @return true if score added false if session key is invalid
     */
    public boolean addScore(int levelId, String sessionKey, int score) {

        // Getting User Session
        UserSession userSession = loginService.getUserSessionByKey(sessionKey);

        // Ignoring if no valid user session
        if (userSession ==  null) {
            return false;
        }

        // Thread safe score update
        lock.lock();

        try {

            //TODO does not work when scores are the same
            // Getting user scores for level
            List<UserScore> levelUserScores = levelScoreMap.get(levelId);

            String userScoreMapKey = getUserScoreMapKey(levelId, userSession.getUserId());

            // If level has no scores yet
            if (levelUserScores == null) {

                UserScore userScore = new UserScore(levelId, userSession.getUserId(), BigInteger.valueOf(score));

                levelUserScores = new ArrayList<>();
                levelUserScores.add(userScore);

                // Adding Score to both maps
                levelScoreMap.put(levelId, levelUserScores);
                userScoreMap.put(userScoreMapKey, userScore);

            } else { // If level has scores

                // Getting Score by level and user
                UserScore userScore = userScoreMap.get(userScoreMapKey);

                if (userScore != null) {

                    // Updating score
                    userScore.setScore(userScore.getScore().add(BigInteger.valueOf(score)));

                } else {

                    // Creating new score and storing it
                    userScore = new UserScore(levelId, userSession.getUserId(), BigInteger.valueOf(score));
                    levelUserScores.add(userScore);
                    userScoreMap.put(userScoreMapKey, userScore);
                }
            }

            return true;

        } finally {
            lock.unlock();
        }
    }

    public List<UserScore> getHighestScores(int levelId) {

        // Thread safe get top scores
        lock.lock();

        try {

            // Get ordered list of scores for the given level
            List<UserScore> userScoreList = levelScoreMap.get(levelId);

            if (userScoreList == null || userScoreList.size() == 0) {
                return userScoreList;
            }

            // Sorting the list
            Collections.sort(userScoreList);

            int toIndex = applicationConfig.getScoreListSize();

            if (userScoreList.size() < toIndex) {
                toIndex = userScoreList.size();
            }

            return userScoreList.subList(0,toIndex);

        } finally {
            lock.unlock();
        }
    }

    /**
     * UserScore Map Key Generator
     * @param levelId
     * @param userId
     * @return
     */
    private String getUserScoreMapKey(int levelId, int userId) {
        return levelId + "-" + userId;
    }
}
