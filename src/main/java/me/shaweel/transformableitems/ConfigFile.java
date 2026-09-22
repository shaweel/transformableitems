package me.shaweel.transformableitems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;

public class ConfigFile {
	public static class TabConfig {
		public float xScale = 1f;
		public float yScale = 1f;
		public float zScale = 1f;
		public float xOffset = 0f;
		public float yOffset = 0f;
		public float zOffset = 0f;
	}

	public static class ConfigData {
		public TabConfig[] tabs = {
			new TabConfig(),
			new TabConfig(),
			new TabConfig()
		};

		public boolean itemHeightAnimations = true;

		public TabConfig get(int index) {
			return tabs[index];
		}
	}

	public static ConfigData configData = new ConfigData();
	private static final Path FILE = Minecraft.getInstance().gameDirectory.toPath().resolve("config/transformableitems.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(configData).replace("  ", "\t"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			if (!Files.exists(FILE)) return;
			String jsonString = Files.readString(FILE);

			JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

			//Pre v1.2 JSON structure
			if (jsonObject.has("xScale")) {
				TabConfig oldConfig = GSON.fromJson(jsonObject, TabConfig.class);

				configData.tabs[0] = oldConfig;
				configData.tabs[1] = oldConfig;
				configData.tabs[2] = new TabConfig();

				configData.itemHeightAnimations = jsonObject.get("itemHeightAnimations").getAsBoolean();
				return;
			}
			
			configData = GSON.fromJson(jsonString, ConfigData.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
