package com.playstarnet.essentials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playstarnet.essentials.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModrinthUpdateChecker {
	private static boolean hasChecked = false;

	public static synchronized void checkForUpdates() {
		// Avoid repeated checks
		if (hasChecked) {
			System.out.println("[ModrinthUpdateChecker] Update check already performed.");
			return;
		}

		hasChecked = true; // Immediately set the flag to prevent further calls

		new Thread(() -> {
			System.out.println("[ModrinthUpdateChecker] Starting update check...");
			try {
				URL url = new URL("https://api.modrinth.com/v2/project/RPyWWV8H/version");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000); // Set timeout to 5 seconds
				connection.setReadTimeout(5000); // Set read timeout to 5 seconds
				connection.connect();

				if (connection.getResponseCode() == 200) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder jsonResponse = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null) {
						jsonResponse.append(line);
					}

					reader.close();

					// Parse JSON response
					JsonArray versions = JsonParser.parseString(jsonResponse.toString()).getAsJsonArray();
					JsonObject latestVersionInfo = versions.get(0).getAsJsonObject(); // Assume first is latest
					String latestVersion = latestVersionInfo.get("version_number").getAsString();
					String downloadUrl = latestVersionInfo.getAsJsonArray("files").get(0).getAsJsonObject().get("url").getAsString();

					if (!latestVersion.equals(Constants.VERSION)) {
						Minecraft.getInstance().execute(() -> {
							System.out.println("[ModrinthUpdateChecker] New version available: " + latestVersion);

							// Create a clickable chat message
							Component updateMessage = Component.literal("§e[StarNet Essentials] §fA new version is available! ")
									.append(Component.literal("§aClick here to download.")
											.setStyle(Style.EMPTY
													.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl))
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7Download the latest version")))));

							// Send the message to the player's chat
							if (Minecraft.getInstance().player != null) {
								Minecraft.getInstance().player.sendSystemMessage(updateMessage);
							}
						});
					} else {
						System.out.println("[ModrinthUpdateChecker] Mod is up-to-date: " + Constants.VERSION);
					}
				}
			} catch (Exception e) {
				System.err.println("[ModrinthUpdateChecker] Failed to fetch latest version: " + e.getMessage());
			}
		}).start();
	}
}
