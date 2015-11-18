package com.oresteluci.scores.test.service;

import com.oresteluci.scores.config.ApplicationConfig;
import com.oresteluci.scores.domain.UserSession;
import com.oresteluci.scores.service.LoginService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oreste Luci
 */
public class LoginServiceTests {

    ApplicationConfig applicationConfig = new ApplicationConfig();

    @Test
    public void loginTest() {

        LoginService loginService = new LoginService();

        ReflectionTestUtils.setField(loginService, "applicationConfig", applicationConfig);

        String sessionKey = loginService.login(100);

        assertTrue(sessionKey != null);
    }

    @Test
    public void getUserSessionByKeyTest() {

        LoginService loginService = new LoginService();

        ReflectionTestUtils.setField(loginService, "applicationConfig", applicationConfig);

        int userId = 100;

        Date date = new Date();

        String sessionKey = loginService.login(userId);

        UserSession userSession = loginService.getUserSessionByKey(sessionKey);

        assertTrue(userSession.getSessionKey().equalsIgnoreCase(sessionKey)
                && userSession.getUserId() == userId
                && userSession.getExpiry().getTime() > date.getTime());
    }

    @Test
    public void getUserSessionByKeyExpiredTest() {

        int userId = 100;

        LoginService loginService = new LoginService();

        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.getSessionKeyTimeoutMinutes()).thenReturn(0);

        ReflectionTestUtils.setField(loginService, "applicationConfig", applicationConfig);

        String sessionKey = loginService.login(userId);

        try {
            Thread.sleep(1000); //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        UserSession userSession = loginService.getUserSessionByKey(sessionKey);

        assertTrue(userSession == null);
    }
}
