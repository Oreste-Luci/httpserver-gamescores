package com.oresteluci.scores.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Base controller class with common utility methods
 *
 * @author Oreste Luci
 */
public abstract class AbstractController {

    public abstract void execute(HttpExchange httpExchange) throws IOException;

    /**
     * Utility method to write response.
     *
     * @param httpExchange
     * @param statusCode
     * @param responseBody
     * @throws IOException
     */
    protected void createResponse(HttpExchange httpExchange, int statusCode, String responseBody) throws IOException {

        int responseLength = 0;

        if (responseBody != null) {
            responseLength = responseBody.length();
        }

        Headers headers = httpExchange.getResponseHeaders();

        if (headers != null) {
            headers.set("Content-Type", "text/html");
        }

        httpExchange.sendResponseHeaders(statusCode, responseLength);
        OutputStream os = httpExchange.getResponseBody();

        if (responseLength > 0) {
            os.write(responseBody.getBytes());
        }

        os.close();
    }

    /**
     * Utility method to extract query parameters.
     *
     * @param query
     * @return
     */
    protected Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    /**
     * Utility method to convert InputStream to String.
     *
     * @param is
     * @return
     */
    protected String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is);
        return s.hasNext() ? s.next() : "";
    }
}
