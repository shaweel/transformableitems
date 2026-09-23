package me.shaweel.transformableitems;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@Mod("transformableitems")
public class TransformableItemsInitializer {
	private static class PatchedModInfo extends ModInfo {
		public PatchedModInfo(ModInfo modInfo) {
			super(modInfo.getOwningFile(), modInfo.getModConfig());
		}
		
		@Override
		public boolean hasConfigUI() {
			return true;
		}
	}
	
	private static boolean replaced = false;

	public static void postInit(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || replaced) {
			return;
		}
		
		Minecraft mc = Minecraft.getInstance();
		
		mc.gameRenderer = new CustomGameRenderer(mc, mc.getResourceManager());
		mc.gameRenderer.itemRenderer = new CustomFirstPersonRenderer(mc);

		replaced = true;
	}
	
	public TransformableItemsInitializer() {
		ConfigFile.load();

		try {
			ModList modList = ModList.get();

			Field sortedListField = ModList.class.getDeclaredField("sortedList");
			sortedListField.setAccessible(true);

			@SuppressWarnings("unchecked")
			List<ModInfo> sortedList = (List<ModInfo>) sortedListField.get(modList);

			ModInfo modInfo = sortedList.stream().filter(info -> info.getModId().equals("transformableitems")).findFirst().get();

			sortedList.set(sortedList.indexOf(modInfo), new PatchedModInfo(modInfo));
			sortedListField.set(modList, sortedList);
		} catch (Exception e) {
			System.err.println("Failed to load config:");
			e.printStackTrace();
		}
		
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> new ConfigScreen());
		MinecraftForge.EVENT_BUS.register(ModKeybinds.class);
		MinecraftForge.EVENT_BUS.addListener(TransformableItemsInitializer::postInit);
		ModKeybinds.initialize();
	}
}