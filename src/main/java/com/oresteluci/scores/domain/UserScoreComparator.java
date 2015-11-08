package com.oresteluci.scores.domain;

import java.util.Comparator;

/**
 * @author Oreste Luci
 */
public class UserScoreComparator implements Comparator<UserScore> {

    @Override
    public int compare(UserScore userScore1, UserScore userScore2) {
        return userScore1.getScore().compareTo(userScore2.getScore());
    }
}
