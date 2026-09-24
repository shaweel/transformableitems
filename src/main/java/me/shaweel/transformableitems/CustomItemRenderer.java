package me.shaweel.transformableitems;

import me.shaweel.transformableitems.ConfigFile.NormalOrFoodConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class CustomItemRenderer extends ItemRenderer {
	public CustomItemRenderer(Minecraft client) {
		super(client);
	}
	
	private boolean isEating(EntityLivingBase livingEntity) {
		if (!(livingEntity instanceof EntityPlayer)) {
			return false;
		} 

		EntityPlayer entityPlayer = (EntityPlayer) livingEntity;

		return entityPlayer.isUsingItem() && entityPlayer.getHeldItem().getItemUseAction() == EnumAction.EAT;
	}

	@Override
	public void renderItem(
		EntityLivingBase livingEntity,
		ItemStack itemStack,
		TransformType transformType
	) {
		NormalOrFoodConfig tabConfig = isEating(livingEntity) ? ConfigFile.configData.foodConfig : ConfigFile.configData.normalConfig;

		GlStateManager.pushMatrix();

		if (transformType == TransformType.FIRST_PERSON) {
			GlStateManager.translate(
				tabConfig.xOffset * -2.5f,
				tabConfig.yOffset * 2.5f,
				tabConfig.zOffset * 2.5f
			);

			GlStateManager.scale(
				tabConfig.xScale,
				tabConfig.yScale,
				tabConfig.zScale
			);

		}

		super.renderItem(livingEntity, itemStack, transformType);

		GlStateManager.popMatrix();
	}

	@Override
	public void updateEquippedItem() {
		super.updateEquippedItem();

		if (ConfigFile.configData.normalConfig.itemHeightAnimations) return;

		this.equippedProgress = 1f;
		this.prevEquippedProgress = 1f;
		if (Minecraft.getMinecraft().thePlayer == null) return;
		this.itemToRender = Minecraft.getMinecraft().thePlayer.getHeldItem();
	}
}
