package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.platform.NativeImage;
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
		Minecraft client = Minecraft.getInstance();
		File screenshotsFolder = new File(gameDirectory, "screenshots");
		if (!screenshotsFolder.exists()) {
			screenshotsFolder.mkdirs();
		}

		// Generate a timestamped file name
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
		String screenshotFileName = timestamp + ".png";
		File screenshotFile = new File(screenshotsFolder, screenshotFileName);

		// Save the screenshot
		try {
			NativeImage nativeImage = Screenshot.takeScreenshot(framebuffer);
			nativeImage.writeToFile(screenshotFile); // Use the correct method to save
			nativeImage.close();
		} catch (Exception e) {
			e.printStackTrace();
			// Notify the user of failure
			if (client.player != null) {
				client.player.displayClientMessage(
						Component.literal("Failed to save screenshot.").withStyle(ChatFormatting.RED), false
				);
			}
			ci.cancel();
			return;
		}

		// Create clickable components for the file and folder
		if (client.player != null) {
			Component openFile = Component.literal(screenshotFileName).withStyle(
					Style.EMPTY.withColor(ChatFormatting.WHITE)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotFile.getAbsolutePath()))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open the screenshot")))
			);

			Component openFolder = Component.literal("[Open Folder]").withStyle(
					Style.EMPTY.withColor(ChatFormatting.YELLOW)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotsFolder.getAbsolutePath()))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open the screenshots folder")))
			);

			// Send the message to the player's chat
			client.player.displayClientMessage(
					Component.literal("Saved screenshot (")
							.append(openFile)
							.append(") ")
							.append(openFolder),
					false
			);
		}

		ci.cancel();
	}
}
