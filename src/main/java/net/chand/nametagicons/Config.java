package net.chand.nametagicons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public boolean transparentNametagBackground = false;
    public boolean nametagIconsEnabled = true;
    public String iconPath = "";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nametag-icons.json");

    public static Config load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
                Config cfg = GSON.fromJson(json, Config.class);
                if (cfg == null) return new Config();
                return cfg;
            }
        } catch (IOException e) {
            NametagIcons.LOGGER.warn("Failed to load config", e);
        }
        return new Config();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            NametagIcons.LOGGER.warn("Failed to save config", e);
        }
    }
}
