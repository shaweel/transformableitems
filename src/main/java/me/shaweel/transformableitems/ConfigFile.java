package me.shaweel.transformableitems;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;

public class ConfigFile {
	public static class NormalOrFoodConfig {
		public float xScale = 1f;
		public float yScale = 1f;
		public float zScale = 1f;
		public float xOffset = 0f;
		public float yOffset = 0f;
		public float zOffset = 0f;
	}

	public static class NormalConfig extends NormalOrFoodConfig {
		public boolean itemHeightAnimations = true;
	}
	
	public static class FoodConfig extends NormalOrFoodConfig {}

	public static class TotemConfig {
		public float xScale = 1f;
		public float yScale = 1f;
		public float zScale = 1f;
		public float xOffset = 0f;
		public float yOffset = 0f;
	}

	public static class ConfigData {
		public NormalConfig normalConfig = new NormalConfig();
		public FoodConfig foodConfig = new FoodConfig();
		public TotemConfig totemConfig = new TotemConfig();
	}

	public static ConfigData configData = new ConfigData();
	private static final Path FILE = Minecraft.getMinecraft().gameDir.toPath().resolve("config/transformableitems.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void save() {
		try {
			Files.write(FILE, GSON.toJson(configData).replace("  ", "\t").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			if (!Files.exists(FILE)) return;
			String jsonString = new String(Files.readAllBytes(FILE), StandardCharsets.UTF_8);

			JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

			//Pre v1.2 JSON structure
			if (jsonObject.has("xScale")) {
				NormalConfig normalConfig = GSON.fromJson(jsonObject, NormalConfig.class);
				FoodConfig foodConfig = GSON.fromJson(jsonObject, FoodConfig.class);

				configData.normalConfig = normalConfig;
				configData.foodConfig = foodConfig;
				configData.totemConfig = new TotemConfig();
				return;
			}
			
			configData = GSON.fromJson(jsonString, ConfigData.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
