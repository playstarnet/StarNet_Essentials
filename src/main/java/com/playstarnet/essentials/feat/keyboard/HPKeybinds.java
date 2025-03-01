package com.playstarnet.essentials.feat.keyboard;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.keyboard.model.KeybindModel;
import com.playstarnet.essentials.mixins.ext.ClientPacketListenerAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HPKeybinds {
    public HPKeybinds() {
        registerKeybinds();
        registerKeybindListeners();
    }

    private void registerKeybinds() {
        for (KeybindModel keybind : KeybindModel.values()) {
            KeyBindingHelper.registerKeyBinding(keybind.keyMapping);
        }
    }

    private void registerKeybindListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!StarNetEssentials.connected()) return; // Ensure keybinds only work when connected

            for (KeybindModel keybind : KeybindModel.values()) {
                if (keybind.keyMapping.isDown()) {
                    handleKeybindAction(client, keybind);
                }
            }
        });
    }

    private void handleKeybindAction(Minecraft client, KeybindModel keybind) {
        switch (keybind) {
            case SOCIALPAD:
                if (client.player != null) {
                    interactWithSlot(client, 8);
                }
                break;
            case PROFILE:
                sendCommand(client, "profile");
                break;
            case MAP:
                sendCommand(client, "map");
                break;
            case ISLAND:
                sendCommand(client, "island");
                break;
            case SPAWN:
                sendCommand(client, "spawn");
                break;
            case MAIL:
                sendCommand(client, "mail");
                break;
            case SETTINGS:
                sendCommand(client, "settings");
                break;
            default:
                break;
        }
    }

    private void sendCommand(Minecraft client, String command) {
        if (client.getConnection() != null) {
            ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand(command);
        }
    }

    private void interactWithSlot(Minecraft client, int targetSlot) {
        Player player = client.player;
        int previousSlot = player.getInventory().selected;
        ItemStack itemInSlot = player.getInventory().getItem(targetSlot);

        if (!itemInSlot.isEmpty()) {
            player.getInventory().selected = targetSlot;
            MultiPlayerGameMode gameMode = client.gameMode;
            if (gameMode != null) {
                gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }
            player.getInventory().selected = previousSlot;
        }
    }
}
