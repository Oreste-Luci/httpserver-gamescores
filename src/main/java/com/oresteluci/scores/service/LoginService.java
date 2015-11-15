package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.injection.AutoBean;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Oreste Luci
 */
public @AutoBean class LoginService {

    private Map<String,UserSession> userSessions = new HashMap<>();

    public String login(int userId) {

        // Creates random session key
        String sessionKey = generateRandomSessionKey();

        // Determining expiry date
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, ApplicationConfig.SESSION_KEY_TIMEOUT_MINUTES);

        UserSession userSession = new UserSession(userId, sessionKey, expiryDate.getTime());

        userSessions.put(sessionKey, userSession);

        return sessionKey;
    }

    public UserSession getUserSessionByKey(String sessionKey) {

        UserSession userSession = userSessions.get(sessionKey);

        if (userSession == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();

        // Determining if sessionKey is valid
        if (cal.getTime().getTime() <= userSession.getExpiry().getTime()) {
            return userSession;
        } else {
            // Removing expired session key
            userSessions.remove(sessionKey);
            return null;
        }
    }

    /**
     * Utility method to create a random session key.
     * It is based on the universally unique identifier logic provided by java.
     * It strips the - character from the generated UUID.
     *
     * @return
     */
    private String generateRandomSessionKey() {
        return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }
}
