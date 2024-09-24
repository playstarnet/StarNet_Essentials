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
    FISHING_HUT(
        "Gill's Fishing Hut",
        "Fishin' all day",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),
    DAZZLES_COSMETICS(
        "At Dazzle's Cosmetics",
        "Chilling with Dazzle",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),

    // Activities
    ISLAND_SELF(
        "On their own island",
        "Look at that view! 🏝️",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),
    ISLAND_OTHER(
        "On <player>'s room",
        "Look at that view! 🏝️",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),

    WARDROBE(
        "In the Wardrobe",
        "Don't look! 👚",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),

    // Minigames
    TRICK_OR_TREAT(
            "Trick or Treat",
            "Look at that view! 🏝️",
            PresenceImage.Large.SCENE,
            PresenceImage.Small.ROUNDEL
    ),

    // Miscellaneous
    GENERIC(
        "On Hideaway Island",
        "Relaxing in the sun ☀️",
        PresenceImage.Large.SCENE,
        PresenceImage.Small.ROUNDEL
    ),
    SECRET(
        "You saw nothing...",
        "This is all just a dream... 😵‍💫",
        PresenceImage.Large.SCENE_DARK,
        PresenceImage.Small.ROUNDEL
    ),
    UNKNOWN(
        "Using Hideaway+",
        "Somewhere in the metaverse... 🚀",
        PresenceImage.Large.SCENE_DARK,
        PresenceImage.Small.ROUNDEL
    ),
    ;

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
        if (location.closerThan(new Vec3(1822f, -9f, -4909f), 10)) StarNetEssentials.setLocation(FISHING_HUT);
        if (location.closerThan(new Vec3(1789f, -7f, -5012f), 10)) StarNetEssentials.setLocation(DAZZLES_COSMETICS);
        else if (server != null) {
            // Scoreboard-based
            ServerScoreboard board = server.getScoreboard();
            Collection<String> boardNames = getBoardNames(board);
            if (client.player.isSpectator()) StarNetEssentials.setLocation(WARDROBE);
            else if (server.getCustomBossEvents().getEvents().stream().findFirst().isPresent()) {
            }
            // Bossbar-based
            else if ((bossBarName = ((BossHealthOverlayAccessor)client.gui.getBossOverlay()).se$getBossBarName()) != null) {
                if (bossBarName.contains("\uE612 | Editor Mode is")) StarNetEssentials.setLocation(ISLAND_SELF);
                else if (bossBarName.contains("\uE293 ") && bossBarName.contains("'s Room")) {
                    String visitingPlayerName = ((BossHealthOverlayAccessor)client.gui.getBossOverlay()).se$getBossBarName().split(" ")[0];
                    visitingPlayerName = visitingPlayerName.replace("'s", "");

                    Location.ISLAND_OTHER.name = "In " + visitingPlayerName + "'s room";
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
