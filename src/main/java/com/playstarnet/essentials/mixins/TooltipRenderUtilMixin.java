package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.ext.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.playstarnet.essentials.util.ParseItemName.getItemId;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    @Inject(method = "renderFrameGradient", at = @At(value = "HEAD"), cancellable = true)
    private static void renderFrameRarity(GuiGraphics guiGraphics, int x, int y, int width, int height, int z, int topColor, int bottomColor, CallbackInfo ci) {
        Screen screen = StarNetEssentials.client().screen;

        if (screen instanceof AbstractContainerScreen && GeneralConfigModel.INVENTORY_RARITIES.value) {
            AbstractContainerScreenAccessor containerScreen = (AbstractContainerScreenAccessor) screen;
            Slot slot = containerScreen.se$getHoveredSlot();

            if (containerScreen.se$getMenu().getCarried().isEmpty() && slot != null && slot.hasItem()) {
                ItemStack item = slot.getItem();
                TextColor itemColor = item.getHoverName().getStyle().getColor();
                String itemId = getItemId(item);

                if (itemColor != null && !itemId.isEmpty()) {
                    int color = itemColor.getValue();
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = color & 0xFF;

                    int itemColour = FastColor.ARGB32.color(150, r, g, b);

                    guiGraphics.fill(x, y - 1, x + width, y, z, itemColour); // Top line
                    guiGraphics.fill(x, y, x + 1, y + height - 2, z, itemColour); // Left line
                    guiGraphics.fill(x, y + height - 2, x + width, y + height - 1, z, itemColour); // Bottom line
                    guiGraphics.fill(x + width - 1, y, x + width, y + height - 2, z, itemColour); // Right line
                    ci.cancel();
                }
            }
        }
    }
}