package com.oresteluci.scores.domain;

import java.math.BigInteger;

/**
 * POJO that keeps the relationship between the user, level and score.
 *
 * @author Oreste Luci
 */
public class UserScore implements Comparable {

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

    /**
     * Comparing scores.
     * @param obj
     * @return 1, 0 or -1 as this score is numerically less than, equal to, or greater than obj.score.
     */
    @Override
    public int compareTo(Object obj) {

        if (!(obj instanceof UserScore)) {
            throw new RuntimeException("Object is not of type UserScore");
        }

        UserScore object2 = (UserScore)obj;

        if (this.getScore() == null && object2.getScore() != null) {
            return 1;
        } else if (this.getScore() != null && object2.getScore() == null) {
            return -1;
        } else {
            return this.getScore().compareTo(object2.getScore()) * (-1);
        }
    }

}
