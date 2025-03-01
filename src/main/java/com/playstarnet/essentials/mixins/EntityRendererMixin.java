package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.util.DisplayNameUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin <T extends Entity> {

	@Shadow protected abstract void renderNameTag(EntityRenderState entityRenderState, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light);

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(EntityRenderState entityRenderState, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (entityRenderState.nameTag != null && StarNetEssentials.connected() && entityRenderState instanceof PlayerRenderState) {
            String playerName = DisplayNameUtil.ignFromDisplayName(entityRenderState.nameTag.getString());
			try {
                MutableComponent newName = DisplayNameUtil.withBadges((MutableComponent) entityRenderState.nameTag, playerName, false);
                this.renderNameTag(entityRenderState, newName, matrices, vertexConsumers, light);
                ci.cancel();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
    }
}
