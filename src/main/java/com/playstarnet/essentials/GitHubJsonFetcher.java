package com.playstarnet.essentials;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GitHubJsonFetcher {

    public static String fetchJsonFromGitHub(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return content.toString();
    }

    public static Map<String, String> parseJsonToUserMap(String jsonString) {
        JsonObject rootNode = JsonParser.parseString(jsonString).getAsJsonObject();
        Map<String, String> userMap = new HashMap<>();
        JsonObject usersNode = rootNode.getAsJsonObject("users");
        if (usersNode != null) {
            for (Map.Entry<String, com.google.gson.JsonElement> entry : usersNode.entrySet()) {
                String uuid = entry.getKey();
                String username = entry.getValue().getAsString();
                userMap.put(uuid, username);
            }
        }
        return userMap;
    }

    public static Set<String> parseJsonToSpecialSet(String node, String jsonString) {
        JsonObject rootNode = JsonParser.parseString(jsonString).getAsJsonObject();
        Set<String> members = new HashSet<>();
        if (rootNode.has(node)) {
            for (com.google.gson.JsonElement element : rootNode.getAsJsonArray(node)) {
                members.add(element.getAsString());
            }
        }

        return members;
    }
}
