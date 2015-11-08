package com.oresteluci.scores.domain;

/**
 * POJO that keeps the relationship between user, session key and its expiry date
 *
 * @author Oreste Luci
 */
public class UserSessionKey {

    private Integer userId;
    private String sessionKey;
    private Long expiryDate;

    public UserSessionKey(Integer userId, String sessionKey, Long expiryDate) {
        this.userId = userId;
        this.sessionKey = sessionKey;
        this.expiryDate = expiryDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }
}
