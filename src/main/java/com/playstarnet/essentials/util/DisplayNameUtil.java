package com.playstarnet.essentials.util;

import com.playstarnet.essentials.GitHubJsonFetcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
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

        for (Map.Entry<String, String> entry : StaticValues.users.entrySet()) {
            if (entry.getValue().equals(playerName)) {
                playerID = entry.getKey();
            }
        }

        if (StaticValues.devs.contains(playerID)) Chars.DEV.addBadge(newComponent, tooltip);
        else if (StaticValues.teamMembers.contains(playerID)) Chars.TEAM.addBadge(newComponent, tooltip);
        else if (StaticValues.translators.contains(playerID)) Chars.TRANSLATOR.addBadge(newComponent, tooltip);
        else if (StaticValues.users.containsKey(playerID)) Chars.USER.addBadge(newComponent, tooltip);

        return tooltip ? newComponent.append(text) : text.append(" ").append(newComponent);
    }
}
