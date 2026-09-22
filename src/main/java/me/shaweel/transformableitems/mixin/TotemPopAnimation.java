package me.shaweel.transformableitems.mixin;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class TotemPopAnimation {
	@ModifyArgs(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
		)
	)
	private void modifyTranslate(Args args) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;

		float originalX = args.get(0);
		float originalY = args.get(1);

		args.set(0, originalX + originalX * totemConfig.xOffset);
		args.set(1, originalY + originalY * totemConfig.yOffset);
	}

	@ModifyArgs(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
		)
	)
	private void modifyScale(Args args) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;

		args.set(0, (float) args.get(0) * totemConfig.xScale);
		args.set(1, (float) args.get(1) * totemConfig.yScale);
		args.set(2, (float) args.get(2) * totemConfig.zScale);
	}
}