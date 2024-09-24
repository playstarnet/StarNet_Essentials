package com.playstarnet.essentials;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static Map<String, String> parseJsonToUserMap(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        Map<String, String> userMap = new HashMap<>();
        JsonNode usersNode = rootNode.get("users");
        if (usersNode != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = usersNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String uuid = field.getKey();
                String username = field.getValue().asText();
                userMap.put(uuid, username);
            }
        }
        return userMap;
    }

    public static Set<String> parseJsonToSpecialSet(String node, String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        Set<String> devs = new HashSet<>();
        JsonNode devsNode = rootNode.get(node);
        if (devsNode != null) {
            for (JsonNode devId : devsNode) {
                devs.add(devId.asText());
            }
        }

        return devs;
    }
}
