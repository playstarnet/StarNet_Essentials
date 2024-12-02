package com.playstarnet.essentials.feat.config.model;

import net.minecraft.client.Minecraft;

public enum GeneralConfigModel {
    HIDE_COSMETIC (
            false,
            "config.se.general.hide_cosmetics"
    ),
    DISCORD_RPC (
            !Minecraft.ON_OSX,
            "config.se.general.discord_rpc"
    ),
    EXP_PERCENT (
            true,
            "config.se.general.xp_percent"
    ),
    INVENTORY_RARITIES (
            true,
            "config.se.general.inventory_rarities"
    ),
    HIDE_PLAYER_NAME_TAGS ( // New option to hide player name tags
            false,
            "config.se.general.hide_player_name_tags"
    ),
    HIDE_NPC_NAME_TAGS ( // New option to hide npc name tags
            false,
            "config.se.general.hide_npc_name_tags"
    ),
    INVENTORY_OPEN_SOUND (
            true,
            "config.se.general.open_inventory_sound"
    );

    public Boolean value;
    public final String name;

    GeneralConfigModel(boolean value, String name) {
        this.name = name;
        this.value = value;
    }
}
