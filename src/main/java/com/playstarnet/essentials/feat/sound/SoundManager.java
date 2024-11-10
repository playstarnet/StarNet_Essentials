package com.playstarnet.essentials.feat.sound;

import com.playstarnet.essentials.StarNetEssentials;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class SoundManager {

	private static final ResourceLocation INTERFACE_CLICK_ID = ResourceLocation.parse("minecraft:interface_click");
	public static SoundEvent INTERFACE_CLICK_EVENT = SoundEvent.createVariableRangeEvent(INTERFACE_CLICK_ID);

	private static final Minecraft client = Minecraft.getInstance();

	public static void initialize() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			// Only play sound if the screen is InventoryScreen and connected to the correct server
			if (screen instanceof InventoryScreen && StarNetEssentials.connected()) {
				playInventoryOpenSound();
			}
		});
	}

	private static void playInventoryOpenSound() {
		if (client.player != null) {
			if (client.getSoundManager().getSoundEvent(INTERFACE_CLICK_ID) != null) {
				client.player.playSound(INTERFACE_CLICK_EVENT, 0.7F, 1.0F); // Volume 1.0F, Pitch 1.0F
			} else {

			}
		}
	}
}
