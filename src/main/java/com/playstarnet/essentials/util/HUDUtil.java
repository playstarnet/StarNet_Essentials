package com.playstarnet.essentials.util;

import com.playstarnet.essentials.StarNetEssentials;
import net.minecraft.world.BossEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HUDUtil {
    public static String getCurrentRoomName() {
        if (StarNetEssentials.connected()) {
            BossEvent bar = StarNetEssentials.client().getSingleplayerServer().getCustomBossEvents().getEvents().stream().findFirst().get();
            String text = bar.getName().getString();

            // Updated pattern to account for optional color codes like &b
            Pattern pattern = Pattern.compile("(&[0-9a-fk-or])*\\w*'s Island", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                // Remove color codes and "'s Island" to return only the player's name
                return matcher.group().replaceAll("(&[0-9a-fk-or])", "").replace("'s Island", "");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
