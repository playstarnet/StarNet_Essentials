package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class) // Target LivingEntityRenderer instead of EntityRenderer
public abstract class ArmorStandRendererMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(T entity, float entityYaw, float partialTicks,
                        PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        // Only block rendering for cosmetic ArmorStands
        if (entity instanceof ArmorStand armorStand && isCosmeticArmorStand(armorStand) && isFirstPerson()) {
            ci.cancel(); // Stops rendering the armor stand in first-person
        }
    }

    private boolean isCosmeticArmorStand(ArmorStand armorStand) {
        return armorStand.getItemBySlot(EquipmentSlot.HEAD).is(Items.LEATHER_HORSE_ARMOR);
    }

    private boolean isFirstPerson() {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
