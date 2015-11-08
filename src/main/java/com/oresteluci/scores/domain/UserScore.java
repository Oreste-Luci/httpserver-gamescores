package com.oresteluci.scores.domain;

import java.math.BigInteger;

/**
 * @author Oreste Luci
 */
public class UserScore {

    private Integer levelId;
    private Integer userId;
    private BigInteger score;

    public UserScore(Integer levelId, Integer userId, BigInteger score) {
        this.levelId = levelId;
        this.userId = userId;
        this.score = score;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigInteger getScore() {
        return score;
    }

    public void setScore(BigInteger score) {
        this.score = score;
    }

}
