package me.shaweel.transformableitems.mixin;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public class TotemPopAnimation {
	@ModifyArg(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"
		),
		index = 0
	)
	private float modifyTranslateX(float originalX) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalX + originalX * totemConfig.xOffset;
	}

	@ModifyArg(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V"
		),
		index = 1
	)
	private float modifyTranslateY(float originalY) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalY - originalY * totemConfig.yOffset;
	}

	@ModifyArg(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V"
		),
		index = 0
	)
	private float modifyScaleX(float originalX) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalX * totemConfig.xScale;
	}

	@ModifyArg(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V"
		),
		index = 1
	)
	private float modifyScaleY(float originalY) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalY * totemConfig.yScale;
	}

	@ModifyArg(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V"
		),
		index = 2
	)
	private float modifyScaleZ(float originalZ) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalZ * totemConfig.zScale;
	}
}