package com.oresteluci.scores.test.handler;

import com.oresteluci.scores.handler.HandlerDispatcher;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oreste Luci
 */
public class HandlerDispatcherTest {

    @Test
    public void loginTest() throws Exception {

        HandlerDispatcher handlerDispatcher = new HandlerDispatcher();

        HttpExchange httpExchange = mockHttpExchange("/100/login", HandlerDispatcher.REQUEST_METHOD_GET, null);

        handlerDispatcher.handle(httpExchange);

        ByteArrayOutputStream out = (ByteArrayOutputStream)httpExchange.getResponseBody();

        String sessionKey = new String(out.toByteArray());

        assertTrue(sessionKey != null && sessionKey.length() > 0);
    }

    @Test
    public void scoreTest() throws Exception {

        HandlerDispatcher handlerDispatcher = new HandlerDispatcher();

        // Creating session key
        HttpExchange httpExchange = mockHttpExchange("/100/login", HandlerDispatcher.REQUEST_METHOD_GET, null);
        handlerDispatcher.handle(httpExchange);
        ByteArrayOutputStream out = (ByteArrayOutputStream)httpExchange.getResponseBody();
        String sessionKey = new String(out.toByteArray());

        // Sending score
        httpExchange = mockHttpExchange("/900/score?sessionkey=" + sessionKey, HandlerDispatcher.REQUEST_METHOD_POST, "2000");
        handlerDispatcher.handle(httpExchange);
        httpExchange = mockHttpExchange("/900/score?sessionkey=" + sessionKey, HandlerDispatcher.REQUEST_METHOD_POST, "1000");
        handlerDispatcher.handle(httpExchange);

        // Verifying score
        httpExchange = mockHttpExchange("/900/highscorelist", HandlerDispatcher.REQUEST_METHOD_GET, null);
        handlerDispatcher.handle(httpExchange);

        out = (ByteArrayOutputStream)httpExchange.getResponseBody();
        String scoreList = new String(out.toByteArray());

        assertTrue("100=3000".equalsIgnoreCase(scoreList));
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
