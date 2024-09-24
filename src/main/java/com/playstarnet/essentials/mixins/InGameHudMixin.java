package com.playstarnet.essentials.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.feat.ext.InGameHudAccessor;
import com.playstarnet.essentials.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin implements InGameHudAccessor {

    @Shadow public abstract Font getFont();

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Nullable private Component overlayMessageString;

    @Shadow @Nullable private Component title;

    @Shadow @Nullable private Component subtitle;

    @Shadow @Final private DebugScreenOverlay debugOverlay;

    @Inject(at = @At("HEAD"), method = "render")
    public void onRender(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
        if (!this.debugOverlay.showDebugScreen() && StarNetEssentials.connected() && minecraft.player.getInventory().getFreeSlot() == -1) {
            int color = FastColor.ARGB32.color(100, 0, 0, 0);
            int padding = 3;
            int yLevel = 30;
            Font font = minecraft.font;
            Component text = Component.empty()
                    .append(Component.literal("\uE015").setStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "text"))))
                    .append(Component.literal(" Full inventory").withStyle(ChatFormatting.RED));
            graphics.fill(0, yLevel - padding, 10 + padding + font.width(text), yLevel + padding - 1 + font.lineHeight, color);
            graphics.drawString(font, text, 10,yLevel, 0xffffff);
        }
    }

    @Inject(method = "renderExperienceLevel",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 1, shift = At.Shift.BEFORE),
            slice = @Slice(
                    from = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 1))
    )
    public void experienceBarPercent(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci, @Local String string, @Local(ordinal = 1) int x, @Local(ordinal = 2) int y) {
        if (StarNetEssentials.connected() && GeneralConfigModel.EXP_PERCENT.value) {
            string = (Math.round(this.minecraft.player.experienceProgress * 10000) / 100.0) + "%";
            x = (graphics.guiWidth() - this.getFont().width(string)) / 2;

            y = y - 14;

            graphics.drawString(this.getFont(), string, x + 2, y, 0, false);
            graphics.drawString(this.getFont(), string, x, y, 0, false);
            graphics.drawString(this.getFont(), string, x + 1, y + 1, 0, false);
            graphics.drawString(this.getFont(), string, x + 1, y - 1, 0, false);
            graphics.drawString(this.getFont(), string, x + 1, y, 8453920, false);
        }
    }

    @Override
    public Component se$getOverlayMessage() {
        return this.overlayMessageString;
    }

    @Override
    public Component se$getTitleMessage() {
        return this.title;
    }

    @Override
    public Component se$getSubtitleMessage() {
        return this.subtitle;
    }

    @Override
    public float se$getExperiencePoints() {
        return this.minecraft.player.experienceProgress;
    }

    @Override
    public int se$getExperienceLevel() {
        return this.minecraft.player.experienceLevel;
    }
}
