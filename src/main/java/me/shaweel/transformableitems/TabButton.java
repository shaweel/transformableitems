package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

public class TabButton extends Button {
	private final int index;

	public TabButton(int index, int x, int y, int width, int height, String name, OnPress onPress) {
		super(x, y, width, height, name, onPress);
		this.index = index;
	}

	@Override 
	public boolean isHovered() {
		if (!(Minecraft.getInstance().screen instanceof ConfigScreen)) {
			return false;
		}

		ConfigScreen configScreen = (ConfigScreen) Minecraft.getInstance().screen;

		return configScreen.currentTab == index;
	}
}
 