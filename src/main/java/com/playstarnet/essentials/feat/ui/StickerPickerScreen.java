package com.playstarnet.essentials.feat.ui;

import com.google.gson.Gson;
import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.mixins.ext.ChatScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StickerPickerScreen {
	private static final ResourceLocation EMOJI_PICKER_TEXTURE = ResourceLocation.parse("starnet_essentials:textures/gui/emojipicker.png");
	private static final ResourceLocation EMOJI_BUTTON_TEXTURE = ResourceLocation.parse("starnet_essentials:textures/gui/button.png");
	private static boolean pickerVisible = false;

	// Caching the connection status
	private static boolean isConnected = false;
	private static long lastConnectionCheck = 0L;
	private static final long CONNECTION_CHECK_INTERVAL_MS = 1000L; // Check every 1 second

	// Relative positions (percentage of screen size)
	private static final int BASE_BUTTON_X = 24; // Base X position for button
	private static final int BASE_BUTTON_Y = 5; // Base Y position for button
	private static final int BASE_PICKER_X = 5; // Base X position for picker
	private static final int BASE_PICKER_Y = 26; // Base Y position for picker
	private static final int BASE_GUI_SCALE = 3; // The GUI scale used to define these base values

	private static int pickerX;
	private static int pickerY;
	private static int buttonX;
	private static int buttonY;

	private static final int PICKER_WIDTH = 120;
	private static final int PICKER_HEIGHT = 75;
	private static final int BUTTON_WIDTH = 16;
	private static final int BUTTON_HEIGHT = 16;

	private static final int EMOJI_SIZE = 10;
	private static final int SPACING = 2;
	private static final int PADDING = 8;
	private static int scrollOffset = 0;

	private static List<Sticker> stickers = new ArrayList<>();

	// Load emojis
	static {
		loadStickers();
		updateScaledPositions();
	}

	private static void loadStickers() {
		try (InputStreamReader reader = new InputStreamReader(
				Minecraft.getInstance().getResourceManager().open(ResourceLocation.parse("starnet_essentials:stickers/stickers.json")),
				StandardCharsets.UTF_8)) {
			stickers = new Gson().fromJson(reader, new com.google.gson.reflect.TypeToken<List<Sticker>>() {}.getType());
		} catch (Exception e) {
			System.err.println("Failed to load stickers: " + e.getMessage());
		}
	}

	// Update positions based on screen dimensions and GUI scale
	private static void updateScaledPositions() {
		int currentGuiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();

		// Scale positions relative to the base GUI scale
		pickerX = (BASE_PICKER_X * currentGuiScale) / BASE_GUI_SCALE;
		pickerY = (BASE_PICKER_Y * currentGuiScale) / BASE_GUI_SCALE;
		buttonX = (BASE_BUTTON_X * currentGuiScale) / BASE_GUI_SCALE;
		buttonY = (BASE_BUTTON_Y * currentGuiScale) / BASE_GUI_SCALE;
	}

	// Check and cache the connection status
	private static boolean checkConnection() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastConnectionCheck > CONNECTION_CHECK_INTERVAL_MS) {
			isConnected = StarNetEssentials.connected();
			lastConnectionCheck = currentTime;
		}
		return isConnected;
	}

	// Toggle picker visibility
	public static void togglePicker() {
		if (!checkConnection()) {
			return; // Do nothing if not connected to the server
		}
		pickerVisible = !pickerVisible;
		scrollOffset = 0;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	// Render the EmojiPicker panel
	public static void renderPicker(GuiGraphics graphics, int mouseX, int mouseY) {
		if (!checkConnection()) {
			return; // Do not render anything if not connected to the server
		}
		// Ensure positions are updated dynamically
		updateScaledPositions();

		if (pickerVisible) {
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 500); // Push picker and emojis to a higher layer (above chat)

			// Render the Emoji Picker panel
			graphics.blit(RenderType::guiTextured, EMOJI_PICKER_TEXTURE, pickerX, pickerY, 0.0F, 0.0F, PICKER_WIDTH, PICKER_HEIGHT, 122, 75, 122, 75);

			// Render the emojis
			float scaleFactor = 0.7f; // Emoji size scaling
			int scaledEmojiSize = (int) (EMOJI_SIZE * scaleFactor);
			int hoverPadding = 1; // Additional pixels for hover box
			int startX = pickerX + PADDING; // Padding for left margin
			int startY = pickerY + PADDING; // Padding for top margin
			int maxColumns = 5; // 9 emojis per row
			int col = 0;

			for (int i = 0; i < stickers.size(); i++) {
				Sticker sticker = stickers.get(i);
				String displayChar = sticker.getDisplayChar();
				String stickerName = sticker.getName();
				int width = sticker.getWidth();
				int height = sticker.getHeight();
				int scaledStickerWidth = (int) (width / scaleFactor);
				int scaledStickerHeight = (int) (height / scaleFactor);

				// Calculate sticker position
				int adjustedSpacing = (int) (SPACING * 1.0f);
				int totalWidth = col * (scaledStickerWidth + adjustedSpacing);
				int offsetX = (PICKER_WIDTH - totalWidth) / 2; // Center horizontally
				int x = startX + offsetX + col * (scaledStickerWidth + adjustedSpacing);
				int row = i / maxColumns;
				int y = startY + row * (scaledStickerHeight + adjustedSpacing) - scrollOffset;

				// Check if hovered
				boolean isHovered = mouseX >= x && mouseX < x + EMOJI_SIZE && mouseY >= y && mouseY < y + EMOJI_SIZE;

				// Draw hover highlight if hovered
				if (isHovered) {
					// Adjust hover box size to include padding
					float centerX = x + width / 2.0f; // Center of the emoji in X
					float centerY = y + height / 2.0f; // Center of the emoji in Y
					graphics.fill(
							(int) (centerX - hoverPadding - scaledStickerWidth / 2.0f), // Left edge
							(int) (centerY - hoverPadding - scaledStickerHeight / 2.0f), // Top edge
							(int) (centerX - hoverPadding + scaledStickerWidth / 2.0f), // Right edge
							(int) (centerY - hoverPadding + scaledStickerHeight / 2.0f), // Bottom edge
							0x80FFFFFF // Semi-transparent white
					);
				}

				// Only render emoji if visible
				if (y >= pickerY + PADDING && y + scaledEmojiSize <= pickerY + PICKER_HEIGHT - PADDING) {
					graphics.pose().pushPose();
					graphics.pose().translate(x, y, 0);
					graphics.pose().scale(scaleFactor, scaleFactor, 1);

					// Draw scaled emoji
					graphics.drawString(
							Minecraft.getInstance().font,
							displayChar,
							0,
							0,
							0xFFFFFF,
							false
					);

					graphics.pose().popPose();
				}

				// Render tooltip if hovered
				if (isHovered) {
					graphics.renderTooltip(
							Minecraft.getInstance().font,
							Component.nullToEmpty(":" + stickerName + ":"),
							mouseX,
							mouseY
					);
				}

				col++;
				if (col >= maxColumns) {
					col = 0; // Reset column for new row
				}
			}

			graphics.pose().popPose(); // Pop the pose after rendering picker and emojis
		}

		// Render the Emoji Button (rendered last, since it's not part of the picker panel)
		graphics.blit(RenderType::guiTextured, EMOJI_BUTTON_TEXTURE, buttonX, buttonY, 0.0F, 0.0F, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
	}

	// Handle mouse scrolling
	public static boolean handleMouseScroll(double scrollDelta) {
		int maxColumns = 9; // Fixed 9 emojis per row
		int totalRows = (int) Math.ceil((double) stickers.size() / maxColumns);
		int maxScrollOffset = Math.max(0, (totalRows - 5) * (EMOJI_SIZE + SPACING)); // Scroll based on total rows minus visible rows

		scrollOffset = (int) Math.max(0, Math.min(scrollOffset - scrollDelta * (EMOJI_SIZE + SPACING), maxScrollOffset));
		return true;
	}

	public static boolean handleMouseClick(double mouseX, double mouseY) {
		if (pickerVisible) {
			// Close the picker if clicked outside
			boolean insidePicker = mouseX >= pickerX && mouseX < pickerX + PICKER_WIDTH &&
					mouseY >= pickerY && mouseY < pickerY + PICKER_HEIGHT;
			if (!insidePicker) {
				togglePicker();
				return true;
			}

			// Detect clicks on emojis
			int startX = pickerX + PADDING;
			int startY = pickerY + PADDING - scrollOffset;
			int maxColumns = 9; // Fixed 9 emojis per row

			for (int i = 0; i < stickers.size(); i++) {
				Sticker sticker = stickers.get(i);

				// Calculate emoji bounds
				int col = i % maxColumns;
				int row = i / maxColumns;
				int x = startX + col * (EMOJI_SIZE + SPACING);
				int y = startY + row * (EMOJI_SIZE + SPACING);

				if (mouseX >= x && mouseX < x + EMOJI_SIZE && mouseY >= y && mouseY < y + EMOJI_SIZE) {
					String stickerName = sticker.getName();
					insertStickerIntoChat(stickerName); // Insert the emoji into chat
					togglePicker(); // Close the picker after selection
					return true;
				}
			}
		} else {
			// Check if the emoji button is clicked
			boolean insideButton = mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH &&
					mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT;
			if (insideButton) {
				togglePicker();
				return true;
			}
		}

		return false;
	}

	// Insert sticker into chat
	private static void insertStickerIntoChat(String stickerName) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && minecraft.screen instanceof ChatScreen chatScreen) {
			String currentText = ((ChatScreenAccessor) chatScreen).getInput().getValue();
			((ChatScreenAccessor) chatScreen).getInput().setValue(currentText + ":" + stickerName + ":");
		}
	}

	public static boolean isPickerVisible() {
		return pickerVisible;
	}
}
