package com.playstarnet.essentials.feat.sound;

import com.playstarnet.essentials.StarNetEssentials;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;

public class SoundManager {

	private static final ResourceLocation INTERFACE_CLICK_ID = ResourceLocation.parse("minecraft:interface_click");
	public static final SoundEvent INTERFACE_CLICK_EVENT = SoundEvent.createVariableRangeEvent(INTERFACE_CLICK_ID);

	private static final Minecraft client = Minecraft.getInstance();
	private static WeighedSoundEvents cachedSoundEvent = null;

	private static WeighedSoundEvents getInterfaceClickEvent() {
		if (cachedSoundEvent == null) {
			cachedSoundEvent = client.getSoundManager().getSoundEvent(INTERFACE_CLICK_ID);
		}
		return cachedSoundEvent;
	}

	public static void initialize() {
		// Register the inventory screen event listener
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			// Only handle InventoryScreen and if connected to the correct server
			if (screen instanceof InventoryScreen && StarNetEssentials.connected()) {
				cacheSoundAndPlay();
			}
		});
	}

	private static void cacheSoundAndPlay() {
		// Cache the sound if not already cached
		if (cachedSoundEvent == null) {
			cachedSoundEvent = getInterfaceClickEvent();
		}

		// Play the sound
		if (cachedSoundEvent != null && client.player != null && client.level != null) {
			client.level.playSound(
					client.player, // Player as the sound source
					client.player.blockPosition(), // Position to play the sound
					INTERFACE_CLICK_EVENT, // The sound event
					SoundSource.MASTER, // The category
					0.006F, // Volume
					1.0F // Pitch
			);
		} else {
			System.err.println("Failed to play interface click sound: Sound not found or player not ready.");
		}
	}
}
