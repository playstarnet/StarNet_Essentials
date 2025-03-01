package com.playstarnet.essentials.feat.location;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.discord.PresenceImage;
import com.playstarnet.essentials.feat.ext.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
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
			"Opening the Furniture Box",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	SKULL_CAVES(
			"At the Mining Island",
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
	BREAKFAST_CLUB(
			"At the Breakfast Club",
			"Grabbing a quick bite",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	ALICE_AREA(
			"In Alice's Area",
			"Taking care of capybaras",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	MOON_GHOST(
			"Near the Moon Ghost",
			"Watching the stars in the night and camping",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	CAMPING_GROUND(
			"At the Camping Ground",
			"Roasting marshmallows and telling stories",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	DUTCHMAN_CAVE(
			"In the Dutchman's Cave",
			"Searching for pirate treasure and doing quests",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	SIMONS_STORE(
			"At Simon's Store",
			"Browsing the wares",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	BILLYS_STORE(
			"At Billy's Store",
			"Checking out the bargains",
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
	BEACH_FIGHT(
			"Beach FIGHT!!",
			"Splashing Around",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	AIR_TACKLE(
			"Playing Air Tackle",
			"Trying not to fall off.",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	PIZZA_PARTY(
			"Playing Pizza Party",
			"Too many orders!!!",
			PresenceImage.Large.STAR,
			PresenceImage.Small.ROUNDEL
	),
	PARKOUR(
			"Playing Parkour Race",
			"",
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

	public static void check() {
		if (!StarNetEssentials.connected()) {
			StarNetEssentials.setLocation(UNKNOWN);
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}

		Vec3 playerLocation = client.player.position();

		// Sequential location-based checks with early return on match
		if (checkLocation(playerLocation, new Vec3(87, -30, 43), "FISHING_HOUSE", 25)) return;
		if (checkLocation(playerLocation, new Vec3(185, -23, 218), "DAZZLES_COSMETICS", 10)) return;
		if (checkLocation(playerLocation, new Vec3(262, 5, 199), "MAZIES_FARM", 20)) return;
		if (checkLocation(playerLocation, new Vec3(218, -27, 153), "FURNITURE_CRAFTER", 10)) return;
		if (checkLocation(playerLocation, new Vec3(424, -13, 275), "SKULL_CAVES", 70)) return;
		if (checkLocation(playerLocation, new Vec3(245, -8, 215), "LUMBERJACK", 15)) return;
		if (checkLocation(playerLocation, new Vec3(304, -18, 105), "BREAKFAST_CLUB", 15)) return;
		if (checkLocation(playerLocation, new Vec3(115, 2, 95), "ALICE_AREA", 5)) return;
		if (checkLocation(playerLocation, new Vec3(140, 0, 80), "MOON_GHOST", 10)) return;
		if (checkLocation(playerLocation, new Vec3(82, 4, 200), "CAMPING_GROUND", 15)) return;
		if (checkLocation(playerLocation, new Vec3(309, -29, 133), "DUTCHMAN_CAVE", 10)) return;
		if (checkLocation(playerLocation, new Vec3(245, -24, 264), "SIMONS_STORE", 5)) return;
		if (checkLocation(playerLocation, new Vec3(163, -24, 264), "BILLYS_STORE", 5)) return;

		// Check for island name in the boss bar only if in the world "genworld"
		if (client.level != null && "genworld".equals(client.level.dimension().location().getPath())) {
			if (checkBossBarForIsland(client)) return;
		}

		// Set to GENERIC if no other match found
		StarNetEssentials.setLocation(GENERIC);
	}

	// Method to check distance-based location and set it if within range
	private static boolean checkLocation(Vec3 playerLocation, Vec3 targetLocation, String locationName, double maxDistance) {
		double distance = playerLocation.distanceTo(targetLocation);

		if (distance <= maxDistance) {
			Location newLocation = Location.valueOf(locationName);
			if (!StarNetEssentials.location().equals(newLocation)) {
				StarNetEssentials.setLocation(newLocation);
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
			String strippedBossBarName = bossBarName.replaceAll("(&[0-9a-fk-or])", "").trim();

			// Extract island name based on specific markers
			String islandName = null;
			int startIdx = strippedBossBarName.indexOf("ꐗ");
			int endIdx = strippedBossBarName.indexOf("|");

			if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
				islandName = strippedBossBarName.substring(startIdx + 1, endIdx).replace("꒩", " ").trim();
			}

			if (islandName != null && !islandName.isEmpty()) {
				// Match found
				if (islandName.equals(client.player.getGameProfile().getName() + "'s Island")) {
					StarNetEssentials.setLocation(ISLAND_SELF);
				} else {
					Location.ISLAND_OTHER.name = "On " + islandName;
					StarNetEssentials.setLocation(ISLAND_OTHER);
				}
				return true; // Match found
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
