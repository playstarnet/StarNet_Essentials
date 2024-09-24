package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.StarNetEssentials;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class TickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci){
        StarNetEssentials.lifecycle().tick();
    }
}
