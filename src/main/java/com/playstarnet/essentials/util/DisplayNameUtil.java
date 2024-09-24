package com.playstarnet.essentials.util;

import com.playstarnet.essentials.GitHubJsonFetcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayNameUtil {
    public static String ignFromDisplayName(String content) {
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(content);
        String username = matcher.find() ? matcher.group(0) : null;
        if (username == null || username.isEmpty()) {
            return content;
        } else return username;
    }

    public static String nameFromChatMessage(String chatMessage) {
        String[] message = chatMessage.split(" ");
        if (message.length > 1) {
            String username = chatMessage.split(" ")[1];
            username = username.replaceAll("^\\S+ ", "");
            if (username.isEmpty()) {
                return "";
            } else return username;
        } else return chatMessage;
    }

    public static MutableComponent withBadges(MutableComponent text, String playerName, boolean tooltip) throws Exception {
        String playerID = "";
        MutableComponent newComponent = Component.empty();

        if (!playerName.isEmpty()) {
            String jsonUrl = "https://raw.githubusercontent.com/playstarnet/StarNet_Essentials/refs/heads/main/users.json";
            String jsonString = GitHubJsonFetcher.fetchJsonFromGitHub(jsonUrl);

            Map<String, String> users = GitHubJsonFetcher.parseJsonToUserMap(jsonString);
            Set<String> friends = GitHubJsonFetcher.parseJsonToSpecialSet("friends", jsonString);
            Set<String> devs = GitHubJsonFetcher.parseJsonToSpecialSet("devs", jsonString);
            Set<String> teamMembers = GitHubJsonFetcher.parseJsonToSpecialSet("teamMembers", jsonString);
            Set<String> translators = GitHubJsonFetcher.parseJsonToSpecialSet("translators", jsonString);

            for (Map.Entry<String, String> entry : users.entrySet()) {
                if (entry.getValue().equals(playerName)) {
                    playerID = entry.getKey();
                }
            }

            if (friends.contains(playerName)) Chars.FRIEND.addBadge(newComponent, tooltip);
            if (devs.contains(playerID)) Chars.DEV.addBadge(newComponent, tooltip);
            else if (teamMembers.contains(playerID)) Chars.TEAM.addBadge(newComponent, tooltip);
            else if (translators.contains(playerID)) Chars.TRANSLATOR.addBadge(newComponent, tooltip);
            else if (users.containsKey(playerID)) Chars.USER.addBadge(newComponent, tooltip);

            return (tooltip) ? newComponent.append(text) : text.append(" ").append(newComponent);
        }
        return text;
    }
}
