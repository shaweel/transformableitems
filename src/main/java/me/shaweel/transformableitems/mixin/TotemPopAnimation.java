package me.shaweel.transformableitems.mixin;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.TabConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.mojang.blaze3d.vertex.PoseStack;


@Mixin(ScreenEffectRenderer.class)
public class TotemPopAnimation {
	@Inject(method = "renderItemActivationAnimation", at = @At("HEAD"))
	private void transform(PoseStack poseStack, float f, CallbackInfo callbackInfo) {
		TabConfig tabConfig = ConfigFile.configData.get(2);

		double divisor = 1.3;

		poseStack.translate(
			tabConfig.xOffset / divisor,
			tabConfig.yOffset / divisor,
			tabConfig.zOffset / divisor
		);
	}
	@ModifyArgs(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
		)
	)
	private void modifyScale(Args args) {
		TabConfig tabConfig = ConfigFile.configData.get(2);

		args.set(0, (float) args.get(0) * tabConfig.xScale);
		args.set(1, (float) args.get(1) * tabConfig.yScale);
		args.set(2, (float) args.get(2) * tabConfig.zScale);
	}
}