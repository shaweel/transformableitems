package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class TabButton extends GuiButton {
	private final int index;

	public TabButton(int id, int index, int x, int y, int width, int height, String message) {
		super(id, x, y, width, height, message);
		this.index = index;
	}

	public boolean isCurrent() {
		if (!(Minecraft.getInstance().currentScreen instanceof ConfigScreen)) {
			return false;
		}

		ConfigScreen configScreen = (ConfigScreen) Minecraft.getInstance().currentScreen;

		return configScreen.currentTab == index;
	}

	@Override 
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft minecraft = Minecraft.getInstance();
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = isCurrent();
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			this.renderBg(minecraft, mouseX, mouseY);
			int j = 14737632;
			if (packedFGColor != 0)
			{
				j = packedFGColor;
			}
			else
			if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
		}
	}

}
