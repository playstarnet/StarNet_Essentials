package com.playstarnet.essentials.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public enum Chars {
    USER (
        "\uE001",
        "tooltip.se.user",
        ChatFormatting.WHITE
    ),
    FRIEND (
        "\uE002",
        "tooltip.se.friend",
        ChatFormatting.GOLD
    ),
    DEV (
        "\uE003",
        "tooltip.se.developer",
        ChatFormatting.DARK_PURPLE
    ),
    TRANSLATOR (
        "\uE004",
        "tooltip.se.translator",
        ChatFormatting.RED
    ),
    TEAM (
        "\uE005",
        "tooltip.se.teamMember",
        ChatFormatting.GREEN
    ),
    SETTINGS (
        "\uEF01",
        null,
        ChatFormatting.WHITE
    );

    public final String character;
    public final String tooltip;
    public final ChatFormatting color;

    public void addBadge(MutableComponent newComponent, boolean tooltip) {
        if (tooltip) {
            Component component = ((MutableComponent) this.getComponent()).withStyle(Style.EMPTY.withHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable(this.tooltip).setStyle(Style.EMPTY.withColor(this.color)
                    ))
            ));
            newComponent.append(component);
        } else {
            newComponent.append(this.getComponent());
        }
    }

    public Component getComponent() {
        return Component.literal(this.character).setStyle(Style.EMPTY.withFont(
                ResourceLocation.parse("starnet_essentials:text")
        )).withStyle(ChatFormatting.WHITE);
    }

    Chars(String character, String tooltip, ChatFormatting color) {
        this.character = character;
        this.tooltip = tooltip;
        this.color = color;
    }

}
