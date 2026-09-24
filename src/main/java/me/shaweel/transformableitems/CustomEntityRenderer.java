package me.shaweel.transformableitems;

import java.util.Locale;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class CustomEntityRenderer extends EntityRenderer {
	public CustomEntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		super(mcIn, resourceManagerIn);
	}

	@Override
	public void updateCameraAndRender(float partialTicks, long nanoTime) {
		boolean flag = Display.isActive();

		if (!flag && Minecraft.getMinecraft().gameSettings.pauseOnLostFocus && (!Minecraft.getMinecraft().gameSettings.touchscreen || !Mouse.isButtonDown(1)))
		{
			if (Minecraft.getSystemTime() - this.prevFrameTime > 500L)
			{
				Minecraft.getMinecraft().displayInGameMenu();
			}
		}
		else
		{
			this.prevFrameTime = Minecraft.getSystemTime();
		}

		Minecraft.getMinecraft().profiler.startSection("mouse");

		if (flag && Minecraft.IS_RUNNING_ON_MAC && Minecraft.getMinecraft().inGameHasFocus && !Mouse.isInsideWindow())
		{
			Mouse.setGrabbed(false);
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
			Mouse.setGrabbed(true);
		}

		if (Minecraft.getMinecraft().inGameHasFocus && flag)
		{
			Minecraft.getMinecraft().mouseHelper.mouseXYChange();
			Minecraft.getMinecraft().getTutorial().handleMouse(Minecraft.getMinecraft().mouseHelper);
			float f = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			float f2 = (float)Minecraft.getMinecraft().mouseHelper.deltaX * f1;
			float f3 = (float)Minecraft.getMinecraft().mouseHelper.deltaY * f1;
			int i = 1;

			if (Minecraft.getMinecraft().gameSettings.invertMouse)
			{
				i = -1;
			}

			if (Minecraft.getMinecraft().gameSettings.smoothCamera)
			{
				this.smoothCamYaw += f2;
				this.smoothCamPitch += f3;
				float f4 = partialTicks - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = partialTicks;
				f2 = this.smoothCamFilterX * f4;
				f3 = this.smoothCamFilterY * f4;
				Minecraft.getMinecraft().player.turn(f2, f3 * (float)i);
			}
			else
			{
				this.smoothCamYaw = 0.0F;
				this.smoothCamPitch = 0.0F;
				Minecraft.getMinecraft().player.turn(f2, f3 * (float)i);
			}
		}

		Minecraft.getMinecraft().profiler.endSection();

		if (!Minecraft.getMinecraft().skipRenderWorld)
		{
			anaglyphEnable = Minecraft.getMinecraft().gameSettings.anaglyph;
			final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			int i1 = scaledresolution.getScaledWidth();
			int j1 = scaledresolution.getScaledHeight();
			final int k1 = Mouse.getX() * i1 / Minecraft.getMinecraft().displayWidth;
			final int l1 = j1 - Mouse.getY() * j1 / Minecraft.getMinecraft().displayHeight - 1;
			int i2 = Minecraft.getMinecraft().gameSettings.limitFramerate;

			if (Minecraft.getMinecraft().world != null)
			{
				Minecraft.getMinecraft().profiler.startSection("level");
				int j = Math.min(Minecraft.getDebugFPS(), i2);
				j = Math.max(j, 60);
				long k = System.nanoTime() - nanoTime;
				long l = Math.max((long)(1000000000 / j / 4) - k, 0L);
				this.renderWorld(partialTicks, System.nanoTime() + l);

				if (Minecraft.getMinecraft().isSingleplayer() && this.timeWorldIcon < Minecraft.getSystemTime() - 1000L)
				{
					this.timeWorldIcon = Minecraft.getSystemTime();

					if (!Minecraft.getMinecraft().getIntegratedServer().isWorldIconSet())
					{
						this.createWorldIcon();
					}
				}

				if (OpenGlHelper.shadersSupported)
				{
					Minecraft.getMinecraft().renderGlobal.renderEntityOutlineFramebuffer();

					if (this.shaderGroup != null && this.useShader)
					{
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.shaderGroup.render(partialTicks);
						GlStateManager.popMatrix();
					}

					Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
				}

				this.renderEndNanoTime = System.nanoTime();
				Minecraft.getMinecraft().profiler.endStartSection("gui");

				if (!Minecraft.getMinecraft().gameSettings.hideGUI || Minecraft.getMinecraft().currentScreen != null)
				{
					GlStateManager.alphaFunc(516, 0.1F);
					this.setupOverlayRendering();
					this.renderCustomItemActivation(i1, j1, partialTicks);
					Minecraft.getMinecraft().ingameGUI.renderGameOverlay(partialTicks);
				}

				Minecraft.getMinecraft().profiler.endSection();
			}
			else
			{
				GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.setupOverlayRendering();
				this.renderEndNanoTime = System.nanoTime();
				// Forge: Fix MC-112292
				TileEntityRendererDispatcher.instance.renderEngine = Minecraft.getMinecraft().getTextureManager();
				// Forge: also fix rendering text before entering world (not part of MC-112292, but the same reason)
				TileEntityRendererDispatcher.instance.fontRenderer = Minecraft.getMinecraft().fontRenderer;
			}

			if (Minecraft.getMinecraft().currentScreen != null)
			{
				GlStateManager.clear(256);

				try
				{
					net.minecraftforge.client.ForgeHooksClient.drawScreen(Minecraft.getMinecraft().currentScreen, k1, l1, Minecraft.getMinecraft().getTickLength());
				}
				catch (Throwable throwable)
				{
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
					crashreportcategory.addDetail("Screen name", new ICrashReportDetail<String>()
					{
						public String call() throws Exception
						{
							return Minecraft.getMinecraft().currentScreen.getClass().getCanonicalName();
						}
					});
					crashreportcategory.addDetail("Mouse location", new ICrashReportDetail<String>()
					{
						public String call() throws Exception
						{
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", k1, l1, Mouse.getX(), Mouse.getY());
						}
					});
					crashreportcategory.addDetail("Screen size", new ICrashReportDetail<String>()
					{
						public String call() throws Exception
						{
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, scaledresolution.getScaleFactor());
						}
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
			float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
			float f4 = f3 * (float)Math.PI;
			float f5 = this.itemActivationOffX * (float)(widthsp / 4);
			float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
			GlStateManager.enableAlpha();
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.enableDepth();
			GlStateManager.disableCull();
			RenderHelper.enableStandardItemLighting();
			TotemConfig totemConfig = ConfigFile.configData.totemConfig;

			float originalXOffset = (float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F));
			float originalYOffset = (float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F));
			float originalZOffset = -50.0F;
			
			float xOffset = originalXOffset + originalXOffset * totemConfig.xOffset;
			float yOffset = originalYOffset - originalYOffset * totemConfig.yOffset;
			float zOffset = originalZOffset;
			GlStateManager.translate(xOffset, yOffset, zOffset);
			
			float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
			float originalXScale = f7;
			float originalYScale = -f7;
			float originalZScale = f7;
			
			float xScale = originalXScale * totemConfig.xScale;
			float yScale = originalYScale * totemConfig.yScale;
			float zScale = originalZScale * totemConfig.zScale;

			GlStateManager.scale(xScale, yScale, zScale);
			GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
			Minecraft.getMinecraft().getRenderItem().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableCull();
			GlStateManager.disableDepth();
		}
	}
}
