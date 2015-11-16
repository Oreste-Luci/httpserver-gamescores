package com.oresteluci.scores.domain;

import java.math.BigInteger;

/**
 * POJO that keeps the relationship between the user, level and score.
 *
 * @author Oreste Luci
 */
public class UserScore {

    private int levelId;
    private int userId;
    private BigInteger score;

    public UserScore(int levelId, int userId, BigInteger score) {
        this.levelId = levelId;
        this.userId = userId;
        this.score = score;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigInteger getScore() {
        return score;
    }

    public void setScore(BigInteger score) {
        this.score = score;
    }
}
