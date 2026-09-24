package me.shaweel.transformableitems;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "transformableitems", version = "1.2", name = "Transformable Items", guiFactory = "me.shaweel.transformableitems.ConfigScreenFactory")
public class TransformableItemsInitializer {
	private static boolean replaced = false;

	@SubscribeEvent
	public void postInit(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || replaced) {
			return;
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		mc.entityRenderer.itemRenderer = new CustomItemRenderer(mc);

		replaced = true;
	}
	
	public TransformableItemsInitializer() {
		ConfigFile.load();
		FMLCommonHandler.instance().bus().register(new ModKeybinds());
		FMLCommonHandler.instance().bus().register(this);
		ModKeybinds.initialize();
	}
}