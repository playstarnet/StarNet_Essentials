/*
package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.ext.AbstractContainerScreenAccessor;
import com.playstarnet.essentials.util.ParseItemName;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

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
                TextColor itemColor = ParseItemName.getItemTextColor(item);

				if (itemColor != null) {
                    int color = itemColor.getValue();
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = color & 0xFF;

                    int itemColour = FastColor.ARGB32.color(255, r, g, b); // Top color

                    int r1 = (color >> 16) & 0xFF;
                    int g1 = (color >> 8) & 0xFF;
                    int b1 = color & 0xFF;

                    // Apply the darkening factor to each RGB component
                    r1 = (int) (r1 * 0.5f);
                    g1 = (int) (g1 * 0.5f);
                    b1 = (int) (b1 * 0.5f);

                    int darkerColor = FastColor.ARGB32.color(255, r1, g1, b1);

                    ci.cancel();
                    guiGraphics.fillGradient(x, y, x + 1, y + height - 2, z, itemColour, darkerColor);
                    guiGraphics.fillGradient(x + width - 1, y, x + width - 1 + 1, y + height - 2, z, itemColour, darkerColor);
                    guiGraphics.fill(x, y - 1, x + width, y - 1 + 1, z, itemColour);
                    guiGraphics.fill(x, y - 1 + height - 1, x + width, y - 1 + height - 1 + 1, z, darkerColor);
                }
			}
        }
    }

    // Utility method to blend two colors based on a ratio (from 0.0 to 1.0)
    private static int blendColors(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + ratio * (r2 - r1));
        int g = (int) (g1 + ratio * (g2 - g1));
        int b = (int) (b1 + ratio * (b2 - b1));

        return FastColor.ARGB32.color(255, r, g, b);
    }
}*/
