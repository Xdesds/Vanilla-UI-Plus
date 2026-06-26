package vanilla.ui.plus.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import vanilla.ui.plus.VanillaUi;
import vanilla.ui.plus.client.config.ConfigManager;
import vanilla.ui.plus.client.config.VanillaUiConfigScreen;

public class VanillaUiClient implements ClientModInitializer {
	private static KeyMapping configKey;

	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.vanillaui.open_config",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			"key.categories.vanillaui"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (configKey.consumeClick()) {
				if (!(client.screen instanceof VanillaUiConfigScreen)) {
					client.setScreen(new VanillaUiConfigScreen(client.screen));
				}
			}
		});
		VanillaUi.LOGGER.info("VanillaUi+ client animations loaded.");
	}
}
