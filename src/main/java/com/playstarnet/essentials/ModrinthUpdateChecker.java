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
import java.util.concurrent.CompletableFuture;

public class ModrinthUpdateChecker {
	private static boolean hasChecked = false;

	public static synchronized void checkForUpdates() {
		// Avoid repeated checks
		if (hasChecked) {
			StarNetEssentials.logger().info("[ModrinthUpdateChecker] Update check already performed.");
			return;
		}

		CompletableFuture.runAsync(() -> {
			StarNetEssentials.logger().info("[ModrinthUpdateChecker] Starting update check...");
			int maxRetries = 3;
			int attempt = 0;

			while (attempt < maxRetries) {
				try {
					attempt++;
					URL url = new URL("https://api.modrinth.com/v2/project/RPyWWV8H/version");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000); // 5 seconds timeout
					connection.setReadTimeout(5000); // 5 seconds read timeout
					connection.connect();

					if (connection.getResponseCode() == 200) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						StringBuilder jsonResponse = new StringBuilder();
						String line;

						while ((line = reader.readLine()) != null) {
							jsonResponse.append(line);
						}
						reader.close();
						connection.disconnect();

						// Parse JSON response
						JsonArray versions = JsonParser.parseString(jsonResponse.toString()).getAsJsonArray();
						JsonObject latestVersionInfo = versions.get(0).getAsJsonObject(); // Assume first is latest
						String latestVersion = latestVersionInfo.get("version_number").getAsString();
						String downloadUrl = latestVersionInfo.getAsJsonArray("files").get(0).getAsJsonObject().get("url").getAsString();

						// Compare current version with the latest version
						if (!latestVersion.equals(Constants.VERSION)) {
							Minecraft.getInstance().execute(() -> {
								Component updateMessage = Component.literal("§e[StarNet Essentials] §fA new version is available! ")
										.append(Component.literal("§aClick here to download.")
												.setStyle(Style.EMPTY
														.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl))
														.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7Download the latest version")))));

								// Send the message to the player's chat
								if (Minecraft.getInstance().player != null) {
									Minecraft.getInstance().player.displayClientMessage(updateMessage, false);
									setHasChecked(); // Mark as checked only after sending the message
								}
							});
						} else {
							StarNetEssentials.logger().info("[ModrinthUpdateChecker] Mod is up to date.");
							setHasChecked(); // Mark as checked even if up to date
						}
						return; // Exit loop on success
					} else {
						StarNetEssentials.logger().warn("[ModrinthUpdateChecker] Failed to check for updates. Response code: " + connection.getResponseCode());
					}
				} catch (Exception e) {
					StarNetEssentials.logger().error("[ModrinthUpdateChecker] Error while checking for updates on attempt " + attempt + ": ", e);
				}
			}

			StarNetEssentials.logger().error("[ModrinthUpdateChecker] Update check failed after " + maxRetries + " attempts.");
		});
	}

	private static synchronized void setHasChecked() {
		hasChecked = true;
	}
}
