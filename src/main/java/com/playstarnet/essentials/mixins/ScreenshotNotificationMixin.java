package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class ScreenshotNotificationMixin {

	@Inject(method = "grab", at = @At("HEAD"), cancellable = true)
	private static void onScreenshotSaved(File gameDirectory, RenderTarget framebuffer, Consumer<Component> messageReceiver, CallbackInfo ci) {
		// Get the screenshots folder
		File screenshotsFolder = new File(gameDirectory, "screenshots");
		if (!screenshotsFolder.exists()) {
			screenshotsFolder.mkdirs();
		}

		// Generate the screenshot file name based on the current timestamp
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
		String screenshotFileName = timestamp + ".png";

		// Create a custom notification for the player
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			// Create the clickable "Open Folder" message
			Component openFolder = Component.literal("[Open Folder]").withStyle(
					Style.EMPTY.withColor(ChatFormatting.YELLOW)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotsFolder.getAbsolutePath()))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open the screenshots folder")))
			);

			// Create the main notification message
			Component screenshotMessage = Component.literal("Saved screenshot (" + screenshotFileName + ") ")
					.withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
					.append(openFolder);

			// Send the message to the player's chat
			client.player.sendSystemMessage(screenshotMessage);
		}

		// Cancel the original method to prevent recursion
		ci.cancel();
	}
}
