package me.shaweel.transformableitems.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.TotemConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.PlayerRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(ScreenEffectRenderer.class)
public class TotemPopAnimation {
	@Inject(method = "renderItemActivationAnimation", at = @At("HEAD"))
	private void transform(PlayerRenderState playerRenderState, PoseStack poseStack, float partialTicks, SubmitNodeCollector submitNodeCollector, CallbackInfo callbackInfo) {
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;

		double divisor = 1.3;

		poseStack.translate(
			totemConfig.xOffset / divisor,
			totemConfig.yOffset / divisor,
			0
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
		TotemConfig totemConfig = ConfigFile.configData.totemConfig;

		args.set(0, (float) args.get(0) * totemConfig.xScale);
		args.set(1, (float) args.get(1) * totemConfig.yScale);
		args.set(2, (float) args.get(2) * totemConfig.zScale);
	}
}