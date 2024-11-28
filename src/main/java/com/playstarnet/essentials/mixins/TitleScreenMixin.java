package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.mixins.ext.ScreenAccessor;
import com.playstarnet.essentials.util.Chars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.gui.screens.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Inject(method = "init", at = @At("TAIL"))
	private void addJoinServerButton(CallbackInfo ci) {
		TitleScreen screen = (TitleScreen) (Object) this;

		// Get all existing buttons
		List<AbstractWidget> existingButtons = new ArrayList<>();
		((ScreenAccessor) screen).getChildren().stream()
				.filter(child -> child instanceof AbstractWidget)
				.map(child -> (AbstractWidget) child)
				.forEach(existingButtons::add);

		// Find the "Minecraft Realms" button position
		int insertIndex = -1;
		for (int i = 0; i < existingButtons.size(); i++) {
			AbstractWidget button = existingButtons.get(i);
			if (button.getMessage().getString().equals("Minecraft Realms")) {
				insertIndex = i + 1; // Insert below "Minecraft Realms"
				break;
			}
		}

		if (insertIndex == -1) {
			return;
		}

		// Create "Join StarNet" button
		Button joinServerButton = Button.builder(Component.literal("Join StarNet"), button -> connectToServer())
				.bounds(screen.width / 2 - 100, existingButtons.get(insertIndex - 1).getY() + 24, 200, 20)
				.build();

		// Adjust buttons below the new one
		for (int i = insertIndex; i < existingButtons.size(); i++) {
			AbstractWidget button = existingButtons.get(i);
			button.setY(button.getY() + 24);
		}

		// Add the new button
		((ScreenAccessor) screen).getChildren().add(joinServerButton);
		screen.renderables.add(joinServerButton);

		// Debugging output to verify layout
		for (GuiEventListener button : ((ScreenAccessor) screen).getChildren()) {
		}
	}

	@Unique
	private static void connectToServer() {
		Minecraft client = Minecraft.getInstance();
		if (client.screen instanceof TitleScreen) {
			ServerData serverData = new ServerData("StarNet Server", "alpha.playstarnet.com", ServerData.Type.OTHER);
			serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED); // Automatically accept resource packs
			ServerAddress serverAddress = ServerAddress.parseString(serverData.ip);
			ConnectScreen.startConnecting(client.screen, client, serverAddress, serverData, false, null);
		}
	}
}
