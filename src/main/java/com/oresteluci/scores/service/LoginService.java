package com.oresteluci.scores.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.injection.AutoComponent;
import com.oresteluci.scores.injection.AutoInject;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Oreste Luci
 */
@AutoComponent
public class LoginService {

    @AutoInject
    private ApplicationConfig applicationConfig;

    // ConcurrentHashMap is used to handle concurrent access. Updates do not interfere with reads.
    // The LoginService does not update values in hte map. It adds, reads and removes, but no updates.
    private Map<String,UserSession> userSessions = new ConcurrentHashMap<>();

    public String login(int userId) {

        // Creates random session key
        String sessionKey = generateRandomSessionKey();

        // Determining expiry date
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, applicationConfig.getSessionKeyTimeoutMinutes());

        UserSession userSession = new UserSession(userId, sessionKey, expiryDate.getTime());

        // Since ConcurrentHashMap is being used no need to synchronize this insert operation
        userSessions.put(sessionKey, userSession);

        return sessionKey;
    }

    public UserSession getUserSessionByKey(String sessionKey) {

        // No need to synchronize the read operation
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
            // No concurrency issues since if an already removed key is removed nothing will happen.
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
