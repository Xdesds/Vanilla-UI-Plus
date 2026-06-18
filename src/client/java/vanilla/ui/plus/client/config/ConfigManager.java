package vanilla.ui.plus.client.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import vanilla.ui.plus.VanillaUi;
import vanilla.ui.plus.client.animation.AnimationManager;

/**
 * Loads and saves the client configuration without touching the render path.
 */
public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("vanillaui-plus.json");
	private static VanillaUiConfig config = VanillaUiConfig.defaults();

	private ConfigManager() {
	}

	public static void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				VanillaUiConfig loaded = GSON.fromJson(reader, VanillaUiConfig.class);
				config = loaded == null ? VanillaUiConfig.defaults() : loaded;
			} catch (IOException | RuntimeException exception) {
				VanillaUi.LOGGER.warn("Failed to read VanillaUi+ config, using defaults.", exception);
				config = VanillaUiConfig.defaults();
			}
		}

		config.sanitize();
		AnimationManager.getInstance().setConfig(config);
		save();
	}

	public static VanillaUiConfig get() {
		return config;
	}

	public static void set(VanillaUiConfig nextConfig) {
		config = nextConfig;
		config.sanitize();
		AnimationManager.getInstance().setConfig(config);
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException exception) {
			VanillaUi.LOGGER.warn("Failed to save VanillaUi+ config.", exception);
		}
	}
}
