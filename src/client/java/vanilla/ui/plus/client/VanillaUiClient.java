package vanilla.ui.plus.client;

import net.fabricmc.api.ClientModInitializer;
import vanilla.ui.plus.VanillaUi;
import vanilla.ui.plus.client.config.ConfigManager;

public class VanillaUiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		VanillaUi.LOGGER.info("VanillaUi+ client animations loaded.");
	}
}
