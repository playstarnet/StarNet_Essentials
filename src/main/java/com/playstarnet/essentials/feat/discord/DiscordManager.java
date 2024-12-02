package com.playstarnet.essentials.feat.discord;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.location.Location;
import com.playstarnet.essentials.util.HUDUtil;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordManager {
    public static boolean active = false;
    private static OffsetDateTime startTime;
    private static final String appID = "1287574652503195759";
    private static IPCClient ipcClient;
    private static Timer updateTimer;
    Long start_time = System.currentTimeMillis() / 1000;
    OffsetDateTime startTimestamp = OffsetDateTime.ofInstant(Instant.ofEpochSecond(start_time), ZoneId.systemDefault());

    public void start() {
        if (!active && GeneralConfigModel.DISCORD_RPC.value) {
            ipcClient = new IPCClient(Long.parseLong(appID));

            ipcClient.setListener(new IPCListener() {
                @Override
                public void onReady(IPCClient client) {
                    StarNetEssentials.logger().info("Discord IPC is ready.");
                    active = true;
                    startTime = OffsetDateTime.now();
                    basicDiscordPresence();
                }

                @Override
                public void onDisconnect(IPCClient client, Throwable throwable) {
                    StarNetEssentials.logger().warn("Discord IPC disconnected: " + (throwable != null ? throwable.getMessage() : "Unknown reason"));
                    active = false;
                }

                @Override
                public void onClose(IPCClient client, JSONObject json) {
                    StarNetEssentials.logger().warn("Discord IPC closed: " + json);
                    active = false;
                }
            });

            try {
                ipcClient.connect();
            } catch (Exception e) {
                StarNetEssentials.logger().error("Failed to connect to Discord IPC: " + e.getMessage());
                return;
            }

            if (updateTimer == null) {
                updateTimer = new Timer();
                updateTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        updateDiscordPresence();
                    }
                }, 5000, 10000);
            }
        }
    }

    public void basicDiscordPresence() {
        if (ipcClient == null || ipcClient.getStatus() != PipeStatus.CONNECTED) return;

        Location.check();
        Location loc = StarNetEssentials.location();

        // Create the presence builder
        RichPresence.Builder presenceBuilder = new RichPresence.Builder();
        presenceBuilder.setDetails(loc.description);
        presenceBuilder.setState(loc.name.contains("<player>") ? loc.name.replace("<player>", Objects.requireNonNull(HUDUtil.getCurrentRoomName())) : loc.name);
        presenceBuilder.setLargeImage(loc.largeIcon.key(), "StarNet Essentials v" + StarNetEssentials.version());
        presenceBuilder.setInstance(true);
        presenceBuilder.setStartTimestamp(startTimestamp);

        // Add buttons to the presence
        presenceBuilder.setButton1Text("Join StarNet");
        presenceBuilder.setButton1Url("http://discord.playstarnet.com");
        presenceBuilder.setButton2Text("Get StarNet Essentials");
        presenceBuilder.setButton2Url("https://modrinth.com/mod/starnet-essentials");

        presenceBuilder.setMatchSecret("abXyyz");
        presenceBuilder.setJoinSecret("moonSqikCklaw");
        presenceBuilder.setSpectateSecret("moonSqikCklawkLopalwdNq");

        ipcClient.sendRichPresence(presenceBuilder.build());

        try {
            ipcClient.sendRichPresence(presenceBuilder.build());
        } catch (Exception e) {
            StarNetEssentials.logger().info("Failed to update Discord presence: " + e.getMessage());
        }
    }
    public void updateDiscordPresence() {
        if (active && GeneralConfigModel.DISCORD_RPC.value) {
            basicDiscordPresence();
        } else {
            stop();
        }
    }

    public void stop() {
        if (ipcClient != null && ipcClient.getStatus() == PipeStatus.CONNECTED) {
            try {
                ipcClient.close();
                StarNetEssentials.logger().info("Discord IPC stopped.");
            } catch (Exception e) {
                StarNetEssentials.logger().warn("Failed to properly close Discord IPC: " + e.getMessage());
            }
        }
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
        active = false;
    }
}
