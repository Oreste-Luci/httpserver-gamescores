package com.oresteluci.scores.controller;

import com.oresteluci.scores.injection.AutoBean;
import com.oresteluci.scores.injection.AutoInject;
import com.oresteluci.scores.service.LoginService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oresteluci.scores.handler.HandlerDispatcher.LOGIN_REGEX_PATH;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Oreste Luci
 */
public @AutoBean class LoginController extends AbstractController {

    @AutoInject
    private LoginService loginService;

    /**
     * Reads user id path param and returns new Session Key.
     * Path parameters: /<userid>/login
     * Response: return the new Session Key in the body.
     *
     * @param httpExchange
     * @throws IOException
     */
    public void execute(HttpExchange httpExchange) throws IOException {

        Integer userId = null;

        Pattern p = Pattern.compile(LOGIN_REGEX_PATH);
        Matcher m = p.matcher(httpExchange.getRequestURI().getPath());

        // Extracting user id from path
        if (m.find()) {
            userId = new Integer(m.group(1));
        }

        String sessionKey = loginService.login(userId);

        // Returning session key
        createResponse(httpExchange, HTTP_OK, sessionKey);
    }
}
