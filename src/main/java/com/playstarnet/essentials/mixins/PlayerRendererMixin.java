package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.rendering.CustomChestLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void constructor(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        if (StarNetEssentials.connected()) {
            this.addLayer(new CustomChestLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        }
    }
}
