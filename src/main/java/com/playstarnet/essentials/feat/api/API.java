package com.playstarnet.essentials.feat.api;

import com.playstarnet.essentials.StarNetEssentials;

public class API {
    public static boolean serverUnreachable = false;
    public static boolean enabled = false;
    public static boolean living = false;
    public static boolean checkingUser = false;
    public static String API_KEY = "";
    private static long lastPlayerListCall = 0;
    private static final long PLAYER_LIST_COOLDOWN = 5000; // 5 seconds

    public static void tick() {
        if (!enabled || serverUnreachable) return;
        if (living || !API_KEY.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        if (!checkingUser) {
            QueryURL.asyncCreateUser(StarNetEssentials.player().getStringUUID(), StarNetEssentials.player().getName().getString());
            checkingUser = true;
        }
        if (currentTime - lastPlayerListCall > PLAYER_LIST_COOLDOWN) {
            QueryURL.asyncPlayerList();
            lastPlayerListCall = currentTime;
        }
        QueryURL.asyncTeam();
    }

    public static void live() {
        if (!enabled || serverUnreachable) return;
        if ((!living || API_KEY.isEmpty()) && !checkingUser) { QueryURL.asyncCreateUser(StarNetEssentials.player().getStringUUID(), StarNetEssentials.player().getName().getString()); checkingUser = true; }
        QueryURL.asyncLifePing(StarNetEssentials.player().getStringUUID(), API_KEY);
        QueryURL.asyncPlayerList();
    }

    public static void end() {
        if (StarNetEssentials.player() != null) QueryURL.asyncDestroy(StarNetEssentials.player().getStringUUID(), API_KEY);
        enabled = false;
    }

    public static void modTeam() {
        QueryURL.asyncTeam();
    }
}
