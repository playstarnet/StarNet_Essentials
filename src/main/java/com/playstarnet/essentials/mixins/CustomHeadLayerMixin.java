package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel<PlayerRenderState>>
        extends RenderLayer<PlayerRenderState, M> {

    public CustomHeadLayerMixin(RenderLayerParent<PlayerRenderState, M> renderer) {
        super(renderer);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, EntityRenderState entityRenderState, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (StarNetEssentials.connected() && GeneralConfigModel.HIDE_COSMETIC.value) {
            ci.cancel();
        }
    }
}
