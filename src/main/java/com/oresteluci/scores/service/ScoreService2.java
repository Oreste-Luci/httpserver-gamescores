package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.domain.UserScoreComparator;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Oreste Luci
 */
@AutoBean
public class ScoreService2 {

    @AutoInject
    private LoginService loginService;

    /**
     * Map that stores the list of user scores for each level. Using and ordered Set to keep scores.
     */
    private Map<Integer,TreeSet<UserScore>> levelScoreMap = new HashMap<>();


    public void addScore(int levelId, String sessionKey, int score) {

        UserSession userSession = loginService.getUserSessionByKey(sessionKey);

        if (userSession ==  null) {
            return;
        }

        TreeSet<UserScore> levelUserScores = levelScoreMap.get(levelId);

        if (levelUserScores == null) {

            UserScore userScore = new UserScore(levelId, userSession.getUserId(), BigInteger.valueOf(score));

            levelUserScores = new TreeSet<>(new UserScoreComparator());
            levelUserScores.add(userScore);

            levelScoreMap.put(levelId,levelUserScores);

        } else {

            boolean added = false;
            for (UserScore userScore : levelUserScores) {

                if (userSession.getUserId() == userScore.getUserId().intValue()) {

                    userScore.setScore(userScore.getScore().add(BigInteger.valueOf(score)));

                    added = true;
                    break;
                }
            }

            if (!added) {
                UserScore userScore = new UserScore(levelId, userSession.getUserId(), BigInteger.valueOf(score));
                levelUserScores.add(userScore);
            }

        }
    }

    public List<UserScore> getHighestScores(int levelId) {

        // Get ordered list of scores for the given level
        TreeSet<UserScore> userScoresSet = levelScoreMap.get(levelId);

        if (userScoresSet == null) {
            return null;
        }

        List<UserScore> highScoresList = new ArrayList<>(ApplicationConfig.SCORE_LIST_SIZE);

        if (userScoresSet.size() == 0) {
            return highScoresList;
        }

        int toIndex = ApplicationConfig.SCORE_LIST_SIZE;

        if (userScoresSet.size() < toIndex) {
            toIndex = userScoresSet.size();
        }

        int count = 0;
        for (UserScore userScore : userScoresSet.descendingSet()) {

            if (++count > toIndex) {
                break;
            }

            highScoresList.add(userScore);
        }


        return highScoresList;
    }
}
