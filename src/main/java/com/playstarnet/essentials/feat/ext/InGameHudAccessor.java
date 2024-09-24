package com.playstarnet.essentials.feat.ext;

import net.minecraft.network.chat.Component;

public interface InGameHudAccessor {

    Component se$getOverlayMessage();
    Component se$getTitleMessage();
    Component se$getSubtitleMessage();

    float se$getExperiencePoints();
    int se$getExperienceLevel();
}
