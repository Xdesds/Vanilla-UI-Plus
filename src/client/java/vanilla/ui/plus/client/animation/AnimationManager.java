package vanilla.ui.plus.client.animation;

import vanilla.ui.plus.client.config.VanillaUiConfig;

/**
 * Coordinates configuration and reusable animation state.
 */
public final class AnimationManager {
	private static final AnimationManager INSTANCE = new AnimationManager();

	private final AnimationRegistry registry = new AnimationRegistry();
	private VanillaUiConfig config = VanillaUiConfig.defaults();

	private AnimationManager() {
	}

	public static AnimationManager getInstance() {
		return INSTANCE;
	}

	public AnimationRegistry registry() {
		return registry;
	}

	public VanillaUiConfig config() {
		return config;
	}

	public void setConfig(VanillaUiConfig config) {
		this.config = config;
	}

	public long screenDurationMillis() {
		float speed = Math.max(0.1F, config.animationSpeed);
		long base = config.performanceMode ? 140L : 220L;
		return Math.max(45L, (long) (base / speed));
	}

	public EasingFunction easing() {
		String easingName = config.animationType == AnimationType.BOUNCE ? "bounce" : config.easing;
		return EasingUtilities.byName(easingName);
	}

	public float hudResponse() {
		return config.performanceMode ? 10.0F : 14.0F * Math.max(0.1F, config.animationSpeed);
	}
}
