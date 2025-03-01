package com.playstarnet.essentials.feat.api;

import com.google.gson.*;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.util.Constants;
import com.playstarnet.essentials.util.StaticValues;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class QueryURL {
    private static final AtomicLong lastPingTime = new AtomicLong(0);
    private static final long PING_COOLDOWN_MS = 1000; // 1 second cooldown
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER =
            new PoolingHttpClientConnectionManager();
    private static final CloseableHttpClient HTTP_CLIENT =
            HttpClients.custom().setConnectionManager(CONNECTION_MANAGER).build();
    private static final URL API_URL;

    static {
        try {
            API_URL = new URL("http://api.blueninjar.com/api/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void asyncLifePing(String playerUUID, String apiCode) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPingTime.get() < PING_COOLDOWN_MS) {
            return; // Skip if within cooldown
        }
        lastPingTime.set(currentTime);

        CompletableFuture.runAsync(() -> {
            try {
                HttpGet request = new HttpGet(API_URL + "live/" + playerUUID + "/" + apiCode);
                addPlayerHeader(request);

                try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String jsonContent = EntityUtils.toString(response.getEntity());
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        if (jsonObject.has("success")) {
                            API.living = true;
                        }
                    }
                }
            } catch (Exception e) {
                StarNetEssentials.logger().error("Error in asyncLifePing: ", e);
            }
        });
    }

    public static void asyncCreateUser(String playerUUID, String userName) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpGet request = new HttpGet(API_URL + "create/" + playerUUID + "/" + userName);
                addPlayerHeader(request);

                try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    String jsonContent = EntityUtils.toString(response.getEntity());

                    if (statusCode == 200) {
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        if (jsonObject.has("code")) {
                            API.API_KEY = jsonObject.get("code").getAsString();
                            API.living = true;
                        }
                    } else if (statusCode == 400) {
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        if (jsonObject.has("error")) {
                            API.checkingUser = false;
                            String errorMessage = jsonObject.get("message").getAsString();
                            if (errorMessage.equals("User already exists") || errorMessage.equals("Invalid UUID or code")) {
                                API.living = true;
                                StarNetEssentials.logger().error("API Error: " + errorMessage);
                            }
                        }
                    }
                }
            } catch (IOException | ParseException | JsonSyntaxException e) {
                handleAPIError(e, "asyncCreateUser");
            }
        });
    }

    public static void asyncDestroy(String playerUUID, String apiCode) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpGet request = new HttpGet(API_URL + "end/" + playerUUID + "/" + apiCode);
                addPlayerHeader(request);

                try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String jsonContent = EntityUtils.toString(response.getEntity());
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                            API.living = false;
                        }
                    }
                }
            } catch (IOException | ParseException | JsonSyntaxException e) {
                handleAPIError(e, "asyncDestroy");
            }
        });
    }

    public static void asyncPlayerList() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpGet request = new HttpGet(API_URL + "users/");
                addPlayerHeader(request);

                try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String jsonContent = EntityUtils.toString(response.getEntity());
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        JsonArray jsonElements = jsonObject.get("users").getAsJsonArray();

                        if (!jsonElements.isEmpty()) {
                            StaticValues.users.clear();
                            for (JsonElement jsonElement : jsonElements) {
                                JsonObject element = jsonElement.getAsJsonObject();
                                String uuid = element.get("uuid").getAsString();
                                String name = element.get("name").getAsString();
                                StaticValues.users.put(uuid, name);
                            }
                        }
                    }
                }
            } catch (IOException | ParseException | JsonSyntaxException e) {
                handleAPIError(e, "asyncPlayerList");
            }
        });
    }

    public static void asyncTeam() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpGet request = new HttpGet(API_URL + "users/team");
                addPlayerHeader(request);

                try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String jsonContent = EntityUtils.toString(response.getEntity());
                        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                        JsonObject jsonTeamObj = jsonObject.getAsJsonObject("team");
                        JsonObject teamObj = jsonTeamObj.getAsJsonObject("team");

                        addToList(StaticValues.translators, teamObj.getAsJsonArray("translator"));
                        addToList(StaticValues.teamMembers, teamObj.getAsJsonArray("team"));
                        addToList(StaticValues.devs, teamObj.getAsJsonArray("dev"));
                    }
                }
            } catch (IOException | ParseException | JsonSyntaxException e) {
                handleAPIError(e, "asyncTeam");
            }
        });
    }

    private static void addPlayerHeader(HttpGet request) {
        if (StarNetEssentials.client().player != null) {
            request.addHeader(Constants.MOD_NAME + " v" + Constants.VERSION,
                    StarNetEssentials.client().player.getName().getString());
        }
    }

    private static void handleAPIError(Exception e, String methodName) {
        if (e instanceof IOException) {
            StarNetEssentials.logger().error("API Error in " + methodName + ": " + e.getMessage() +
                    "\nChecking back in 30 seconds...");
            API.serverUnreachable = true;
        } else {
            StarNetEssentials.logger().error("API Exception in " + methodName + ": ", e);
        }
    }

    private static void addToList(java.util.List<String> list, JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            String value = element.getAsString();
            if (!list.contains(value)) {
                list.add(value);
            }
        }
    }
}
