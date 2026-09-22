package me.shaweel.transformableitems;

import net.minecraft.client.KeyMapping;

public class ModKeybinds {
	public static KeyMapping OPEN_CONFIG_KEYBIND;

	public static void initialize() {
		OPEN_CONFIG_KEYBIND = new KeyMapping("key.transformableitems.open_config", 296, "key.categories.transformableitems");
	}
}
