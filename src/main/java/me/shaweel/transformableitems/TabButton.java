package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphics.HoveredTextEffects;
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
	protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
		this.renderDefaultSprite(guiGraphics);
		this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, HoveredTextEffects.NONE));
	}
}
