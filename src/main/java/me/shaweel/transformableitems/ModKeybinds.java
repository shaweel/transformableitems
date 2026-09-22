package me.shaweel.transformableitems;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ModKeybinds {
	public static KeyMapping OPEN_CONFIG_KEYBIND;

	public static void initialize() {
		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("transformableitems", "transformableitems"));
		OPEN_CONFIG_KEYBIND = new KeyMapping("key.transformableitems.open_config", GLFW.GLFW_KEY_F7, category);
	}
}
