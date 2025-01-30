package com.playstarnet.essentials.feat.ui;

import com.google.gson.Gson;
import com.playstarnet.essentials.StarNetEssentials;
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

	// Caching the connection status
	private static boolean isConnected = false;
	private static long lastConnectionCheck = 0L;
	private static final long CONNECTION_CHECK_INTERVAL_MS = 1000L; // Check every 1 second

	// Relative positions (percentage of screen size)
	private static final int BASE_BUTTON_X = 5; // Base X position for button
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

	private static List<Map<String, String>> emojis = new ArrayList<>();

	// Load emojis
	static {
		loadEmojis();
		updateScaledPositions();
	}

	private static void loadEmojis() {
		String remoteUrl = "https://raw.githubusercontent.com/playstarnet/StarNet_Essentials/refs/heads/main/emoji.json";
		boolean success = false;

		// Attempt to load emojis from the remote URL
		try (java.io.InputStream inputStream = new java.net.URL(remoteUrl).openStream();
			 java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

			// Parse the JSON from the remote URL
			emojis = new Gson().fromJson(reader, new com.google.gson.reflect.TypeToken<List<Map<String, String>>>() {}.getType());
			success = true; // Mark success if no exceptions occur
			System.out.println("Successfully loaded emojis from remote URL.");
		} catch (Exception e) {
			System.err.println("Failed to load emojis from remote URL: " + e.getMessage());
		}

		// Fallback to local file if remote URL loading fails
		if (!success) {
			System.err.println("Falling back to local emoji.json file...");
			try (InputStreamReader reader = new InputStreamReader(
					Minecraft.getInstance().getResourceManager().open(ResourceLocation.parse("starnet_essentials:emojis/emoji.json")),
					StandardCharsets.UTF_8)) {

				// Parse the JSON from the local file
				emojis = new Gson().fromJson(reader, new com.google.gson.reflect.TypeToken<List<Map<String, String>>>() {}.getType());
				System.out.println("Successfully loaded emojis from local file.");
			} catch (Exception e) {
				System.err.println("Failed to load emojis from local file: " + e.getMessage());
			}
		}

		// If both fail, initialize an empty list as a last resort
		if (emojis == null) {
			emojis = new ArrayList<>();
			System.err.println("Emojis list is empty. Failed to load from both remote URL and local file.");
		}
	}

	private static void updateScaledPositions() {
		Minecraft minecraft = Minecraft.getInstance();
		int screenWidth = minecraft.getWindow().getGuiScaledWidth();
		int screenHeight = minecraft.getWindow().getGuiScaledHeight();

		// Position the button to the very left of the screen
		buttonX = 5; // Fixed padding from the left edge of the screen
		buttonY = screenHeight - 35; // Move the button slightly lower by 5 pixels

		// Position the picker relative to the button but above and slightly to the left
		pickerX = buttonX - 0; // Shift the picker 10 pixels to the left to align with the button
		pickerY = buttonY - PICKER_HEIGHT - 5; // Place it slightly above the button
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
			graphics.blit(EMOJI_PICKER_TEXTURE, pickerX, pickerY, PICKER_WIDTH, PICKER_HEIGHT, 0.0F, 0.0F, 122, 75, 122, 75);

			// Render the emojis
			float scaleFactor = 1.25f; // Emoji size scaling
			int scaledEmojiSize = (int) (EMOJI_SIZE * scaleFactor);
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
				boolean isHovered = mouseX >= x && mouseX < x + scaledEmojiSize && mouseY >= y && mouseY < y + scaledEmojiSize;

				// Draw hover highlight if hovered
				if (isHovered) {
					renderEmojiHighlight(graphics, x, y, scaledEmojiSize);
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

	private static void renderEmojiHighlight(GuiGraphics graphics, int x, int y, int scaledEmojiSize) {
		int hoverSize = 15; // Slightly larger than the emoji size of 16x16 pixels
		int hoverOffset = (hoverSize - scaledEmojiSize) / 2; // Center the highlight around the emoji

		// Calculate the hover box position and size
		int hoverBoxLeft = x - hoverOffset;
		int hoverBoxTop = y - hoverOffset;
		int hoverBoxRight = x + scaledEmojiSize + hoverOffset - 2;
		int hoverBoxBottom = y + scaledEmojiSize + hoverOffset - 2;

		// Draw the hover highlight
		graphics.fill(
				hoverBoxLeft, // Left edge
				hoverBoxTop, // Top edge
				hoverBoxRight, // Right edge
				hoverBoxBottom, // Bottom edge
				0x80FFFFFF // Semi-transparent white
		);
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
