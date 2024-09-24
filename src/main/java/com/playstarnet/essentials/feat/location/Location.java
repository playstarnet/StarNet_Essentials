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
			return;
		}

		Minecraft client = Minecraft.getInstance();
		MinecraftServer server = client.player.getServer();
		Vec3 location = client.player.position();
		String bossBarName;

		// Location-based
		if (location.closerThan(new Vec3(1822f, -9f, -4909f), 10)) StarNetEssentials.setLocation(FISHING_HOUSE);
		if (location.closerThan(new Vec3(1789f, -7f, -5012f), 10)) StarNetEssentials.setLocation(DAZZLES_COSMETICS);
		if (location.closerThan(new Vec3(1962f, 12f, -5096f), 15)) StarNetEssentials.setLocation(MAZIES_FARM);
		if (location.closerThan(new Vec3(1951f, -7f, -5196f), 10)) StarNetEssentials.setLocation(FURNITURE_CRAFTER);
		if (location.closerThan(new Vec3(-4247, 71f, -1390f), 60)) StarNetEssentials.setLocation(SKULL_CAVES);
		if (location.closerThan(new Vec3(1909f, 17f, -5081f), 15)) StarNetEssentials.setLocation(LUMBERJACK);
		else if (server != null) {
			// Scoreboard-based
			ServerScoreboard board = server.getScoreboard();
			Collection<String> boardNames = getBoardNames(board);
			if (client.player.isSpectator()) StarNetEssentials.setLocation(WARDROBE);
			else if ((bossBarName = ((BossHealthOverlayAccessor)client.gui.getBossOverlay()).se$getBossBarName()) != null) {
				System.out.println(bossBarName);
				if (bossBarName.contains(client.player.getGameProfile().getName() + "'s Island'")) StarNetEssentials.setLocation(ISLAND_SELF);
				else if (bossBarName.contains("'s Island")) {
					String visitingPlayerName = ((BossHealthOverlayAccessor)client.gui.getBossOverlay()).se$getBossBarName().split(" ")[0];
					visitingPlayerName = visitingPlayerName.replace("'s", "");

					Location.ISLAND_OTHER.name = "On " + visitingPlayerName + "'s Island";
					StarNetEssentials.setLocation(ISLAND_OTHER);
				}
				else if (bossBarName.contains("\uE293 ")) {
					StarNetEssentials.setLocation(ISLAND_OTHER);
				}
			}
			else StarNetEssentials.setLocation(GENERIC);
		}
		else StarNetEssentials.setLocation(GENERIC);
	}

	private static Collection<String> getBoardNames(ServerScoreboard board) {
		Collection<String> names = board.getTeamNames();
		names.add(board.getObjectives().stream().toList().get(0).getDisplayName().getString());
		return names;
	}
}
