package vanilla.ui.plus.client.config;

import java.util.LinkedHashMap;
import java.util.Map;

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
	public String animationQuality = "high";
	public String selectedProfileTarget = "screen";
	public String selectedScreenCategory = ScreenAnimationCategory.INVENTORY.key;
	public int selectedLayerIndex = 0;
	public Map<String, AnimationProfileConfig> screenProfiles = new LinkedHashMap<>();
	public AnimationProfileConfig buttonHoverProfile = tunedProfile(AnimationType.SCALE, 120, "cubic", 0.035F, 0.0F);
	public AnimationProfileConfig buttonPressProfile = tunedProfile(AnimationType.PRESS, 95, "back", 0.025F, 0.0F);
	public AnimationProfileConfig slotHoverProfile = tunedProfile(AnimationType.LIFT, 120, "cubic", 0.045F, 1.25F);
	public AnimationProfileConfig slotClickProfile = AnimationProfileConfig.single(AnimationType.BOUNCE, 140, "back");
	public AnimationProfileConfig itemAppearProfile = tunedProfile(AnimationType.POP, 160, "back", 0.04F, 2.25F);
	public AnimationProfileConfig itemMoveProfile = AnimationProfileConfig.single(AnimationType.SCALE_SLIDE, 150, "cubic");
	public AnimationProfileConfig itemDisappearProfile = AnimationProfileConfig.single(AnimationType.FADE_SCALE, 130, "cubic");
	public AnimationProfileConfig hotbarProfile = AnimationProfileConfig.single(AnimationType.BOUNCE, 140, "back");
	public AnimationProfileConfig chatProfile = AnimationProfileConfig.single(AnimationType.FADE_SLIDE, 190, "cubic");
	public AnimationProfileConfig notificationProfile = AnimationProfileConfig.single(AnimationType.FADE_SLIDE, 190, "back");
	public AnimationProfileConfig tooltipProfile = AnimationProfileConfig.single(AnimationType.FADE_SCALE, 110, "cubic");
	public AnimationProfileConfig scrollbarProfile = AnimationProfileConfig.single(AnimationType.SLIDE, 110, "cubic");
	public AnimationProfileConfig textProfile = AnimationProfileConfig.single(AnimationType.FADE, 120, "cubic");

	public static VanillaUiConfig defaults() {
		return new VanillaUiConfig();
	}

	public static VanillaUiConfig vanillaPlusPreset() {
		VanillaUiConfig config = new VanillaUiConfig();
		config.preset = "vanilla_plus";
		config.animationType = AnimationType.FADE_SLIDE;
		config.easing = "cubic";
		config.animationSpeed = 0.95F;
		config.ensureDefaultProfiles();
		return config;
	}

	public static VanillaUiConfig modernPreset() {
		VanillaUiConfig config = new VanillaUiConfig();
		config.preset = "modern";
		config.animationType = AnimationType.SCALE;
		config.easing = "exponential";
		config.animationSpeed = 1.2F;
		config.itemPickupEffects = true;
		config.ensureDefaultProfiles();
		config.buttonHoverProfile.scale = 0.045F;
		config.buttonHoverProfile.glowIntensity = 0.25F;
		config.slotHoverProfile.scale = 0.06F;
		config.slotHoverProfile.glowIntensity = 0.25F;
		config.slotClickProfile.glowIntensity = 0.35F;
		config.scrollbarProfile.glowIntensity = 0.15F;
		config.itemMoveProfile.type = AnimationType.ARC;
		config.itemMoveProfile.offsetY = 9.0F;
		config.itemDisappearProfile.type = AnimationType.FADE_SCALE;
		config.itemDisappearProfile.scale = 0.35F;
		return config;
	}

	public static VanillaUiConfig smoothPreset() {
		VanillaUiConfig config = vanillaPlusPreset();
		config.preset = "smooth";
		config.easing = "sine";
		config.animationSpeed = 0.8F;
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.easing = "sine";
			profile.durationMillis = 240;
		}
		return config;
	}

	public static VanillaUiConfig performancePreset() {
		VanillaUiConfig config = vanillaPlusPreset();
		config.preset = "performance";
		config.performanceMode = true;
		config.animationQuality = "low";
		config.animationSpeed = 1.75F;
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.durationMillis = 100;
			profile.layers.clear();
			profile.layers.add(AnimationLayerConfig.of(AnimationType.FADE));
		}
		config.buttonHoverProfile.enabled = false;
		config.slotHoverProfile.enabled = false;
		config.buttonPressProfile.glowIntensity = 0.0F;
		config.slotClickProfile.glowIntensity = 0.0F;
		config.scrollbarProfile.glowIntensity = 0.0F;
		config.itemMoveProfile.durationMillis = 70;
		config.itemMoveProfile.type = AnimationType.SLIDE;
		config.itemDisappearProfile.enabled = false;
		return config;
	}

	public static VanillaUiConfig minimalPreset() {
		VanillaUiConfig config = vanillaPlusPreset();
		config.preset = "minimal";
		config.animationQuality = "medium";
		config.animationSpeed = 1.35F;
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.type = AnimationType.FADE;
			profile.scale = 0.0F;
			profile.durationMillis = 120;
			profile.layers.clear();
			profile.layers.add(AnimationLayerConfig.of(AnimationType.FADE));
		}
		config.buttonHoverProfile.scale = 0.018F;
		config.slotHoverProfile.scale = 0.025F;
		config.buttonHoverProfile.glowIntensity = 0.0F;
		config.slotHoverProfile.glowIntensity = 0.0F;
		config.scrollbarProfile.glowIntensity = 0.0F;
		config.itemMoveProfile.type = AnimationType.SLIDE;
		config.itemDisappearProfile.type = AnimationType.FADE;
		return config;
	}

	public static VanillaUiConfig fastPreset() {
		VanillaUiConfig config = vanillaPlusPreset();
		config.preset = "fast";
		config.animationQuality = "medium";
		config.animationSpeed = 1.85F;
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.durationMillis = 95;
		}
		config.buttonPressProfile.durationMillis = 65;
		config.slotClickProfile.durationMillis = 75;
		config.itemMoveProfile.durationMillis = 80;
		config.itemDisappearProfile.durationMillis = 70;
		return config;
	}

	public static VanillaUiConfig elegantPreset() {
		VanillaUiConfig config = smoothPreset();
		config.preset = "elegant";
		config.animationQuality = "high";
		config.animationSpeed = 0.9F;
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.type = AnimationType.FADE_SCALE;
			profile.scale = 0.055F;
			profile.easing = "sine";
		}
		config.buttonHoverProfile.scale = 0.025F;
		config.slotHoverProfile.scale = 0.035F;
		return config;
	}

	public static VanillaUiConfig lunarStylePreset() {
		VanillaUiConfig config = modernPreset();
		config.preset = "lunar_style";
		config.animationQuality = "high";
		config.animationSpeed = 1.25F;
		config.hotbarProfile.type = AnimationType.ELASTIC;
		config.buttonHoverProfile.scale = 0.05F;
		config.buttonHoverProfile.glowIntensity = 0.35F;
		config.slotHoverProfile.scale = 0.055F;
		config.slotHoverProfile.glowIntensity = 0.35F;
		config.slotClickProfile.glowIntensity = 0.45F;
		config.scrollbarProfile.glowIntensity = 0.25F;
		config.itemMoveProfile.type = AnimationType.MAGNETIC;
		config.itemMoveProfile.elasticStrength = 1.4F;
		return config;
	}

	public static VanillaUiConfig badlionStylePreset() {
		VanillaUiConfig config = fastPreset();
		config.preset = "badlion_style";
		config.animationQuality = "medium";
		config.hotbarProfile.type = AnimationType.SLIDE;
		config.buttonHoverProfile.scale = 0.03F;
		config.buttonPressProfile.glowIntensity = 0.25F;
		return config;
	}

	public static VanillaUiConfig appleStylePreset() {
		VanillaUiConfig config = elegantPreset();
		config.preset = "apple_style";
		config.animationQuality = "high";
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.easing = "circ";
			profile.durationMillis = 210;
		}
		config.buttonHoverProfile.offsetY = 1.0F;
		config.itemMoveProfile.type = AnimationType.BEZIER;
		config.itemMoveProfile.offsetX = 5.0F;
		config.itemMoveProfile.offsetY = 8.0F;
		return config;
	}

	public static VanillaUiConfig materialStylePreset() {
		VanillaUiConfig config = vanillaPlusPreset();
		config.preset = "material_style";
		config.animationQuality = "high";
		for (AnimationProfileConfig profile : config.screenProfiles.values()) {
			profile.type = AnimationType.SLIDE_BOTTOM;
			profile.offsetY = 24.0F;
			profile.easing = "cubic";
		}
		config.buttonPressProfile.scale = 0.04F;
		config.buttonPressProfile.glowIntensity = 0.3F;
		config.slotClickProfile.scale = 0.05F;
		config.slotClickProfile.glowIntensity = 0.4F;
		config.scrollbarProfile.glowIntensity = 0.2F;
		config.itemMoveProfile.type = AnimationType.INERTIA;
		config.itemMoveProfile.offsetY = 6.0F;
		config.itemDisappearProfile.type = AnimationType.DROP;
		config.itemDisappearProfile.offsetY = 10.0F;
		return config;
	}

	public AnimationProfileConfig screenProfile(ScreenAnimationCategory category) {
		ensureDefaultProfiles();
		return screenProfiles.getOrDefault(category.key, screenProfiles.get(ScreenAnimationCategory.GENERIC.key));
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
		if (animationQuality == null || animationQuality.isBlank()) {
			animationQuality = performanceMode ? "low" : "high";
		}
		animationQuality = switch (animationQuality) {
			case "low", "medium", "high", "ultra" -> animationQuality;
			default -> "high";
		};
		ensureDefaultProfiles();
		sanitizeProfile(buttonHoverProfile, AnimationType.SCALE);
		sanitizeProfile(buttonPressProfile, AnimationType.PRESS);
		sanitizeProfile(slotHoverProfile, AnimationType.LIFT);
		sanitizeProfile(slotClickProfile, AnimationType.BOUNCE);
		sanitizeProfile(itemAppearProfile, AnimationType.POP);
		sanitizeProfile(itemMoveProfile, AnimationType.SCALE_SLIDE);
		sanitizeProfile(itemDisappearProfile, AnimationType.FADE_SCALE);
		sanitizeProfile(hotbarProfile, AnimationType.BOUNCE);
		sanitizeProfile(chatProfile, AnimationType.FADE_SLIDE);
		sanitizeProfile(notificationProfile, AnimationType.FADE_SLIDE);
		sanitizeProfile(tooltipProfile, AnimationType.FADE_SCALE);
		sanitizeProfile(scrollbarProfile, AnimationType.SLIDE);
		sanitizeProfile(textProfile, AnimationType.FADE);
		if (selectedScreenCategory == null || selectedScreenCategory.isBlank()) {
			selectedScreenCategory = ScreenAnimationCategory.INVENTORY.key;
		}
		if (selectedProfileTarget == null || selectedProfileTarget.isBlank()) {
			selectedProfileTarget = "screen";
		}
		selectedProfileTarget = switch (selectedProfileTarget) {
			case "screen", "button_hover", "button_press", "slot_hover", "slot_click", "item_appear", "item_move", "item_disappear", "hotbar", "chat", "notification", "tooltip", "scrollbar", "text" -> selectedProfileTarget;
			default -> "screen";
		};
		selectedLayerIndex = Math.max(0, Math.min(15, selectedLayerIndex));
	}

	public void ensureDefaultProfiles() {
		if (screenProfiles == null) {
			screenProfiles = new LinkedHashMap<>();
		}
		for (ScreenAnimationCategory category : ScreenAnimationCategory.values()) {
			screenProfiles.computeIfAbsent(category.key, ignored -> defaultScreenProfile(category));
		}
		for (AnimationProfileConfig profile : screenProfiles.values()) {
			sanitizeProfile(profile, animationType);
		}
		buttonHoverProfile = ensureProfile(buttonHoverProfile, AnimationType.SCALE);
		buttonPressProfile = ensureProfile(buttonPressProfile, AnimationType.PRESS);
		slotHoverProfile = ensureProfile(slotHoverProfile, AnimationType.LIFT);
		slotClickProfile = ensureProfile(slotClickProfile, AnimationType.BOUNCE);
		itemAppearProfile = ensureProfile(itemAppearProfile, AnimationType.POP);
		itemMoveProfile = ensureProfile(itemMoveProfile, AnimationType.SCALE_SLIDE);
		itemDisappearProfile = ensureProfile(itemDisappearProfile, AnimationType.FADE_SCALE);
		hotbarProfile = ensureProfile(hotbarProfile, AnimationType.BOUNCE);
		chatProfile = ensureProfile(chatProfile, AnimationType.FADE_SLIDE);
		notificationProfile = ensureProfile(notificationProfile, AnimationType.FADE_SLIDE);
		tooltipProfile = ensureProfile(tooltipProfile, AnimationType.FADE_SCALE);
		scrollbarProfile = ensureProfile(scrollbarProfile, AnimationType.SLIDE);
		textProfile = ensureProfile(textProfile, AnimationType.FADE);
	}

	private static AnimationProfileConfig defaultScreenProfile(ScreenAnimationCategory category) {
		AnimationProfileConfig profile = switch (category) {
			case INVENTORY, CREATIVE_INVENTORY -> AnimationProfileConfig.single(AnimationType.ZOOM_IN, 170, "back");
			case CHEST, FURNACE, CRAFTING_TABLE, ANVIL, ENCHANTING, BEACON, SMITHING, VILLAGER_TRADING, CARTOGRAPHY, LOOM, GRINDSTONE, STONECUTTER -> AnimationProfileConfig.single(AnimationType.FADE_SCALE, 170, "cubic");
			case PAUSE_MENU, OPTIONS -> AnimationProfileConfig.single(AnimationType.FADE_SLIDE, 180, "cubic");
			case MULTIPLAYER, SINGLEPLAYER, ADVANCEMENTS -> AnimationProfileConfig.single(AnimationType.SLIDE_BOTTOM, 190, "cubic");
			case CHAT -> AnimationProfileConfig.single(AnimationType.FADE_SLIDE, 150, "cubic");
			case TITLE -> AnimationProfileConfig.single(AnimationType.FADE, 220, "sine");
			default -> AnimationProfileConfig.single(AnimationType.FADE_SLIDE, 180, "cubic");
		};
		profile.scale = category == ScreenAnimationCategory.INVENTORY || category == ScreenAnimationCategory.CREATIVE_INVENTORY ? 0.28F : 0.08F;
		profile.offsetY = 18.0F;
		profile.opacity = 1.0F;
		for (AnimationLayerConfig layer : profile.layers) {
			layer.durationMillis = profile.durationMillis;
			layer.easing = profile.easing;
			layer.scale = profile.scale;
			layer.offsetY = profile.offsetY;
			layer.opacity = profile.opacity;
		}
		return profile;
	}

	private static AnimationProfileConfig ensureProfile(AnimationProfileConfig profile, AnimationType fallbackType) {
		AnimationProfileConfig ensured = profile == null ? AnimationProfileConfig.single(fallbackType, 160, "cubic") : profile;
		ensured.sanitize(fallbackType, "cubic");
		return ensured;
	}

	private static void sanitizeProfile(AnimationProfileConfig profile, AnimationType fallbackType) {
		if (profile != null) {
			profile.sanitize(fallbackType, "cubic");
		}
	}

	private static AnimationProfileConfig tunedProfile(AnimationType type, int durationMillis, String easing, float scale, float offsetY) {
		AnimationProfileConfig profile = AnimationProfileConfig.single(type, durationMillis, easing);
		profile.scale = scale;
		profile.offsetY = offsetY;
		for (AnimationLayerConfig layer : profile.layers) {
			layer.scale = scale;
			layer.offsetY = offsetY;
		}
		return profile;
	}
}
