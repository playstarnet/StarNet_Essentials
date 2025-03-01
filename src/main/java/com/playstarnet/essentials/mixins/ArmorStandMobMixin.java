package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMobMixin {

	@Inject(at = @At("TAIL"), method = "tick")
	private void tick(CallbackInfo ci) {
		if (!StarNetEssentials.connected()) return;

		ArmorStand armorStand = (ArmorStand) (Object) this;
		if (!isCosmeticArmorStand(armorStand)) return; // Only process cosmetic armor stands

		Vec3 pos = armorStand.position();
		if (!pos.closerThan(new Vec3(71.5f, 5f, -135.5f), 5)) {
			if (!armorStand.level().isClientSide) {
				armorStand.discard(); // Use discard() instead of kill() for Minecraft 1.20+
			}
		}
	}

	private boolean isCosmeticArmorStand(ArmorStand armorStand) {
		return armorStand.getItemBySlot(EquipmentSlot.HEAD).is(Items.LEATHER_HORSE_ARMOR);
	}
}
