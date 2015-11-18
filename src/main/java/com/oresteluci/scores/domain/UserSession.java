package com.oresteluci.scores.domain;

import java.util.Date;

/**
 * @author Oreste Luci
 */
public class UserSession {

    private int userId;
    private String sessionKey;
    private Date expiry;

    public UserSession(int userId, String sessionKey, Date expiry) {
        this.userId = userId;
        this.sessionKey = sessionKey;
        this.expiry = expiry;
    }

    public int getUserId() {
        return userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Date getExpiry() {
        return expiry;
    }
}
