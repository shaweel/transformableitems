package me.shaweel.transformableitems;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "transformableitems", version = "1.2", name = "Transformable Items", guiFactory = "me.shaweel.transformableitems.ConfigScreenFactory")
public class TransformableItemsInitializer {
	private static boolean replaced = false;

	@SubscribeEvent
	public void postInit(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || replaced) {
			return;
		}
				
		try {
			Field itemRenderer = EntityRenderer.class.getDeclaredField("field_78516_c");
			itemRenderer.setAccessible(true);
			itemRenderer.set(Minecraft.getMinecraft().entityRenderer, new CustomItemRenderer(Minecraft.getMinecraft()));
		} catch (Exception e) {
			System.out.println("failed to replace itemrenderer");
			e.printStackTrace();
		}
		
		replaced = true;
	}
	
	public TransformableItemsInitializer() {
		ConfigFile.load();
		FMLCommonHandler.instance().bus().register(new ModKeybinds());
		FMLCommonHandler.instance().bus().register(this);
		ModKeybinds.initialize();
	}
}