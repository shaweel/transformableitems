package me.shaweel.transformableitems;

import java.util.Locale;

import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class CustomGameRenderer extends GameRenderer {
	public CustomGameRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		super(mcIn, resourceManagerIn);
	}

	@Override
	public void updateCameraAndRender(float partialTicks, long nanoTime, boolean renderWorldIn) {
		if (!Minecraft.getInstance().isGameFocused() && Minecraft.getInstance().gameSettings.pauseOnLostFocus && (!Minecraft.getInstance().gameSettings.touchscreen || !Minecraft.getInstance().mouseHelper.isRightDown())) {
			if (Util.milliTime() - this.prevFrameTime > 500L) {
				Minecraft.getInstance().displayInGameMenu();
			}
		} else {
			this.prevFrameTime = Util.milliTime();
		}

		if (!Minecraft.getInstance().skipRenderWorld) {
			int i = (int)(Minecraft.getInstance().mouseHelper.getMouseX() * (double)Minecraft.getInstance().mainWindow.getScaledWidth() / (double)Minecraft.getInstance().mainWindow.getWidth());
			int j = (int)(Minecraft.getInstance().mouseHelper.getMouseY() * (double)Minecraft.getInstance().mainWindow.getScaledHeight() / (double)Minecraft.getInstance().mainWindow.getHeight());
			int k = Minecraft.getInstance().gameSettings.limitFramerate;
			if (renderWorldIn && Minecraft.getInstance().world != null) {
				Minecraft.getInstance().profiler.startSection("level");
				int l = Math.min(Minecraft.getDebugFPS(), k);
				l = Math.max(l, 60);
				long i1 = Util.nanoTime() - nanoTime;
				long j1 = Math.max((long)(1000000000 / l / 4) - i1, 0L);
				this.renderWorld(partialTicks, Util.nanoTime() + j1);
				if (Minecraft.getInstance().isSingleplayer() && this.timeWorldIcon < Util.milliTime() - 1000L) {
					this.timeWorldIcon = Util.milliTime();
					if (!Minecraft.getInstance().getIntegratedServer().isWorldIconSet()) {
						this.createWorldIcon();
					}
				}

				if (OpenGlHelper.shadersSupported) {
					Minecraft.getInstance().worldRenderer.renderEntityOutlineFramebuffer();
					if (this.shaderGroup != null && this.useShader) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.shaderGroup.render(partialTicks);
						GlStateManager.popMatrix();
					}

					Minecraft.getInstance().getFramebuffer().bindFramebuffer(true);
				}

				Minecraft.getInstance().profiler.endStartSection("gui");
				if (!Minecraft.getInstance().gameSettings.hideGUI || Minecraft.getInstance().currentScreen != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					Minecraft.getInstance().mainWindow.setupOverlayRendering();
					this.renderCustomItemActivation(Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight(), partialTicks);
					Minecraft.getInstance().ingameGUI.renderGameOverlay(partialTicks);
				}

				Minecraft.getInstance().profiler.endSection();
			} else {
				GlStateManager.viewport(0, 0, Minecraft.getInstance().mainWindow.getFramebufferWidth(), Minecraft.getInstance().mainWindow.getFramebufferHeight());
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				Minecraft.getInstance().mainWindow.setupOverlayRendering();
				// Forge: Fix MC-112292
				TileEntityRendererDispatcher.instance.textureManager = Minecraft.getInstance().getTextureManager();
				// Forge: also fix rendering text before entering world (not part of MC-112292, but the same reason)
				TileEntityRendererDispatcher.instance.fontRenderer = Minecraft.getInstance().fontRenderer;
			}

			if (Minecraft.getInstance().currentScreen != null) {
				GlStateManager.clear(256);

				try {
					net.minecraftforge.client.ForgeHooksClient.drawScreen(Minecraft.getInstance().currentScreen, i, j, Minecraft.getInstance().getTickLength());
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
					crashreportcategory.addDetail("Screen name", () -> {
						return Minecraft.getInstance().currentScreen.getClass().getCanonicalName();
					});
					crashreportcategory.addDetail("Mouse location", () -> {
						return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, Minecraft.getInstance().mouseHelper.getMouseX(), Minecraft.getInstance().mouseHelper.getMouseY());
					});
					crashreportcategory.addDetail("Screen size", () -> {
						return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight(), Minecraft.getInstance().mainWindow.getFramebufferWidth(), Minecraft.getInstance().mainWindow.getFramebufferHeight(), Minecraft.getInstance().mainWindow.getGuiScaleFactor());
					});
					throw new ReportedException(crashreport);
				}
			}

		}
	}

	private void renderCustomItemActivation(int widthsp, int heightScaled, float partialTicks) {
		if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
			int i = 40 - this.itemActivationTicks;
			float f = ((float)i + partialTicks) / 40.0F;
			float f1 = f * f;
			float f2 = f * f1;
			float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
			float f4 = f3 * (float)Math.PI;
			float f5 = this.itemActivationOffX * (float)(widthsp / 4);
			float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
			GlStateManager.enableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttrib();
			GlStateManager.enableDepthTest();
			GlStateManager.disableCull();
			RenderHelper.enableStandardItemLighting();
			TotemConfig totemConfig = ConfigFile.configData.totemConfig;

			float originalXOffset = (float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F));
			float originalYOffset = (float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F));
			float originalZOffset = -50.0F;
			
			float xOffset = originalXOffset + originalXOffset * totemConfig.xOffset;
			float yOffset = originalYOffset - originalYOffset * totemConfig.yOffset;
			float zOffset = originalZOffset;
			GlStateManager.translatef(xOffset, yOffset, zOffset);
			
			float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
			float originalXScale = f7;
			float originalYScale = -f7;
			float originalZScale = f7;
			
			float xScale = originalXScale * totemConfig.xScale;
			float yScale = originalYScale * totemConfig.yScale;
			float zScale = originalZScale * totemConfig.zScale;

			GlStateManager.scalef(xScale, yScale, zScale);
			GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
			Minecraft.getInstance().getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableCull();
			GlStateManager.disableDepthTest();
		}
	}
}
