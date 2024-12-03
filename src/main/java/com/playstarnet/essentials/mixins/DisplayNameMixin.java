package com.playstarnet.essentials.mixins;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.util.DisplayNameUtil;

@Mixin(PlayerTabOverlay.class)
public class DisplayNameMixin {
    @Inject(at = @At("RETURN"), method = "getNameForDisplay", cancellable = true)
    public void getDisplayName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
		Component originalName = cir.getReturnValue();
		String originalNameString = originalName.getString();

		// Skip modification if conditions aren't met
		if (!shouldModifyName(originalNameString)) {
			return;
		}

		try {
			// Transform the name by adding badges
			String playerName = DisplayNameUtil.ignFromDisplayName(originalNameString);
			MutableComponent modifiedName = DisplayNameUtil.withBadges((MutableComponent) originalName, playerName, false);
			cir.setReturnValue(modifiedName);
		} catch (Exception e) {
			handleError(e, originalNameString);
		}
	}

	/**
	 * Determines if the name should be modified.
	 *
	 * @param name The player's original name as a string
	 * @return true if the name should be modified, false otherwise
	 */
	@Unique
	private boolean shouldModifyName(String name) {
		return StarNetEssentials.connected() && !name.startsWith("|slot_");
	}

	/**
	 * Handles errors during display name transformation.
	 *
	 * @param e           The exception that occurred
	 * @param playerName  The player's name that caused the error
	 */
	@Unique
	private void handleError(Exception e, String playerName) {
		// Log the error with meaningful context (adjust to your logging framework)
		System.err.printf("Error modifying display name for player '%s': %s%n", playerName, e.getMessage());
		e.printStackTrace();
	}
}
