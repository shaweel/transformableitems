package me.shaweel.transformableitems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.shaweel.transformableitems.ConfigFile.ConfigData;
import me.shaweel.transformableitems.ConfigFile.FoodConfig;
import me.shaweel.transformableitems.ConfigFile.NormalConfig;
import me.shaweel.transformableitems.ConfigFile.NormalOrFoodConfig;
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

	public static class ConfigData {
		public NormalConfig normalConfig = new NormalConfig();
		public FoodConfig foodConfig = new FoodConfig();
	}

	public static ConfigData configData = new ConfigData();
	private static final File FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/transformableitems.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void save() {
		try {
			FILE.getParentFile().mkdirs();

			Writer writer = new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8");

			writer.write(GSON.toJson(configData).replace("  ", "\t"));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			if (!FILE.exists()) return;

			InputStream inputStream = new FileInputStream(FILE);
			byte[] data = new byte[(int) FILE.length()];

			inputStream.read(data);
			inputStream.close();

			String jsonString = new String(data, "UTF-8");

			JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

			//Pre v1.2 JSON structure
			if (jsonObject.has("xScale")) {
				NormalConfig normalConfig = GSON.fromJson(jsonObject, NormalConfig.class);
				FoodConfig foodConfig = GSON.fromJson(jsonObject, FoodConfig.class);

				configData.normalConfig = normalConfig;
				configData.foodConfig = foodConfig;
				return;
			}
			
			configData = GSON.fromJson(jsonString, ConfigData.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
