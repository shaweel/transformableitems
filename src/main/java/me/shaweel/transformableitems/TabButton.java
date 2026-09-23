package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class TabButton extends Button {
	private final int index;

	public TabButton(int index, int x, int y, int width, int height, ITextComponent message, IPressable onPress) {
		super(x, y, width, height, message, onPress);
		this.index = index;
	}

	@Override 
	public boolean isHovered() {
		if (!(Minecraft.getInstance().currentScreen instanceof ConfigScreen)) {
			return false;
		}

		ConfigScreen configScreen = (ConfigScreen) Minecraft.getInstance().currentScreen;

		return configScreen.currentTab == index;
	}
}