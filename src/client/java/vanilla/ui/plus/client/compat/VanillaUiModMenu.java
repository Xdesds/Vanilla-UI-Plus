package vanilla.ui.plus.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import vanilla.ui.plus.client.config.VanillaUiConfigScreen;

public final class VanillaUiModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return VanillaUiConfigScreen::new;
	}
}
