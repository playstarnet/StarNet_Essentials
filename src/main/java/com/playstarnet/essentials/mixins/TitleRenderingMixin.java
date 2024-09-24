package com.playstarnet.essentials.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class TitleRenderingMixin {

    @Shadow
    private Component title;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();

        // Check if HUD is disabled (F1 is pressed)
        if (!client.options.hideGui) {
            // Titles are rendered only if the HUD is visible
            return;
        }
    }
}
