package com.playstarnet.essentials.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.gui.screens.social.SocialInteractionsScreen.class)
public class SocialInteractionsScreenMixin {

	@Redirect(
			method = "updateServerLabel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;getOnlinePlayers()Ljava/util/Collection;"
			)
	)
	public Collection<PlayerInfo> filterOnlinePlayers(ClientPacketListener listener) {
		return listener.getOnlinePlayers().stream()
				.filter(info -> !shouldHideName(info.getProfile().getName()))
				.collect(Collectors.toList());
	}

	@Redirect(
			method = "showPage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;getOnlinePlayerIds()Ljava/util/Collection;"
			)
	)
	public Collection<UUID> filterOnlinePlayerIds(ClientPacketListener listener) {
		return listener.getOnlinePlayerIds().stream()
				.filter(uuid -> {
					PlayerInfo info = listener.getPlayerInfo(uuid);
					return info == null || !shouldHideName(info.getProfile().getName());
				})
				.collect(Collectors.toList());
	}

	private boolean shouldHideName(String name) {
		// Check if the name starts with "|slot_" followed by digits
		return name.startsWith("|slot_") && name.substring(6).matches("\\d+");
	}
}
