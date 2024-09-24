package com.playstarnet.essentials.feat.config.model;

public enum GeneralConfigModel {
    HIDE_COSMETIC (
            false,
            "config.se.general.hide_cosmetics"
    ),
    DISCORD_RPC (
            true,
            "config.se.general.discord_rpc"
    ),
    EXP_PERCENT (
            true,
            "config.se.general.xp_percent"
    ),
    INVENTORY_RARITIES (
            true,
            "config.se.general.inventory_rarities"
    );

    public Boolean value;
    public final String name;

    GeneralConfigModel(boolean value, String name) {
        this.name = name;
        this.value = value;
    }
}