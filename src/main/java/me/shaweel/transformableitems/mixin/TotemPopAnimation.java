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
		method = "renderItemActivation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V"
		),
		index = 0
	)
	private double modifyTranslateX(double originalX) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalX + originalX * totemConfig.xOffset;
	}

	@ModifyArg(
		method = "renderItemActivation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V"
		),
		index = 1
	)
	private double modifyTranslateY(double originalY) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalY - originalY * totemConfig.yOffset;
	}

	@ModifyArg(
		method = "renderItemActivation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;scale(FFF)V"
		),
		index = 0
	)
	private float modifyScaleX(float originalX) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalX * totemConfig.xScale;
	}

	@ModifyArg(
		method = "renderItemActivation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;scale(FFF)V"
		),
		index = 1
	)
	private float modifyScaleY(float originalY) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalY * totemConfig.yScale;
	}

	@ModifyArg(
		method = "renderItemActivation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;scale(FFF)V"
		),
		index = 2
	)
	private float modifyScaleZ(float originalZ) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;
		return originalZ * totemConfig.zScale;
	}
}