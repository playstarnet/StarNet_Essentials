package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.ui.EmojiPickerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@Inject(method = "render", at = @At("TAIL"))
	private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (StarNetEssentials.connected()) EmojiPickerScreen.renderPicker(graphics, mouseX, mouseY);
	}

	@Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
	private void onMouseScrolled(double mouseX, double mouseY, double amountX, double amountY, CallbackInfoReturnable<Boolean> cir) {
		if (EmojiPickerScreen.isPickerVisible() && StarNetEssentials.connected()) {
			boolean handled = EmojiPickerScreen.handleMouseScroll(amountY); // Pass amountY for scrolling
			if (handled) {
				cir.setReturnValue(true); // Block further processing
				cir.cancel();
			}
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
//		System.out.println("ChatScreen mouseClicked: mouseX=" + mouseX + ", mouseY=" + mouseY);

		if (EmojiPickerScreen.handleMouseClick(mouseX, mouseY) && StarNetEssentials.connected()) {
			System.out.println("EmojiPicker handled mouseClicked");
			cir.setReturnValue(true); // Block further processing
		}
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
//		System.out.println("ChatScreen keyPressed: keyCode=" + keyCode);

		if (EmojiPickerScreen.isPickerVisible() && StarNetEssentials.connected()) {
			System.out.println("Blocking keyboard input while EmojiPicker is visible");
			cir.setReturnValue(true); // Block further processing
		}
	}
}
