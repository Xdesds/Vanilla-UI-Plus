package vanilla.ui.plus.client.config;

import vanilla.ui.plus.client.animation.AnimationType;

/**
 * Serializable user configuration for VanillaUi+.
 */
public final class VanillaUiConfig {
	public String preset = "vanilla_plus";
	public AnimationType animationType = AnimationType.FADE_SLIDE;
	public String easing = "cubic";
	public float animationSpeed = 1.0F;
	public boolean screenAnimations = true;
	public boolean itemAnimations = true;
	public boolean slotHoverAnimations = true;
	public boolean itemPickupEffects = true;
	public boolean hudAnimations = true;
	public boolean notificationAnimations = true;
	public boolean performanceMode = false;

	public static VanillaUiConfig defaults() {
		return new VanillaUiConfig();
	}

	public static VanillaUiConfig vanillaPlusPreset() {
		VanillaUiConfig config = new VanillaUiConfig();
		config.preset = "vanilla_plus";
		config.animationType = AnimationType.FADE_SLIDE;
		config.easing = "cubic";
		config.animationSpeed = 0.95F;
		return config;
	}

	public static VanillaUiConfig modernPreset() {
		VanillaUiConfig config = new VanillaUiConfig();
		config.preset = "modern";
		config.animationType = AnimationType.SCALE;
		config.easing = "exponential";
		config.animationSpeed = 1.2F;
		config.itemPickupEffects = true;
		return config;
	}

	public void sanitize() {
		if (animationType == null) {
			animationType = AnimationType.FADE_SLIDE;
		}
		if (easing == null || easing.isBlank()) {
			easing = "cubic";
		}
		animationSpeed = Math.max(0.25F, Math.min(3.0F, animationSpeed));
		if (preset == null || preset.isBlank()) {
			preset = "custom";
		}
	}
}
