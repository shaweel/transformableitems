package me.shaweel.transformableitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.transformableitems.ConfigScreen;
import me.shaweel.transformableitems.ModKeybinds;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class GuiOpener {
	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo callbackInfo) {
		Minecraft client = Minecraft.getInstance();

		if (ModKeybinds.OPEN_CONFIG_KEYBIND == null || !ModKeybinds.OPEN_CONFIG_KEYBIND.consumeClick() || client.gui == null || client.gui.screen() != null) return;
		client.gui.setScreen(new ConfigScreen());
	}
}
