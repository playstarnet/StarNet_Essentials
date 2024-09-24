package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.util.DisplayNameUtil;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin <T extends Entity>{

    @Shadow protected abstract boolean shouldShowName(T entity);

    @Shadow protected abstract void renderNameTag(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float tickDelta);

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (this.shouldShowName(entity) && StarNetEssentials.connected() && entity instanceof Player) {
            String playerName = DisplayNameUtil.ignFromDisplayName(entity.getDisplayName().getString());
            MutableComponent newName = DisplayNameUtil.withBadges((MutableComponent) entity.getDisplayName(), playerName, false);
            this.renderNameTag(entity, newName, poseStack, buffer, packedLight, partialTick);
            ci.cancel();
        }
    }
}
