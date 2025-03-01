package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {
    @Inject(at = @At("HEAD"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", cancellable = true)
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        if (StarNetEssentials.connected()) {
            if (!GeneralConfigModel.HIDE_COSMETIC.value) {
                ItemStack playerChestplate = playerRenderState.chestItem;
                if (playerChestplate != ItemStack.EMPTY) ci.cancel();
            }
        }
    }
}
