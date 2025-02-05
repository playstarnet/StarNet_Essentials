package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorStandRenderer.class)
public abstract class HidePlayerNameTagMixin {

	@Redirect(
			method = "shouldShowName(Lnet/minecraft/world/entity/decoration/ArmorStand;D)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;isCustomNameVisible()Z")
	)
	private boolean hideNameTags(ArmorStand armorStand) {
		// Check if F1 (hideGui) is active
		if (Minecraft.getInstance().options.hideGui) {
			// Check if name tags should be hidden based on config and conditions
			if (shouldHideNameTag(armorStand)) {
				return false; // Prevent name tag rendering
			}
		}

		// Otherwise, allow the default behavior
		return armorStand.isCustomNameVisible();
	}

	@Unique
	private boolean shouldHideNameTag(ArmorStand armorStand) {
		// Hide player-related name tags
		return GeneralConfigModel.HIDE_PLAYER_NAME_TAGS.value && isPlayerNameTag(armorStand);// Default to not hiding
	}

	@Unique
	private boolean isPlayerNameTag(ArmorStand armorStand) {
		// Ensure it has a custom name
		if (armorStand.getCustomName() != null) {
			String customName = armorStand.getCustomName().getString();

			// List of player-related tags
			String[] playerTags = {"ꐠ", "ꐡ", "ꐣ", "ꐤ", "ꐥ", "ꐧ", "ꐨ", "꒏"};

			// Check if the custom name contains any of the player tags
			for (String tag : playerTags) {
				if (customName.contains(tag)) {
					return true; // Player name tag
				}
			}
		}

		return false; // Not a player name tag
	}

}
