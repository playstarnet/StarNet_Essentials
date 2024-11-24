package com.playstarnet.essentials.feat.ui;

import com.google.gson.Gson;
import com.playstarnet.essentials.mixins.ext.ChatScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmojiPickerScreen {
	private static final ResourceLocation EMOJI_PICKER_TEXTURE = ResourceLocation.parse("starnet_essentials:textures/gui/emojipicker.png");
	private static final ResourceLocation EMOJI_BUTTON_TEXTURE = ResourceLocation.parse("starnet_essentials:textures/gui/button.png");
	private static boolean pickerVisible = false;

	// Relative positions (percentage of screen size)
	private static final int BASE_BUTTON_X = 5; // Base X position for button
	private static final int BASE_BUTTON_Y = 325; // Base Y position for button
	private static final int BASE_PICKER_X = 5; // Base X position for picker
	private static final int BASE_PICKER_Y = 246; // Base Y position for picker
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

	private static List<Map<String, String>> emojis = new ArrayList<>();

	// Load emojis
	static {
		loadEmojis();
		updateScaledPositions();
	}

	private static void loadEmojis() {
		try (InputStreamReader reader = new InputStreamReader(
				Minecraft.getInstance().getResourceManager().open(ResourceLocation.parse("starnet_essentials:emojis/emoji.json")),
				StandardCharsets.UTF_8)) {
			emojis = new Gson().fromJson(reader, new com.google.gson.reflect.TypeToken<List<Map<String, String>>>() {}.getType());
		} catch (Exception e) {
			System.err.println("Failed to load emojis: " + e.getMessage());
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

	// Toggle picker visibility
	public static void togglePicker() {
		pickerVisible = !pickerVisible;
		scrollOffset = 0;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	// Render the EmojiPicker panel
	public static void renderPicker(GuiGraphics graphics, int mouseX, int mouseY) {
		// Ensure positions are updated dynamically
		updateScaledPositions();

		if (pickerVisible) {
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 500); // Push picker and emojis to a higher layer (above chat)

			// Render the Emoji Picker panel
			graphics.blit(EMOJI_PICKER_TEXTURE, pickerX, pickerY, PICKER_WIDTH, PICKER_HEIGHT, 0.0F, 0.0F, 122, 75, 122, 75);

			// Render the emojis
			float scaleFactor = 1.25f; // Emoji size scaling
			int scaledEmojiSize = (int) (EMOJI_SIZE * scaleFactor);
			int hoverPadding = 1; // Additional pixels for hover box
			int startX = pickerX + PADDING; // Padding for left margin
			int startY = pickerY + PADDING; // Padding for top margin
			int maxColumns = 9; // 9 emojis per row
			int col = 0;

			for (int i = 0; i < emojis.size(); i++) {
				Map<String, String> emoji = emojis.get(i);
				String displayChar = emoji.get("displayChar");
				String emojiName = emoji.get("name");

				// Calculate emoji position
				int x = startX + col * (EMOJI_SIZE + SPACING);
				int row = i / maxColumns;
				int y = startY + row * (EMOJI_SIZE + SPACING) - scrollOffset;

				// Check if hovered
				boolean isHovered = mouseX >= x && mouseX < x + EMOJI_SIZE && mouseY >= y && mouseY < y + EMOJI_SIZE;

				// Draw hover highlight if hovered
				if (isHovered) {
					int hoverBoxSize = (int) (scaledEmojiSize + hoverPadding * 2); // Adjust hover box size to include padding
					float centerX = x + scaledEmojiSize / 2.0f; // Center of the emoji in X
					float centerY = y + scaledEmojiSize / 2.0f; // Center of the emoji in Y
					graphics.fill(
							(int) (centerX - hoverBoxSize / 2.0f), // Left edge
							(int) (centerY - hoverBoxSize / 2.0f), // Top edge
							(int) (centerX + hoverBoxSize / 2.0f), // Right edge
							(int) (centerY + hoverBoxSize / 2.0f), // Bottom edge
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
							Component.nullToEmpty(":" + emojiName + ":"),
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
		graphics.blit(EMOJI_BUTTON_TEXTURE, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, 0.0F, 0.0F, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
	}

	// Handle mouse scrolling
	public static boolean handleMouseScroll(double scrollDelta) {
		int maxColumns = 9; // Fixed 9 emojis per row
		int totalRows = (int) Math.ceil((double) emojis.size() / maxColumns);
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

			for (int i = 0; i < emojis.size(); i++) {
				Map<String, String> emoji = emojis.get(i);

				// Calculate emoji bounds
				int col = i % maxColumns;
				int row = i / maxColumns;
				int x = startX + col * (EMOJI_SIZE + SPACING);
				int y = startY + row * (EMOJI_SIZE + SPACING);

				if (mouseX >= x && mouseX < x + EMOJI_SIZE && mouseY >= y && mouseY < y + EMOJI_SIZE) {
					String emojiName = emoji.get("name");
					insertEmojiIntoChat(emojiName); // Insert the emoji into chat
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

	// Insert emoji into chat
	private static void insertEmojiIntoChat(String emojiName) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && minecraft.screen instanceof ChatScreen) {
			ChatScreen chatScreen = (ChatScreen) minecraft.screen;
			String currentText = ((ChatScreenAccessor) chatScreen).getInput().getValue();
			((ChatScreenAccessor) chatScreen).getInput().setValue(currentText + ":" + emojiName + ":");
		}
	}

	public static boolean isPickerVisible() {
		return pickerVisible;
	}
}
