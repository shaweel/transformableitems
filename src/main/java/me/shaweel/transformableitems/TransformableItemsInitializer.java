package me.shaweel.transformableitems;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("transformableitems")
public class TransformableItemsInitializer {
	private static boolean replaced = false;

	public static void postInit(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || replaced) {
			return;
		}
		
		Minecraft mc = Minecraft.getInstance();
		
		mc.gameRenderer = new CustomGameRenderer(mc, mc.getResourceManager(), mc.getRenderTypeBuffers());
		mc.gameRenderer.itemRenderer = new CustomFirstPersonRenderer(mc);

		replaced = true;
	}
	
	public TransformableItemsInitializer() {
		ConfigFile.load();
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> new ConfigScreen());
		MinecraftForge.EVENT_BUS.register(ModKeybinds.class);
		MinecraftForge.EVENT_BUS.addListener(TransformableItemsInitializer::postInit);
		ModKeybinds.initialize();
	}
}