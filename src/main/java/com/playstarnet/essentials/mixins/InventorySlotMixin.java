package com.playstarnet.essentials.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for preventing items from being moved inside custom inventories.
 * This is done to improve using menus, as there won't be any flickering when clicking on buttons anymore.
 */
@Mixin(Slot.class)
public abstract class InventorySlotMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "mayPickup", at = @At(value = "HEAD"), cancellable = true)
    public void mayPickup(final Player player, final CallbackInfoReturnable<Boolean> cir) {
        final ItemStack itemStack = getItem();
        if (itemStack.isEmpty()) return;
        if (player.isCreative()) return;

        if (!itemStack.has(DataComponents.CUSTOM_MODEL_DATA) && itemStack.getItem() != Items.PAPER) return;

        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);

            if (customData != null && customData.copyTag() != null && customData.copyTag().contains("custom_locked") &&
                    customData.copyTag().getBoolean("custom_locked")) {
                cir.setReturnValue(false); // Locked, so block the item pickup.
            }
        }

        cir.setReturnValue(false);
    }
}