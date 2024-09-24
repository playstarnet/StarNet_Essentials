package com.playstarnet.essentials.mixins;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.util.DisplayNameUtil;

@Mixin(PlayerTabOverlay.class)
public class DisplayNameMixin {
    @Inject(at = @At("RETURN"), method = "getNameForDisplay", cancellable = true)
    public void getDisplayName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
        Component name = cir.getReturnValue();
        if (StarNetEssentials.connected()) {
            String playerName = DisplayNameUtil.ignFromDisplayName(name.getString());
            MutableComponent newName = DisplayNameUtil.withBadges((MutableComponent) name, playerName, false);
            cir.setReturnValue(newName);
        }
    }
}
