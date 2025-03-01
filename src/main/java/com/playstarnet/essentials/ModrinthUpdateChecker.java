package com.playstarnet.essentials;

import com.playstarnet.essentials.StarNetEssentials;

import java.util.concurrent.CompletableFuture;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ModrinthUpdateChecker {
	private static boolean hasChecked = false;

	public static synchronized void checkForUpdates() {
		if (hasChecked) {
			StarNetEssentials.logger().info("[ModrinthUpdateChecker] Update check already performed.");
			return;
		}

		hasChecked = true;

		CompletableFuture.runAsync(() -> {
			try {
				URL url = new URL("https://api.modrinth.com/v2/project/RPyWWV8H/version");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.connect();

				if (connection.getResponseCode() == 200) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder jsonResponse = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						jsonResponse.append(line);
					}
					reader.close();
				}
			} catch (Exception e) {
				StarNetEssentials.logger().error("Failed to check for updates.", e);
			}
		});
	}

	public static synchronized void resetCheck() {
		hasChecked = false; // Allow update check on next connection
	}
}
