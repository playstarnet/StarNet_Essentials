package com.playstarnet.essentials.feat.sound;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;

public class SoundManager {

	private static final ResourceLocation INTERFACE_CLICK_ID = ResourceLocation.parse("starnet_essentials:interface_click");
	public static final SoundEvent INTERFACE_CLICK_EVENT = SoundEvent.createVariableRangeEvent(INTERFACE_CLICK_ID);

	private static final Minecraft client = Minecraft.getInstance();
	private static WeighedSoundEvents cachedSoundEvent = null;

	// Cooldown mechanism to prevent spamming the sound
	private static long lastSoundPlayTime = 0;
	private static final long SOUND_COOLDOWN_MS = 500; // 500ms cooldown

	public static void initialize() {
		// Delay sound caching until the game is fully initialized
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			if (cachedSoundEvent == null) {
				cachedSoundEvent = getInterfaceClickEvent();
				if (cachedSoundEvent != null) {
					StarNetEssentials.logger().info("Sound successfully cached: " + INTERFACE_CLICK_ID);
				} else {
					StarNetEssentials.logger().warn("Sound event not found: " + INTERFACE_CLICK_ID);
				}
			}
		});

		// Register the inventory screen event listener
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen && StarNetEssentials.connected()) {
				System.out.println("Inventory screen opened.");
				playSoundWithCooldown();
			}
		});
	}

	private static WeighedSoundEvents getInterfaceClickEvent() {
		try {
			if (client.getSoundManager() != null) {
				return client.getSoundManager().getSoundEvent(INTERFACE_CLICK_ID);
			}
		} catch (Exception e) {
			StarNetEssentials.logger().error("Error retrieving sound event: " + INTERFACE_CLICK_ID, e);
		}
		return null;
	}

	public static void playSoundWithCooldown() {
		long currentTime = System.currentTimeMillis();

		// Cooldown check
		if (currentTime - lastSoundPlayTime < SOUND_COOLDOWN_MS) {
			return;
		}

		// Update the last sound play time
		lastSoundPlayTime = currentTime;

		// Play the sound
		if (cachedSoundEvent != null && client.player != null && client.level != null) {
			if(GeneralConfigModel.INVENTORY_OPEN_SOUND.value)
				client.level.playSound(
						client.player,
						client.player.blockPosition(),
						INTERFACE_CLICK_EVENT,
						SoundSource.MASTER,
						0.3F,
						1.0F
				);
		} else {
			System.err.println("Sound not played: Sound not cached or player/level not ready.");
		}
	}
}
