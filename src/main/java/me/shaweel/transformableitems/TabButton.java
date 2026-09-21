package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabButton extends Button {
	private final int index;

	public TabButton(int index, int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.index = index;
		this.setOverrideRenderHighlightedSprite(this::isCurrentTab);
	}

	private boolean isCurrentTab() {
		if (!(Minecraft.getInstance().screen instanceof ConfigScreen configScreen)) {
			return false;
		}

		return configScreen.currentTab == index;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		this.extractDefaultSprite(graphics);
		this.extractDefaultLabel(graphics.textRendererForWidget(this, HoveredTextEffects.NONE));
	}
}
