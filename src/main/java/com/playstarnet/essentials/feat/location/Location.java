package com.playstarnet.essentials.feat.location;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.discord.PresenceImage;
import com.playstarnet.essentials.feat.ext.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public enum Location {
	// Locations
	FISHING_HOUSE(
			"At Gill's Fishing House",
			"Fishin' all day",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	DAZZLES_COSMETICS(
			"At Dazzle's Cosmetics",
			"Spinning the Spectacle Wardrobe Wheel",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	MAZIES_FARM(
			"At Mazies Farm",
			"Farming with Mazie",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	FURNITURE_CRAFTER(
			"At The Furniture Crafter",
			"Opening the furniture box",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	SKULL_CAVES(
			"At the Skull Caves",
			"Chilling with Bones",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	LUMBERJACK(
			"At the Lumberjack",
			"Chopping wood all day",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),

	// Activities
	ISLAND_SELF(
			"On their own island",
			"Look at that view! 🏝️",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	ISLAND_OTHER(
			"On <player>'s room",
			"Look at that view! 🏝️",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),

	WARDROBE(
			"In the Wardrobe",
			"Don't look! 👚",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),

	// Minigames
	TRICK_OR_TREAT(
			"Trick or Treat",
			"Look at that view! 🏝️",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),

	// Miscellaneous
	GENERIC(
			"On StarNet Island",
			"Soaking in the sun rays... ☀️",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	SECRET(
			"You saw nothing...",
			"This is all just a dream... 😵‍💫",
			PresenceImage.Large.SCENE_DARK,
			PresenceImage.Small.ROUNDEL
	),
	UNKNOWN(
			"Using StarNet Essentials",
			"Somewhere in the metaverse... 🚀",
			PresenceImage.Large.SCENE_DARK,
			PresenceImage.Small.ROUNDEL
	);

	public String name;
	public final String description;
	public final PresenceImage.Large largeIcon;
	public final PresenceImage.Small smallIcon;

	Location(
			String name,
			String description,
			PresenceImage.Large largeIcon,
			PresenceImage.Small smallIcon
	) {
		this.name = name;
		this.description = description;
		this.largeIcon = largeIcon;
		this.smallIcon = smallIcon;
	}

	// Lord have mercy on my soul for the amount of intense,
	// messy and complicated hardcoding you are about to be
	// subjected to. Grab a paper bag if you get sick easily.
	public static void check() {
		if (!StarNetEssentials.connected()) {
			StarNetEssentials.setLocation(UNKNOWN);
			StarNetEssentials.logger().info("Player not connected, setting location to UNKNOWN.");
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			StarNetEssentials.logger().info("Player is null.");
			return;
		}

		Vec3 playerLocation = client.player.position();
		StarNetEssentials.logger().info("Player position: " + playerLocation);

		// Sequential location-based checks with early return on match
		if (checkLocation(playerLocation, new Vec3(87, -30, 43), "FISHING_HOUSE", 25)) return;
		if (checkLocation(playerLocation, new Vec3(197, -28, 147), "DAZZLES_COSMETICS", 5)) return;
		if (checkLocation(playerLocation, new Vec3(262, 5, 199), "MAZIES_FARM", 20)) return;
		if (checkLocation(playerLocation, new Vec3(218, -27, 153), "FURNITURE_CRAFTER", 10)) return;
		if (checkLocation(playerLocation, new Vec3(-4247, 71, -1390), "SKULL_CAVES", 60)) return;
		if (checkLocation(playerLocation, new Vec3(241, 8, 213), "LUMBERJACK", 15)) return;

		// Check for island name in the boss bar only if in the world "genworld"
		if (client.level != null && "genworld".equals(client.level.dimension().location().getPath())) {
			if (checkBossBarForIsland(client)) return;
		}

		// Set to GENERIC if no other match found
		StarNetEssentials.setLocation(GENERIC);
		StarNetEssentials.logger().info("Setting location to GENERIC (no specific match found).");
	}

	// Method to check distance-based location and set it if within range
	private static boolean checkLocation(Vec3 playerLocation, Vec3 targetLocation, String locationName, double maxDistance) {
		double distance = playerLocation.distanceTo(targetLocation);
		StarNetEssentials.logger().info("Distance to " + locationName + ": " + distance);

		if (distance <= maxDistance) {
			Location newLocation = Location.valueOf(locationName);
			if (!StarNetEssentials.location().equals(newLocation)) {
				StarNetEssentials.setLocation(newLocation);
				StarNetEssentials.logger().info("Setting location to " + locationName);
				StarNetEssentials.DISCORD_MANAGER.updateDiscordPresence(); // Trigger Discord update
			}
			return true;
		}
		return false;
	}

	// Method to check boss bar for island name and set location if matched
	private static boolean checkBossBarForIsland(Minecraft client) {
		String bossBarName = ((BossHealthOverlayAccessor) client.gui.getBossOverlay()).se$getBossBarName();
		if (bossBarName != null) {
			// Remove color codes and trim the result
			String strippedBossBarName = bossBarName.replaceAll("(&[0-9a-fk-or])", "").trim();
			StarNetEssentials.logger().info("Stripped boss bar name: " + strippedBossBarName);

			// Extract island name based on specific markers
			String islandName = null;
			int startIdx = strippedBossBarName.indexOf("ꐗ");
			int endIdx = strippedBossBarName.indexOf("|");

			if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
				islandName = strippedBossBarName.substring(startIdx + 1, endIdx).replace("꒩", " ").trim();
			}

			if (islandName != null && !islandName.isEmpty()) {
				if (islandName.equals(client.player.getGameProfile().getName() + "'s Island")) {
					StarNetEssentials.setLocation(ISLAND_SELF);
					StarNetEssentials.logger().info("Setting location to ISLAND_SELF (own island)");
					return true; // Match found
				} else {
					Location.ISLAND_OTHER.name = "On " + islandName;
					StarNetEssentials.setLocation(ISLAND_OTHER);
					StarNetEssentials.logger().info("Setting location to ISLAND_OTHER (visiting " + islandName + ")");
					return true; // Match found
				}
			}
		}
		return false; // No match found
	}

	private static Collection<String> getBoardNames(ServerScoreboard board) {
		Collection<String> names = board.getTeamNames();
		names.add(board.getObjectives().stream().toList().get(0).getDisplayName().getString());
		return names;
	}
}