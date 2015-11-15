package com.oresteluci.scores.test.integrated;

import com.oresteluci.scores.Application;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

/**
 * @author Oreste Luci
 */
public class ApplicationTests {

    private static final int SERVER_PORT = 8585;
    private static final String SERVER_URL = "http://localhost:" + ApplicationTests.SERVER_PORT;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Application application = new Application();
        //application.runServer(ApplicationTests.SERVER_PORT, Executors.newCachedThreadPool());
    }

    @Test
    public void loginTest() throws IOException {

        String url = ApplicationTests.SERVER_URL + "/100/login";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);

        CloseableHttpResponse response = httpclient.execute(httpget);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {

            fail("Wrong Status Code");

        } else {

            HttpEntity entity = response.getEntity();
            String responseBody = convertStreamToString(entity.getContent());

            assertTrue(responseBody != null && responseBody.length() > 0);
        }
    }

    @Test
    public void loginWrongURLTest() throws IOException {

        String url = ApplicationTests.SERVER_URL + "/100/log";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);

        CloseableHttpResponse response = httpclient.execute(httpget);

        int statusCode = response.getStatusLine().getStatusCode();

        assertTrue(statusCode == 404);
    }

    @Test
    public void scoreTest() throws IOException {

        int keyCount =20;

        // Creating SessionKeys
        List<String> sessionKeys = getSessionKeys(keyCount);

        assertTrue(sessionKeys != null && sessionKeys.size() == keyCount);

        // Posting Scores
        int score = 1000;
        for (String sessionKey : sessionKeys) {

            String url = ApplicationTests.SERVER_URL + "/5001/score?sessionkey=" + sessionKey;

            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new StringEntity("" + score));

            httpclient.execute(httpPost);

            score += 10;
        }

        // Getting scores for level
        String url = ApplicationTests.SERVER_URL + "/5001/highscorelist";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);

        CloseableHttpResponse response = httpclient.execute(httpget);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {

            fail("Wrong Status Code");

        } else {

            HttpEntity entity = response.getEntity();
            String[] scores = convertStreamToString(entity.getContent()).split(",");

            assertTrue(scores.length == 15);

            score = 1190;
            int j = 0;
            for (int i=keyCount-1; i>=5; i--) {

                String userScore = "10" + i + "=" + score;

                assertTrue(userScore.equalsIgnoreCase(scores[j]));

                j++;
                score -= 10;
            }
        }
    }

    private List<String> getSessionKeys(int amount) throws IOException {

        List<String> sessionKeys = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        for (int i=0; i<amount; i++) {

            String url = ApplicationTests.SERVER_URL + "/10" + i + "/login";

            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();
            String sessionKey = convertStreamToString(entity.getContent());

            sessionKeys.add(sessionKey);
        }

        return sessionKeys;
    }


    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is);
        return s.hasNext() ? s.next() : "";
    }
}
