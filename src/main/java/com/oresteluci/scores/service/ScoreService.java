package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Oreste Luci
 */
@AutoBean
public class ScoreService {

    @AutoInject
    private LoginService loginService;

    // Two maps are used to de-normalize the store UserScore. One for searching by levelId the other by levelId and UserId.
    /**
     * Map that stores the list of user scores for each level. Using and ordered Set to keep scores.
     */
    private Map<Integer,TreeSet<UserScore>> levelScoreMap = new ConcurrentHashMap<>();
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
            TreeSet<UserScore> levelUserScores = levelScoreMap.get(levelId);

            // If level has no scores yet
            if (levelUserScores == null) {

                UserScore userScore = new UserScore(levelId, userSession.getUserId(), BigInteger.valueOf(score));

                levelUserScores = new TreeSet<>();
                levelUserScores.add(userScore);

                // Adding Score to both maps
                levelScoreMap.put(levelId, levelUserScores);

                String userScoreMapKey = getUserScoreMapKey(levelId, userSession.getUserId());
                userScoreMap.put(userScoreMapKey, userScore);

            } else { // If level has scores

                // Getting Score by level and user
                String userScoreMapKey = getUserScoreMapKey(levelId, userSession.getUserId());
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
            TreeSet<UserScore> userScoresSet = levelScoreMap.get(levelId);

            List<UserScore> highScoresList = new ArrayList<>(ApplicationConfig.SCORE_LIST_SIZE);

            if (userScoresSet == null || userScoresSet.size() == 0) {
                return highScoresList;
            }

            int toIndex = ApplicationConfig.SCORE_LIST_SIZE;

            if (userScoresSet.size() < toIndex) {
                toIndex = userScoresSet.size();
            }

            // Put into result list top scores
            int count = 0;
            for (UserScore userScore : userScoresSet.descendingSet()) {

                if (++count > toIndex) {
                    break;
                }

                highScoresList.add(userScore);
            }

            return highScoresList;

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
