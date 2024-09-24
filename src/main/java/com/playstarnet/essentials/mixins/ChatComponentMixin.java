package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.util.DisplayNameUtil;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @ModifyVariable(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/network/chat/Component;)V", argsOnly = true)
    private Component addMessageIcons(Component message) {
        String playerName = DisplayNameUtil.nameFromChatMessage(message.getString());
		try {
			return DisplayNameUtil.withBadges((MutableComponent) message, playerName, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
