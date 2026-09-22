package me.shaweel.transformableitems.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import me.shaweel.transformableitems.ConfigFile;
import me.shaweel.transformableitems.ConfigFile.NormalOrFoodConfig;

@Mixin(ItemInHandRenderer.class)
public class TransformableItems {
	@Shadow private float mainHandHeight;
	@Shadow private float offHandHeight;
	@Shadow private float oMainHandHeight;
	@Shadow private float oOffHandHeight;
	@Shadow private ItemStack mainHandItem;
	@Shadow private ItemStack offHandItem;
	
	private boolean isEating(LivingEntity livingEntity) {
		return livingEntity.isUsingItem() && livingEntity.getUseItem().getUseAnimation() == ItemUseAnimation.EAT;
	}

	@Inject(method = "renderItem", at = @At("HEAD"))
	private void transform(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ItemDisplayContext itemDisplayContext,
		boolean bl,
		PoseStack poseStack,
		MultiBufferSource multiBufferSource,
		int i,
		CallbackInfo callbackInfo
	) {
		NormalOrFoodConfig tabConfig = isEating(livingEntity) ? ConfigFile.configData.foodConfig : ConfigFile.configData.normalConfig;

		if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
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

		} else if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
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
		} else {
			return;
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo callbackInfo) {
		if (ConfigFile.configData.normalConfig.itemHeightAnimations) return;
		oMainHandHeight = 1f;
		oOffHandHeight = 1f;
		mainHandHeight = 1f;
		offHandHeight = 1f;

		mainHandItem = Minecraft.getInstance().player.getMainHandItem();
		offHandItem = Minecraft.getInstance().player.getOffhandItem();

		callbackInfo.cancel();
	}
}