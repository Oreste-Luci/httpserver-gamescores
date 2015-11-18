package com.oresteluci.scores.test.handler;

import com.oresteluci.scores.controller.HighScoreController;
import com.oresteluci.scores.controller.LoginController;
import com.oresteluci.scores.controller.ScoreController;
import com.oresteluci.scores.domain.UserScore;
import com.oresteluci.scores.handler.HandlerDispatcher;
import com.oresteluci.scores.service.LoginService;
import com.oresteluci.scores.service.ScoreService;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oreste Luci
 */
public class HandlerDispatcherTest {

    @Test
    public void loginTest() throws Exception {

        String sessionKey = "ABC";

        LoginController loginController = new LoginController();

        LoginService loginService = mock(LoginService.class);
        when(loginService.login(100)).thenReturn(sessionKey);

        ReflectionTestUtils.setField(loginController, "loginService", loginService);

        HandlerDispatcher handlerDispatcher = new HandlerDispatcher();

        ReflectionTestUtils.setField(handlerDispatcher, "loginController", loginController);

        HttpExchange httpExchange = mockHttpExchange("/100/login", HandlerDispatcher.REQUEST_METHOD_GET, null);

        handlerDispatcher.handle(httpExchange);

        ByteArrayOutputStream out = (ByteArrayOutputStream)httpExchange.getResponseBody();

        String responseSessionKey = new String(out.toByteArray());

        assertTrue(sessionKey.equalsIgnoreCase(responseSessionKey));
    }

    @Test
    public void scoreTest() throws Exception {

        int userId = 100;

        HandlerDispatcher handlerDispatcher = new HandlerDispatcher();

        // Creating session key
        String sessionKey = "ABC";
        LoginController loginController = new LoginController();
        LoginService loginService = mock(LoginService.class);
        when(loginService.login(userId)).thenReturn(sessionKey);

        ReflectionTestUtils.setField(loginController, "loginService", loginService);
        ReflectionTestUtils.setField(handlerDispatcher, "loginController", loginController);

        HttpExchange httpExchange = mockHttpExchange("/100/login", HandlerDispatcher.REQUEST_METHOD_GET, null);
        handlerDispatcher.handle(httpExchange);
        ByteArrayOutputStream out = (ByteArrayOutputStream)httpExchange.getResponseBody();
        String responseSessionKey = new String(out.toByteArray());


        // Sending score
        ScoreController scoreController = new ScoreController();
        ScoreService scoreService = mock(ScoreService.class);
        when(scoreService.addScore(900,sessionKey,200)).thenReturn(true);
        when(scoreService.addScore(900,sessionKey,1000)).thenReturn(true);

        ReflectionTestUtils.setField(scoreController, "scoreService", scoreService);
        ReflectionTestUtils.setField(handlerDispatcher, "scoreController", scoreController);

        httpExchange = mockHttpExchange("/900/score?sessionkey=" + sessionKey, HandlerDispatcher.REQUEST_METHOD_POST, "2000");
        handlerDispatcher.handle(httpExchange);
        httpExchange = mockHttpExchange("/900/score?sessionkey=" + sessionKey, HandlerDispatcher.REQUEST_METHOD_POST, "1000");
        handlerDispatcher.handle(httpExchange);


        // Verifying score
        HighScoreController highScoreController = new HighScoreController();
        List<UserScore> scores = new ArrayList<>();
        scores.add(new UserScore(900,userId, BigInteger.valueOf(3000)));
        when(scoreService.getHighestScores(900)).thenReturn(scores);

        ReflectionTestUtils.setField(highScoreController, "scoreService", scoreService);
        ReflectionTestUtils.setField(handlerDispatcher, "highScoreController", highScoreController);

        httpExchange = mockHttpExchange("/900/highscorelist", HandlerDispatcher.REQUEST_METHOD_GET, null);
        handlerDispatcher.handle(httpExchange);

        out = (ByteArrayOutputStream)httpExchange.getResponseBody();
        String scoreList = new String(out.toByteArray());

        assertTrue("100=3000".equalsIgnoreCase(scoreList) && sessionKey.equalsIgnoreCase(responseSessionKey));
    }

    private HttpExchange mockHttpExchange(String uri, String requestMethod, String body) throws URISyntaxException, IOException {

        HttpExchange httpExchange = mock(HttpExchange.class);

        URI requestURI = new URI(uri);

        when(httpExchange.getRequestURI()).thenReturn(requestURI);
        when(httpExchange.getRequestMethod()).thenReturn(requestMethod);

        if (body != null) {
            InputStream inputStream = new ByteArrayInputStream( body.getBytes() );
            when(httpExchange.getRequestBody()).thenReturn(inputStream);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(out);

        return httpExchange;
    }
}
