package com.playstarnet.essentials.feat.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.location.Location;
import com.playstarnet.essentials.util.HUDUtil;

import java.time.Instant;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordManager {
    public static boolean active = false;
    private static Instant start;
    DiscordRPC discord = DiscordRPC.INSTANCE; //discord rich presence instance
    String appID = "1287574652503195759"; //app id for discord, you should probably NOT change this
    String steamId = ""; //this is useless because minecraft isn't a steam game, this is just for the sake of
    // passing it in methods
    DiscordEventHandlers handlers = new DiscordEventHandlers(); //discord event handler
    Timer t = new Timer();
    Long start_time = System.currentTimeMillis() / 1000;
    String largeImageKey;


    public void start() {
        if (!active && GeneralConfigModel.DISCORD_RPC.value) {
//            StarNetEssentials.logger().info("Starting Discord RPC client...");
            handlers.ready = (user) -> {
                StarNetEssentials.logger().info("Discord RPC is ready");
                active = true;
                start = Instant.now();
            };
            handlers.disconnected = (errorCode, message) -> {
                active = false;
            };
            discord.Discord_Initialize(appID, handlers, true, steamId);

            basicDiscordPresence();
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    discord.Discord_RunCallbacks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "RPC-Callback-Handler").start();

            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateDiscordPresence();
                }
            }, 5000, 100);
        }
    }

    public void basicDiscordPresence() {
        Location.check();
        Location loc = StarNetEssentials.location();
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.largeImageKey = largeImageKey;
        presence.details = loc.description;
        presence.state = loc.name.contains("<player>") ? loc.name.replace("<player>", Objects.requireNonNull(HUDUtil.getCurrentRoomName())) : loc.name;
        presence.largeImageKey = loc.largeIcon.key();
        presence.largeImageText = "StarNet Essentials v" + StarNetEssentials.version();
        presence.instance = 1;
        presence.startTimestamp = start_time;

        //all of this stuff here is useless
        presence.partyId = "priv_party";
        presence.matchSecret = "abXyyz";
        presence.joinSecret = "moonSqikCklaw";
        presence.spectateSecret = "moonSqikCklawkLopalwdNq";
        discord.Discord_UpdatePresence(presence);
    }

    public void updateDiscordPresence() {
        if (active && GeneralConfigModel.DISCORD_RPC.value) {
            basicDiscordPresence();
        } else start();
    }

    public void stop() {
        discord.Discord_ClearPresence();
        active = false;
    }
}