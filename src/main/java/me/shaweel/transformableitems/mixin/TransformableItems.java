package me.shaweel.transformableitems.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.NormalOrFoodConfig;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class TransformableItems {
	private boolean isEating(PlayerRenderState playerState, ItemStack itemStack) {
		return playerState.avatarRenderState.isUsingItem && itemStack.getUseAnimation() == ItemUseAnimation.EAT;
	}

	private boolean isLeftHand(PlayerRenderState playerState, InteractionHand hand) {
		boolean leftHanded = playerState.avatarRenderState.mainArm == HumanoidArm.LEFT;
		boolean leftHand = hand.equals(InteractionHand.OFF_HAND);
		if (leftHanded) {
			leftHand = !leftHand;
		}

		return leftHand;
	}

	@Inject(
		method = "submitArmWithItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;II I)V",
			ordinal = 1
		)
	)
	private void transform(
		PlayerRenderState playerState,
		FirstPersonHandsAndItemsRenderState state,
		float partialTicks,
		float xRot,
		InteractionHand hand,
		float attack,
		ItemStack itemStack,
		float inverseArmHeight,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		CallbackInfo callbackInfo
	) {
		NormalOrFoodConfig tabConfig = isEating(playerState, itemStack) ? ConfigFile.configData.foodConfig : ConfigFile.configData.normalConfig;

		if (isLeftHand(playerState, hand)) {
			poseStack.translate(
				tabConfig.xOffset,
				tabConfig.yOffset,
				tabConfig.zOffset
			);

			poseStack.scale(
				tabConfig.xScale,
				tabConfig.yScale,
				tabConfig.zScale
			);

		} else {
			poseStack.translate(
				tabConfig.xOffset * -1,
				tabConfig.yOffset,
				tabConfig.zOffset
			);

			poseStack.scale(
				tabConfig.xScale,
				tabConfig.yScale,
				tabConfig.zScale
			);
		}
	}

	@Inject(method = "submitHandsWithItems", at = @At("HEAD"))
	private void disableItemHeightAnimations(
		float partialTicks,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		PlayerRenderState playerState,
		FirstPersonHandsAndItemsRenderState state,
		CallbackInfo callbackInfo
	) {
		if (ConfigFile.configData.normalConfig.itemHeightAnimations) return;
		
		state.mainHandHeight = 1f;
		state.oldMainHandHeight = 1f;
		state.offHandHeight = 1f;
		state.oldOffHandHeight = 1f;

		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.player == null) {
			return;
		}

		state.mainHandItem = minecraft.player.getMainHandItem();
		state.offHandItem = minecraft.player.getOffhandItem();
	}
}