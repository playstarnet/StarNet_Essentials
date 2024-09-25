package com.playstarnet.essentials.feat.keyboard;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.keyboard.model.KeybindCategoryModel;
import com.playstarnet.essentials.feat.keyboard.model.KeybindModel;
import com.playstarnet.essentials.mixins.ext.ClientPacketListenerAccessor;
import net.minecraft.client.Minecraft;

public class HPKeybinds {
    public HPKeybinds() {
        for (KeybindCategoryModel category : KeybindCategoryModel.values()) {
            KeyBindingRegistryImpl.addCategory(category.translationString);
        }

        for (KeybindModel keybind : KeybindModel.values()) {
            KeyBindingRegistryImpl.registerKeyBinding(keybind.keyMapping);
        }
    }

    public void tick() {
        Minecraft client = StarNetEssentials.client();

        while (KeybindModel.WARDROBE.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("wardrobe");
            }
        }
        while (KeybindModel.PROFILE.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("profile");
            }
        }
        while (KeybindModel.MAP.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("map");
            }
        }
        while (KeybindModel.ISLAND.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("island");
            }
        }
        while (KeybindModel.SPAWN.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("spawn");
            }
        }
        while (KeybindModel.MAIL.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("mail");
            }
        }
        while (KeybindModel.SETTINGS.keyMapping.consumeClick()) {
            if (client.getConnection() != null) {
                ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("settings");
            }
        }
    }
}
