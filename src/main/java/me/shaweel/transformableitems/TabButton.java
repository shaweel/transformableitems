package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabButton extends Button {
	private final int index;

	public TabButton(int index, int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress);
		this.index = index;
	}

	@Override 
	public boolean isHovered() {
		if (!(Minecraft.getInstance().screen instanceof ConfigScreen configScreen)) {
			return false;
		}

		return configScreen.currentTab == index;
	}
}
 