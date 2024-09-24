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
            Pattern pattern = Pattern.compile("\\w*'s Island", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group().replace("'s Island", "");
            } else return null;
        } else return null;
    }
}
