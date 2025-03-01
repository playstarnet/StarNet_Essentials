package com.playstarnet.essentials.feat.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CustomChestLayer<T extends AbstractClientPlayer, M extends EntityModel<PlayerRenderState>>
        extends RenderLayer<PlayerRenderState, M> {

    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final ItemRenderer itemInHandRenderer;
    private final BakedModel chestItemModel;

    private static final boolean HIDE_COSMETIC = GeneralConfigModel.HIDE_COSMETIC.value;

    public CustomChestLayer(RenderLayerParent<PlayerRenderState, M> renderer, ItemRenderer itemInHandRenderer, BakedModel chestItemModel) {
        this(renderer, 1.0f, 1.0f, 1.0f, itemInHandRenderer, chestItemModel);
    }

    public CustomChestLayer(RenderLayerParent<PlayerRenderState, M> renderer, float scaleX, float scaleY, float scaleZ,
                            ItemRenderer itemInHandRenderer, BakedModel chestItemModel) {
        super(renderer);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.itemInHandRenderer = itemInHandRenderer;
        this.chestItemModel = chestItemModel;
    }

    public static void translateToBody(PoseStack poseStack, boolean crouching) {
        // Base translation
        poseStack.translate(0.0f, -0.25f, 0.0f);

        if (crouching) {
            // Adjust for crouching posture
            poseStack.mulPose(Axis.XP.rotationDegrees(30.0f));
            poseStack.translate(0.0f, 0.15f, -0.245f);
        }

        // Rotate to align with body
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));

        // Scale and position adjustment
        poseStack.scale(0.625f, -0.625f, -0.625f);
        poseStack.translate(0.0D, 3, 0.0D);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, PlayerRenderState entityRenderState, float limbAngle, float limbDistance) {
        if (GeneralConfigModel.HIDE_COSMETIC.value) return;

        ItemStack chestArmor = entityRenderState.chestItem;
        if (chestArmor.isEmpty()) return;

        matrices.pushPose();

        // Apply scaling
        matrices.scale(this.scaleX, this.scaleY, this.scaleZ);

        // Apply body transformations
        translateToBody(matrices, entityRenderState.isCrouching);

        // Render the item
        this.itemInHandRenderer.render(chestArmor, ItemDisplayContext.HEAD, false, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY, chestItemModel);

        matrices.popPose();
    }
}
