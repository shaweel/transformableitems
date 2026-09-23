package me.shaweel.transformableitems;

import java.util.Locale;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class CustomGameRenderer extends GameRenderer {
	public CustomGameRenderer(Minecraft mcIn, IResourceManager resourceManagerIn, RenderTypeBuffers renderTypeBuffersIn) {
		super(mcIn, resourceManagerIn, renderTypeBuffersIn);
	}

	@Override
	public void updateCameraAndRender(float partialTicks, long nanoTime, boolean renderWorldIn) {
		if (!Minecraft.getInstance().isGameFocused() && Minecraft.getInstance().gameSettings.pauseOnLostFocus && (!Minecraft.getInstance().gameSettings.touchscreen || !Minecraft.getInstance().mouseHelper.isRightDown())) {
			if (Util.milliTime() - this.prevFrameTime > 500L) {
				Minecraft.getInstance().displayInGameMenu(false);
			}
		} else {
			this.prevFrameTime = Util.milliTime();
		}

		if (!Minecraft.getInstance().skipRenderWorld) {
			int i = (int)(Minecraft.getInstance().mouseHelper.getMouseX() * (double)Minecraft.getInstance().getMainWindow().getScaledWidth() / (double)Minecraft.getInstance().getMainWindow().getWidth());
			int j = (int)(Minecraft.getInstance().mouseHelper.getMouseY() * (double)Minecraft.getInstance().getMainWindow().getScaledHeight() / (double)Minecraft.getInstance().getMainWindow().getHeight());
			MatrixStack matrixstack = new MatrixStack();
			RenderSystem.viewport(0, 0, Minecraft.getInstance().getMainWindow().getFramebufferWidth(), Minecraft.getInstance().getMainWindow().getFramebufferHeight());
			if (renderWorldIn && Minecraft.getInstance().world != null) {
				Minecraft.getInstance().getProfiler().startSection("level");
				this.renderWorld(partialTicks, nanoTime, matrixstack);
				if (Minecraft.getInstance().isSingleplayer() && this.timeWorldIcon < Util.milliTime() - 1000L) {
					this.timeWorldIcon = Util.milliTime();
					if (!Minecraft.getInstance().getIntegratedServer().isWorldIconSet()) {
						this.createWorldIcon();
					}
				}

				Minecraft.getInstance().worldRenderer.renderEntityOutlineFramebuffer();
				if (this.shaderGroup != null && this.useShader) {
					RenderSystem.disableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.disableAlphaTest();
					RenderSystem.enableTexture();
					RenderSystem.matrixMode(5890);
					RenderSystem.pushMatrix();
					RenderSystem.loadIdentity();
					this.shaderGroup.render(partialTicks);
					RenderSystem.popMatrix();
				}

				Minecraft.getInstance().getFramebuffer().bindFramebuffer(true);
			}

			MainWindow mainwindow = Minecraft.getInstance().getMainWindow();
			RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
			RenderSystem.matrixMode(5889);
			RenderSystem.loadIdentity();
			RenderSystem.ortho(0.0D, (double)mainwindow.getFramebufferWidth() / mainwindow.getGuiScaleFactor(), (double)mainwindow.getFramebufferHeight() / mainwindow.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
			RenderSystem.matrixMode(5888);
			RenderSystem.loadIdentity();
			RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
			RenderHelper.setupGui3DDiffuseLighting();
			if (renderWorldIn && Minecraft.getInstance().world != null) {
				Minecraft.getInstance().getProfiler().endStartSection("gui");
				if (!Minecraft.getInstance().gameSettings.hideGUI || Minecraft.getInstance().currentScreen != null) {
					RenderSystem.defaultAlphaFunc();
					this.renderCustomItemActivation(Minecraft.getInstance().getMainWindow().getScaledWidth(), Minecraft.getInstance().getMainWindow().getScaledHeight(), partialTicks);
					Minecraft.getInstance().ingameGUI.renderGameOverlay(partialTicks);
					RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
				}

				Minecraft.getInstance().getProfiler().endSection();
			}

			if (Minecraft.getInstance().loadingGui != null) {
				try {
					Minecraft.getInstance().loadingGui.render(i, j, Minecraft.getInstance().getTickLength());
				} catch (Throwable throwable1) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Rendering overlay");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Overlay render details");
					crashreportcategory.addDetail("Overlay name", () -> {
						return Minecraft.getInstance().loadingGui.getClass().getCanonicalName();
					});
					throw new ReportedException(crashreport);
				}
			} else if (Minecraft.getInstance().currentScreen != null) {
				try {
					net.minecraftforge.client.ForgeHooksClient.drawScreen(Minecraft.getInstance().currentScreen, i, j, Minecraft.getInstance().getTickLength());
				} catch (Throwable throwable) {
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Screen render details");
					crashreportcategory1.addDetail("Screen name", () -> {
						return Minecraft.getInstance().currentScreen.getClass().getCanonicalName();
					});
					crashreportcategory1.addDetail("Mouse location", () -> {
						return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, Minecraft.getInstance().mouseHelper.getMouseX(), Minecraft.getInstance().mouseHelper.getMouseY());
					});
					crashreportcategory1.addDetail("Screen size", () -> {
						return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", Minecraft.getInstance().getMainWindow().getScaledWidth(), Minecraft.getInstance().getMainWindow().getScaledHeight(), Minecraft.getInstance().getMainWindow().getFramebufferWidth(), Minecraft.getInstance().getMainWindow().getFramebufferHeight(), Minecraft.getInstance().getMainWindow().getGuiScaleFactor());
					});
					throw new ReportedException(crashreport1);
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
			RenderSystem.enableAlphaTest();
			RenderSystem.pushMatrix();
			RenderSystem.pushLightingAttributes();
			RenderSystem.enableDepthTest();
			RenderSystem.disableCull();
			MatrixStack matrixstack = new MatrixStack();
			TotemConfig totemConfig = ConfigFile.configData.totemConfig;

			matrixstack.push();
			double originalXOffset = (double)((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)));
			double originalYOffset = (double)((float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)));
			double originalZOffset = -50.0D;
			
			double xOffset = originalXOffset + originalXOffset * totemConfig.xOffset;
			double yOffset = originalYOffset - originalYOffset * totemConfig.yOffset;
			double zOffset = originalZOffset;

			matrixstack.translate(xOffset, yOffset, zOffset);
			float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
			float originalXScale = f7;
			float originalYScale = -f7;
			float originalZScale = f7;
			
			float xScale = originalXScale * totemConfig.xScale;
			float yScale = originalYScale * totemConfig.yScale;
			float zScale = originalZScale * totemConfig.zScale;

			matrixstack.scale(xScale, yScale, zScale);
			matrixstack.rotate(Vector3f.YP.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(f4))));
			matrixstack.rotate(Vector3f.XP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			matrixstack.rotate(Vector3f.ZP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderTypeBuffers.getBufferSource();
			Minecraft.getInstance().getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixstack, irendertypebuffer$impl);
			matrixstack.pop();
			irendertypebuffer$impl.finish();
			RenderSystem.popAttributes();
			RenderSystem.popMatrix();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
		}
	}
}
