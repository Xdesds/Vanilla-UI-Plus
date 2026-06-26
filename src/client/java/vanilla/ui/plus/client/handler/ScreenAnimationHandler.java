package vanilla.ui.plus.client.handler;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.config.ScreenAnimationCategory;
import vanilla.ui.plus.client.config.VanillaUiConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

/**
 * Applies subtle opening motion to vanilla GUI screens.
 */
public final class ScreenAnimationHandler {
	private static final String[] SUPPORTED_SCREEN_MARKERS = {
		"titlescreen",
		"inventoryscreen",
		"creativemodeinventoryscreen",
		"containerscreen",
		"chestscreen",
		"shulkerscreen",
		"craftingscreen",
		"furnacescreen",
		"blastfurnacescreen",
		"smokerscreen",
		"beaconscreen",
		"pause",
		"optionsscreen",
		"controls",
		"soundoptions",
		"video",
		"accessibility",
		"language",
		"packselection",
		"multiplayer",
		"joinmultiplayer",
		"disconnected",
		"selectworld",
		"createworld",
		"advancements",
		"stats",
		"anvilscreen",
		"enchantmentscreen",
		"merchantscreen",
		"recipebook"
	};
	private static final Set<Screen> CLOSING_SCREENS = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
	private static final Set<Screen> FINISHING_CLOSE = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
	private static Screen transformedScreen;

	private ScreenAnimationHandler() {
	}

	public static void onScreenAdded(Screen screen) {
		AnimationManager manager = AnimationManager.getInstance();
		if (isSupported(screen, manager.config())) {
			CLOSING_SCREENS.remove(screen);
			FINISHING_CLOSE.remove(screen);
			TextAnimationHandler.restartScreenText();
			manager.registry().restart(screen, durationMillis(screen, manager.config()), manager.easing());
		}
	}

	public static void onScreenRemoved(Screen screen) {
		AnimationManager.getInstance().registry().remove(screen);
		CLOSING_SCREENS.remove(screen);
		FINISHING_CLOSE.remove(screen);
	}

	public static boolean requestClose(Screen screen) {
		AnimationManager manager = AnimationManager.getInstance();
		if (!isSupported(screen, manager.config()) || FINISHING_CLOSE.contains(screen)) {
			return false;
		}
		if (!CLOSING_SCREENS.contains(screen)) {
			CLOSING_SCREENS.add(screen);
			manager.registry().restart(screen, durationMillis(screen, manager.config()), manager.easing());
		}
		return true;
	}

	public static boolean shouldAnimate(Screen screen) {
		AnimationManager manager = AnimationManager.getInstance();
		VanillaUiConfig config = manager.config();
		if (!isSupported(screen, config)) {
			return false;
		}

		AnimationProfileConfig profile = config.screenProfile(ScreenAnimationCategory.fromScreen(screen));
		AnimationEngine animation = manager.registry().getOrCreate(screen, durationMillis(screen, config), manager.easing());
		boolean closing = CLOSING_SCREENS.contains(screen);
		if (animation.isDone() && !closing) {
			return false;
		}
		return true;
	}

	public static boolean beginForegroundTransform(Screen screen, GuiGraphics graphics) {
		if (transformedScreen == screen || !shouldAnimate(screen)) {
			return false;
		}
		AnimationManager manager = AnimationManager.getInstance();
		VanillaUiConfig config = manager.config();
		AnimationProfileConfig profile = config.screenProfile(ScreenAnimationCategory.fromScreen(screen));
		AnimationEngine animation = manager.registry().getOrCreate(screen, durationMillis(screen, config), manager.easing());
		boolean closing = CLOSING_SCREENS.contains(screen);
		graphics.pose().pushPose();
		float progress = closing ? 1.0F - animation.rawProgress() : animation.rawProgress();
		RenderUtilities.applyProfileTransform(graphics, screen.width, screen.height, profile, progress);
		transformedScreen = screen;
		return true;
	}

	public static void afterRender(Screen screen, GuiGraphics graphics, boolean transformed) {
		if (transformed) {
			transformedScreen = null;
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
		}
		if (CLOSING_SCREENS.contains(screen)) {
			AnimationEngine animation = AnimationManager.getInstance().registry().getOrCreate(
				screen,
				durationMillis(screen, AnimationManager.getInstance().config()),
				AnimationManager.getInstance().easing()
			);
			if (animation.isDone()) {
				FINISHING_CLOSE.add(screen);
				Minecraft.getInstance().setScreen(null);
			}
		}
	}

	public static boolean shouldSkipTransformedBackground(Screen screen) {
		return false;
	}

	private static boolean isSupported(Screen screen, VanillaUiConfig config) {
		if (!config.screenAnimations || screen == null) {
			return false;
		}
		AnimationProfileConfig profile = config.screenProfile(ScreenAnimationCategory.fromScreen(screen));
		if (profile == null || !profile.enabled) {
			return false;
		}
		if (screen instanceof vanilla.ui.plus.client.config.VanillaUiConfigScreen) {
			return true;
		}
		String name = screen.getClass().getName().toLowerCase(Locale.ROOT);
		for (String marker : SUPPORTED_SCREEN_MARKERS) {
			if (name.contains(marker)) {
				return true;
			}
		}
		return name.startsWith("net.minecraft.client.gui.screens.");
	}

	private static long durationMillis(Screen screen, VanillaUiConfig config) {
		AnimationProfileConfig profile = config.screenProfile(ScreenAnimationCategory.fromScreen(screen));
		float speed = Math.max(0.1F, config.animationSpeed * profile.speedMultiplier);
		long duration = profile.durationMillis + profile.delayMillis;
		if (config.performanceMode) {
			duration = Math.min(duration, 140L);
		}
		return Math.max(35L, (long) (duration / speed));
	}
}
