package com.playstarnet.essentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.playstarnet.essentials.feat.api.API;
import com.playstarnet.essentials.feat.config.StarNetPlusConfig;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.discord.DiscordManager;
import com.playstarnet.essentials.feat.keyboard.HPKeybinds;
import com.playstarnet.essentials.feat.lifecycle.Lifecycle;
import com.playstarnet.essentials.feat.lifecycle.Task;
import com.playstarnet.essentials.feat.location.Location;
import com.playstarnet.essentials.util.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public class StarNetEssentials implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
	private static final ArrayList<String> debugUsers = new ArrayList<>();
	public static DiscordManager DISCORD_MANAGER = new DiscordManager();
	private static final StarNetPlusConfig CONFIG = new StarNetPlusConfig();
	private static Location LOCATION = Location.UNKNOWN;
	private static Lifecycle LIFECYCLE;
	private static final boolean debugMode = false;
	private static boolean updateChecked = false;
	private static boolean soundCached = false;
	private static final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final File LOG_FILE = new File("./starnet_riding_log.txt");

	@Override
	public void onInitializeClient() {
		Constants.MOD_MENU_PRESENT = FabricLoader.getInstance().isModLoaded("modmenu");
		new HPKeybinds();
		LIFECYCLE = new Lifecycle();

		// Discord Manager initialization with error handling
		try {
			if (GeneralConfigModel.DISCORD_RPC.value) {
				DISCORD_MANAGER.start();
			}
		} catch (Error err) {
			logger().error("Error starting Discord Manager: ", err);
		}

		// Add lifecycle tasks
		lifecycle()
				.add(Task.of(() -> {
					if (!StarNetEssentials.connected() && API.enabled) {
						API.end();
					}
				}, 0))
				.add(Task.of(Location::check, 200))
				.add(Task.of(this::handleDiscordLifecycle, 10))
				.add(Task.of(() -> {
					if (StarNetEssentials.connected()) {
						API.enabled = true;
						API.tick();
					}
				}, 0))
				.add(Task.of(() -> {
					if (StarNetEssentials.connected()) {
						API.live();
					}
				}, 50))
				.add(Task.of(() -> {
					if (StarNetEssentials.connected() && API.serverUnreachable) {
						API.serverUnreachable = false;
						API.tick();
					}
				}, 100))
				.add(Task.of(API::modTeam, 50));
	}

	private void handleDiscordLifecycle() {
		try {
			if (DiscordManager.active) DISCORD_MANAGER.updateDiscordPresence();
			if (DiscordManager.active && !GeneralConfigModel.DISCORD_RPC.value) DISCORD_MANAGER.stop();
			if (!DiscordManager.active && GeneralConfigModel.DISCORD_RPC.value) DISCORD_MANAGER.start();
		} catch (Error err) {
			logger().error("Error in Discord lifecycle task: ", err);
		}
	}

	public static boolean connected() {
		ServerData server = Minecraft.getInstance().getCurrentServer();
		if (server != null) {
			boolean isConnected = server.ip.toLowerCase().endsWith("playstarnet.com") && !server.ip.toLowerCase().contains("event");

			if (isConnected) {
				if (!updateChecked) {
					logger().info("Performing Modrinth update check.");

					// Offload update check to background thread
					backgroundExecutor.submit(ModrinthUpdateChecker::checkForUpdates);

					updateChecked = true; // Mark as checked for this session
				}
			} else {
				// Reset when disconnecting or switching servers
				updateChecked = false;
				ModrinthUpdateChecker.resetCheck();
			}

			return isConnected;
		} else {
			// Reset when completely disconnected
			updateChecked = false;
			ModrinthUpdateChecker.resetCheck();
			return false;
		}
	}

	public static String version() {
		return String.valueOf(
				FabricLoader.getInstance().getModContainer(Constants.MOD_ID).get().getMetadata().getVersion()
		);
	}

	private static void debugLog(String message) {
		if (debugMode) {
			logger().info(message);
		}
	}

	public static Logger logger() { return LOGGER; }
	public static Minecraft client() { return Minecraft.getInstance(); }
	public static LocalPlayer player() { return client().player; }
	public static StarNetPlusConfig config() { return CONFIG; }
	public static Lifecycle lifecycle() { return LIFECYCLE; }
	public static Location location() { return LOCATION; }
	public static void setLocation(Location l) { LOCATION = l; }
}
