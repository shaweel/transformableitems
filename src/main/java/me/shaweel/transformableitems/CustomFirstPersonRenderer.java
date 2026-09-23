package me.shaweel.transformableitems;

import com.mojang.blaze3d.platform.GlStateManager;

import me.shaweel.transformableitems.ConfigFile.NormalOrFoodConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;

public class CustomFirstPersonRenderer extends FirstPersonRenderer {
	public CustomFirstPersonRenderer(Minecraft client) {
		super(client);
	}
	
	private boolean isEating(LivingEntity livingEntity) {
		return livingEntity.isHandActive() && livingEntity.getActiveItemStack().getUseAction() == UseAction.EAT;
	}

	@Override
	public void renderItemSide(
		LivingEntity livingEntity,
		ItemStack itemStack,
		TransformType transformType,
		boolean bl
	) {
		NormalOrFoodConfig tabConfig = isEating(livingEntity) ? ConfigFile.configData.foodConfig : ConfigFile.configData.normalConfig;

		GlStateManager.pushMatrix();

		if (transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
			GlStateManager.translatef(
				tabConfig.xOffset,
				tabConfig.yOffset,
				tabConfig.zOffset
			);

			GlStateManager.scalef(
				tabConfig.xScale,
				tabConfig.yScale,
				tabConfig.zScale
			);

		} else if (transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
			GlStateManager.translatef(
				tabConfig.xOffset * -1,
				tabConfig.yOffset,
				tabConfig.zOffset
			);

			GlStateManager.scalef(
				tabConfig.xScale,
				tabConfig.yScale,
				tabConfig.zScale
			);
		}

		super.renderItemSide(livingEntity, itemStack, transformType, bl);

		GlStateManager.popMatrix();
	}

	@Override
	public void tick() {
		super.tick();

		if (ConfigFile.configData.normalConfig.itemHeightAnimations) return;

		this.equippedProgressMainHand = 1f;
		this.equippedProgressOffHand = 1f;
		this.prevEquippedProgressMainHand = 1f;
		this.prevEquippedProgressOffHand = 1f;
		if (Minecraft.getInstance().player == null) return;
		this.itemStackMainHand = Minecraft.getInstance().player.getHeldItemMainhand();
		this.itemStackOffHand = Minecraft.getInstance().player.getHeldItemOffhand();
	}
}
