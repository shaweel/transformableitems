package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "transformableitems", guiFactory = "me.shaweel.transformableitems.ConfigScreenFactory")
public class TransformableItemsInitializer {
	private static boolean replaced = false;

	@SubscribeEvent
	public void postInit(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || replaced) {
			return;
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		mc.entityRenderer = new CustomEntityRenderer(mc, mc.getResourceManager());
		mc.entityRenderer.itemRenderer = new CustomItemRenderer(mc);

		replaced = true;
	}
	
	public TransformableItemsInitializer() {
		ConfigFile.load();
		MinecraftForge.EVENT_BUS.register(ModKeybinds.class);
		MinecraftForge.EVENT_BUS.register(this);
		ModKeybinds.initialize();
	}
}