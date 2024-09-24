package com.playstarnet.essentials.mixins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.ext.BossHealthOverlayAccessor;
import com.playstarnet.essentials.feat.location.Location;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin implements BossHealthOverlayAccessor {
    @Shadow @Final Map<UUID, LerpingBossEvent> events;
    @Unique private String bossBarName;

    @Inject(method = "render", at = @At("HEAD"))
    private void renderBossBarName(GuiGraphics guiGraphics, CallbackInfo ci) {
        Iterator<LerpingBossEvent> var4 = this.events.values().iterator();
        if (var4.hasNext()) {

            LerpingBossEvent lerpingBossEvent = var4.next();

            if (StarNetEssentials.location() == Location.DAZZLES_COSMETICS)
                this.events.values().forEach(e -> e.setName(Component.nullToEmpty("")));

            this.bossBarName = lerpingBossEvent.getName().getString();
        } else {
            this.bossBarName = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "update", cancellable = true)
    private void update(ClientboundBossEventPacket packet, CallbackInfo ci) {
        if (StarNetEssentials.location() == Location.DAZZLES_COSMETICS) {
            ci.cancel();
        }
    }

    @Override
    public String se$getBossBarName() {
        return bossBarName;
    }
}
