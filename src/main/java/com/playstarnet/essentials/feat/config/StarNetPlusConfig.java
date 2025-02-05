package com.playstarnet.essentials.feat.config;

import com.google.gson.*;
import com.playstarnet.essentials.feat.config.model.GeneralConfigModel;
import com.playstarnet.essentials.util.Constants;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StarNetPlusConfig {
    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve(Constants.MOD_ID + ".json").toFile();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public StarNetPlusConfig() {
        load(); // Load or create config on startup
    }

    public static void load() {
        if (configFile.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                JsonObject jsonFile = gson.fromJson(reader, JsonObject.class);

                JsonObject general = jsonFile.getAsJsonObject("general");
                if (general == null) general = new JsonObject(); // Ensure it exists

                boolean updated = false;
                for (GeneralConfigModel config : GeneralConfigModel.values()) {
                    if (general.has(config.name)) {
                        config.value = general.get(config.name).getAsBoolean();
                    } else {
                        // Add missing key with default value
                        general.addProperty(config.name, config.value);
                        updated = true;
                    }
                }

                if (updated) {
                    jsonFile.add("general", general);
                    save(jsonFile);
                }
            } catch (Exception e) {
                System.err.println("Error loading mod config file: " + e.getMessage());
            }
        } else {
            save(createDefaultConfig()); // Create default config if file doesn’t exist
        }
    }

    public static void save(JsonObject jsonData) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            gson.toJson(jsonData, writer);
        } catch (IOException e) {
            System.err.println("Error saving mod config file: " + e.getMessage());
        }
    }

    public static JsonObject createDefaultConfig() {
        JsonObject jsonFile = new JsonObject();
        JsonObject general = new JsonObject();

        for (GeneralConfigModel config : GeneralConfigModel.values()) {
            general.addProperty(config.name, config.value);
        }

        jsonFile.add("general", general);
        return jsonFile;
    }
}
