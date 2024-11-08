package com.playstarnet.essentials.feat.keyboard;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.keyboard.model.KeybindCategoryModel;
import com.playstarnet.essentials.feat.keyboard.model.KeybindModel;
import com.playstarnet.essentials.mixins.ext.ClientPacketListenerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

        while (KeybindModel.POUCH.keyMapping.consumeClick()) {
            if (client.player != null) {
                // Access the item in the 8th hotbar slot (index 7, zero-based)
                interactWithSlot(client, 7);
            }
        }

        handleCommandKeybinds(client);
    }

    private void handleCommandKeybinds(Minecraft client) {
        while (KeybindModel.PROFILE.keyMapping.consumeClick()) {
            sendCommand(client, "profile");
        }
        while (KeybindModel.MAP.keyMapping.consumeClick()) {
            sendCommand(client, "map");
        }
        while (KeybindModel.ISLAND.keyMapping.consumeClick()) {
            sendCommand(client, "island");
        }
        while (KeybindModel.SPAWN.keyMapping.consumeClick()) {
            sendCommand(client, "spawn");
        }
        while (KeybindModel.MAIL.keyMapping.consumeClick()) {
            sendCommand(client, "mail");
        }
        while (KeybindModel.SETTINGS.keyMapping.consumeClick()) {
            sendCommand(client, "settings");
        }
    }

    private void sendCommand(Minecraft client, String command) {
        if (client.getConnection() != null) {
            ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand(command);
        }
    }

    private void interactWithSlot(Minecraft client, int targetSlot) {
        Player player = client.player;

        // Save the current selected slot
        int previousSlot = player.getInventory().selected;

        // Access the item in the target slot
        ItemStack itemInSlot = player.getInventory().getItem(targetSlot);

        // Check if the slot is not empty
        if (!itemInSlot.isEmpty()) {
            // Temporarily set the selected slot to the target slot
            player.getInventory().selected = targetSlot;

            // Perform the right-click action
            MultiPlayerGameMode gameMode = client.gameMode;
            if (gameMode != null) {
                gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }

            // Restore the previous slot after interacting
            player.getInventory().selected = previousSlot;
        }
    }
}
