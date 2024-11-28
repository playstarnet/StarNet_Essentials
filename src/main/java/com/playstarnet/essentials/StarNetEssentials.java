package com.playstarnet.essentials;

import com.playstarnet.essentials.feat.config.StarNetPlusConfig;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.discord.DiscordManager;
import com.playstarnet.essentials.feat.keyboard.HPKeybinds;
import com.playstarnet.essentials.feat.lifecycle.Lifecycle;
import com.playstarnet.essentials.feat.lifecycle.Task;
import com.playstarnet.essentials.feat.location.Location;
import com.playstarnet.essentials.feat.sound.SoundManager;
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
	private static final HPKeybinds KEYBINDS = new HPKeybinds();
	private static final boolean debugMode = true;
	private static boolean updateChecked = false;
	private static boolean soundCached = false;
	private static final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

	@Override
	public void onInitializeClient() {
		Constants.MOD_MENU_PRESENT = FabricLoader.getInstance().isModLoaded("modmenu");

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
				.add(Task.of(Location::check, 200))
				.add(Task.of(this::handleDiscordLifecycle, 100))
				.add(Task.of(this::handleKeybinds, 10));

		// Initialize the SoundManager
		SoundManager.initialize();
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

	private void handleKeybinds() {
		if (connected()) {
			KEYBINDS.tick();
		}
	}

	public static boolean connected() {
		ServerData server = Minecraft.getInstance().getCurrentServer();
		if (server != null) {
			boolean isConnected = server.ip.toLowerCase().endsWith("playstarnet.com") && !server.ip.toLowerCase().contains("event");
			if (isConnected && !updateChecked) {
				logger().info("Performing Modrinth update check.");

				// Offload update check and sound caching to background thread
				backgroundExecutor.submit(() -> {
					ModrinthUpdateChecker.checkForUpdates();
					if (!soundCached) {
						logger().info("Caching sound from server resource pack.");
						SoundManager.playSoundWithCooldown();
						soundCached = true;
					}
				});

				updateChecked = true;
			}
			return isConnected;
		} else {
			updateChecked = false;
			soundCached = false;
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
