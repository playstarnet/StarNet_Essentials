package com.playstarnet.essentials;

import com.playstarnet.essentials.feat.config.StarNetPlusConfig;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.discord.DiscordManager;
import com.playstarnet.essentials.feat.keyboard.HPKeybinds;
import com.playstarnet.essentials.feat.lifecycle.Lifecycle;
import com.playstarnet.essentials.feat.lifecycle.Task;
import com.playstarnet.essentials.feat.location.Location;
import com.playstarnet.essentials.util.Constants;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class StarNetEssentials implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    private static final ArrayList<String> debugUsers = new ArrayList<>();
    public static DiscordManager DISCORD_MANAGER = new DiscordManager();
    private static final StarNetPlusConfig CONFIG = new StarNetPlusConfig();
    private static Location LOCATION = Location.UNKNOWN;
    private static Lifecycle LIFECYCLE;
    private static final HPKeybinds KEYBINDS = new HPKeybinds();

    @Override
    public void onInitializeClient() {
        Constants.MOD_MENU_PRESENT = FabricLoader.getInstance().isModLoaded("modmenu");

        LIFECYCLE = new Lifecycle();

        try {
            if (GeneralConfigModel.DISCORD_RPC.value && !Minecraft.ON_OSX) DISCORD_MANAGER.start();
        } catch (Error err) {
            StarNetEssentials.logger().info(err);
            return;
        }

        lifecycle()
                .add(Task.of(Location::check, 40))
                .add(Task.of(() -> {
                    if (!Minecraft.ON_OSX) {
                        try {
                            if (DiscordManager.active) DISCORD_MANAGER.updateDiscordPresence();
                            if (DiscordManager.active && !GeneralConfigModel.DISCORD_RPC.value) DISCORD_MANAGER.stop();
                            if (!DiscordManager.active && GeneralConfigModel.DISCORD_RPC.value) DISCORD_MANAGER.start();
                        } catch (Error err) {
                            StarNetEssentials.logger().error(err);
                        }
                    }
                }, 10))
                .add(Task.of(() -> {
                    if (StarNetEssentials.connected()) {
                        KEYBINDS.tick();
                    }
                }, 0));
    }

    public static boolean connected() {
        ServerData server = Minecraft.getInstance().getCurrentServer();
        if (server != null) {
            return server.ip.toLowerCase().endsWith("playstarnet.com") && !server.ip.toLowerCase().contains("event");
        } else return false;
    }

    public static String version() {
        return String.valueOf(
            FabricLoader.getInstance().getModContainer(Constants.MOD_ID).get().getMetadata().getVersion()
        );
    }

    public static Logger logger() { return LOGGER; }
    public static Minecraft client() { return Minecraft.getInstance(); }
    public static LocalPlayer player() { return client().player; }
    public static StarNetPlusConfig config() { return CONFIG; }
    public static Lifecycle lifecycle() { return LIFECYCLE; }
    public static Location location() { return LOCATION; }
    public static void setLocation(Location l) { LOCATION = l; }
}