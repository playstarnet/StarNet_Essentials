package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.rendering.CustomChestLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    private BakedModel chestItemModel;

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void constructor(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        if (StarNetEssentials.connected()) {
            this.addLayer(new CustomChestLayer<>(this, context.getItemRenderer(), chestItemModel));
        }
    }

    @Override
    public void extractRenderState(AbstractClientPlayer entity, PlayerRenderState livingEntityRenderState, float f) {
        super.extractRenderState(entity, livingEntityRenderState, f);

        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        chestItemModel = this.itemRenderer.resolveItemModel(itemStack, entity, ItemDisplayContext.HEAD);
    }
}
