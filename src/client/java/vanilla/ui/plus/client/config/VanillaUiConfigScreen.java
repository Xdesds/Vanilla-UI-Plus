package vanilla.ui.plus.client.config;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import vanilla.ui.plus.client.animation.AnimationType;

public final class VanillaUiConfigScreen extends Screen {
	private static final String[] EASINGS = {
		"linear", "sine", "quad", "cubic", "quart", "quint", "expo", "circ", "back", "elastic", "bounce"
	};
	private static final String[] PRESETS = {
		"vanilla_plus", "modern", "smooth", "minimal", "fast", "performance", "elegant", "lunar_style", "badlion_style", "apple_style", "material_style", "custom"
	};
	private static final String[] QUALITIES = {
		"low", "medium", "high", "ultra"
	};
	private static final ProfileTarget[] TARGETS = ProfileTarget.values();

	private final Screen parent;
	private VanillaUiConfig draft;

	public VanillaUiConfigScreen(Screen parent) {
		super(Component.literal("VanillaUi+"));
		this.parent = parent;
		this.draft = copy(ConfigManager.get());
	}

	@Override
	protected void init() {
		draft.ensureDefaultProfiles();
		AnimationProfileConfig profile = selectedProfile();
		AnimationLayerConfig layer = selectedLayer(profile);
		int center = width / 2;
		int y = 42;

		addRenderableWidget(button(center - 155, y, 150, "Preset: " + shortName(draft.preset), ignored -> cyclePreset()));
		addRenderableWidget(button(center + 5, y, 150, "Quality: " + draft.animationQuality, ignored -> cycleQuality()));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Speed: " + formatSpeed(), ignored -> cycleSpeed()));
		addRenderableWidget(button(center + 5, y, 150, toggleText("Performance", draft.performanceMode), ignored -> togglePerformance()));

		y += 30;
		addRenderableWidget(button(center - 155, y, 150, "Target: " + selectedTarget().label, ignored -> cycleTarget()));
		if (selectedTarget() == ProfileTarget.SCREEN) {
			addRenderableWidget(button(center + 5, y, 150, "Screen: " + shortName(ScreenAnimationCategory.fromKey(draft.selectedScreenCategory).key), ignored -> cycleScreenCategory()));
		} else {
			addRenderableWidget(button(center + 5, y, 150, toggleText("Enabled", profile.enabled), ignored -> toggleProfile(profile)));
		}
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, toggleText("Enabled", profile.enabled), ignored -> toggleProfile(profile)));
		addRenderableWidget(button(center + 5, y, 150, "Profile Ease: " + profile.easing, ignored -> cycleProfileEasing(profile)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Profile Dur: " + profile.durationMillis + "ms", ignored -> cycleProfileDuration(profile)));
		addRenderableWidget(button(center + 5, y, 150, "Profile Scale: " + format(profile.scale), ignored -> cycleProfileScale(profile)));

		y += 30;
		addRenderableWidget(button(center - 155, y, 150, "Layer: " + (selectedLayerIndex(profile) + 1) + "/" + profile.layers.size(), ignored -> cycleLayer(profile)));
		addRenderableWidget(button(center + 5, y, 70, "+ Layer", ignored -> addLayer(profile)));
		addRenderableWidget(button(center + 85, y, 70, "- Layer", ignored -> removeLayer(profile)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, toggleText("Layer", layer.enabled), ignored -> toggleLayer(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Type: " + shortName(layer.type.name()), ignored -> cycleLayerType(profile, layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Ease: " + layer.easing, ignored -> cycleLayerEasing(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Dur: " + layer.durationMillis + "ms", ignored -> cycleLayerDuration(layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Delay: " + layer.delayMillis + "ms", ignored -> cycleLayerDelay(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Strength: " + format(layer.strength), ignored -> cycleLayerStrength(layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Scale: " + format(layer.scale), ignored -> cycleLayerScale(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Opacity: " + format(layer.opacity), ignored -> cycleLayerOpacity(layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Rot: " + format(layer.rotation), ignored -> cycleLayerRotation(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Offset: " + format(layer.offsetX) + "," + format(layer.offsetY), ignored -> cycleLayerOffset(layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Bounce: " + format(layer.bounceStrength), ignored -> cycleLayerBounce(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Elastic: " + format(layer.elasticStrength), ignored -> cycleLayerElastic(layer)));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Blur: " + format(layer.blurAmount), ignored -> cycleLayerBlur(layer)));
		addRenderableWidget(button(center + 5, y, 150, "Glow: " + format(layer.glowIntensity), ignored -> cycleLayerGlow(layer)));

		y += 30;
		addRenderableWidget(button(center - 155, y, 150, "Done", ignored -> saveAndClose()));
		addRenderableWidget(button(center + 5, y, 150, "Reset", ignored -> resetDefaults()));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 18, 0xFFFFFF);
		graphics.drawCenteredString(font, Component.literal("Vanilla-style animation constructor"), width / 2, 30, 0xA0A0A0);
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void onClose() {
		saveAndClose();
	}

	private Button button(int x, int y, int width, String label, Button.OnPress onPress) {
		return Button.builder(Component.literal(label), button -> {
			onPress.onPress(button);
			rebuildWidgets();
		}).bounds(x, y, width, 20).build();
	}

	private void cyclePreset() {
		String next = next(PRESETS, draft.preset);
		draft = switch (next) {
			case "modern" -> VanillaUiConfig.modernPreset();
			case "smooth" -> VanillaUiConfig.smoothPreset();
			case "minimal" -> VanillaUiConfig.minimalPreset();
			case "fast" -> VanillaUiConfig.fastPreset();
			case "performance" -> VanillaUiConfig.performancePreset();
			case "elegant" -> VanillaUiConfig.elegantPreset();
			case "lunar_style" -> VanillaUiConfig.lunarStylePreset();
			case "badlion_style" -> VanillaUiConfig.badlionStylePreset();
			case "apple_style" -> VanillaUiConfig.appleStylePreset();
			case "material_style" -> VanillaUiConfig.materialStylePreset();
			case "custom" -> {
				VanillaUiConfig custom = copy(draft);
				custom.preset = "custom";
				yield custom;
			}
			default -> VanillaUiConfig.vanillaPlusPreset();
		};
	}

	private void cycleQuality() {
		draft.animationQuality = next(QUALITIES, draft.animationQuality);
		draft.performanceMode = "low".equals(draft.animationQuality);
		markCustom();
	}

	private void cycleSpeed() {
		draft.animationSpeed += 0.25F;
		if (draft.animationSpeed > 2.0F) {
			draft.animationSpeed = 0.5F;
		}
		markCustom();
	}

	private void togglePerformance() {
		draft.performanceMode = !draft.performanceMode;
		draft.animationQuality = draft.performanceMode ? "low" : "high";
		markCustom();
	}

	private void cycleTarget() {
		ProfileTarget current = selectedTarget();
		draft.selectedProfileTarget = TARGETS[(current.ordinal() + 1) % TARGETS.length].key;
		draft.selectedLayerIndex = 0;
	}

	private void cycleScreenCategory() {
		ScreenAnimationCategory current = ScreenAnimationCategory.fromKey(draft.selectedScreenCategory);
		ScreenAnimationCategory[] values = ScreenAnimationCategory.values();
		draft.selectedScreenCategory = values[(current.ordinal() + 1) % values.length].key;
		draft.selectedLayerIndex = 0;
	}

	private void toggleProfile(AnimationProfileConfig profile) {
		profile.enabled = !profile.enabled;
		markCustom();
	}

	private void cycleProfileEasing(AnimationProfileConfig profile) {
		profile.easing = next(EASINGS, profile.easing);
		markCustom();
	}

	private void cycleProfileDuration(AnimationProfileConfig profile) {
		profile.durationMillis += 40;
		if (profile.durationMillis > 500) {
			profile.durationMillis = 60;
		}
		markCustom();
	}

	private void cycleProfileScale(AnimationProfileConfig profile) {
		profile.scale = cycleFloat(profile.scale, 0.025F, 0.0F, 0.35F);
		markCustom();
	}

	private void cycleLayer(AnimationProfileConfig profile) {
		draft.selectedLayerIndex = (selectedLayerIndex(profile) + 1) % profile.layers.size();
	}

	private void addLayer(AnimationProfileConfig profile) {
		AnimationLayerConfig layer = AnimationLayerConfig.of(AnimationType.FADE);
		layer.durationMillis = profile.durationMillis;
		layer.easing = profile.easing;
		layer.scale = profile.scale;
		layer.opacity = profile.opacity;
		profile.layers.add(layer);
		draft.selectedLayerIndex = profile.layers.size() - 1;
		markCustom();
	}

	private void removeLayer(AnimationProfileConfig profile) {
		if (profile.layers.size() <= 1) {
			return;
		}
		profile.layers.remove(selectedLayerIndex(profile));
		draft.selectedLayerIndex = Math.max(0, draft.selectedLayerIndex - 1);
		markCustom();
	}

	private void toggleLayer(AnimationLayerConfig layer) {
		layer.enabled = !layer.enabled;
		markCustom();
	}

	private void cycleLayerType(AnimationProfileConfig profile, AnimationLayerConfig layer) {
		AnimationType[] values = AnimationType.values();
		layer.type = values[(layer.type.ordinal() + 1) % values.length];
		profile.type = layer.type;
		markCustom();
	}

	private void cycleLayerEasing(AnimationLayerConfig layer) {
		layer.easing = next(EASINGS, layer.easing);
		markCustom();
	}

	private void cycleLayerDuration(AnimationLayerConfig layer) {
		layer.durationMillis += 40;
		if (layer.durationMillis > 500) {
			layer.durationMillis = 40;
		}
		markCustom();
	}

	private void cycleLayerDelay(AnimationLayerConfig layer) {
		layer.delayMillis += 25;
		if (layer.delayMillis > 300) {
			layer.delayMillis = 0;
		}
		markCustom();
	}

	private void cycleLayerStrength(AnimationLayerConfig layer) {
		layer.strength = cycleFloat(layer.strength, 0.25F, 0.25F, 3.0F);
		markCustom();
	}

	private void cycleLayerScale(AnimationLayerConfig layer) {
		layer.scale = cycleFloat(layer.scale, 0.025F, 0.0F, 0.35F);
		markCustom();
	}

	private void cycleLayerOpacity(AnimationLayerConfig layer) {
		layer.opacity = cycleFloat(layer.opacity, 0.1F, 0.1F, 1.0F);
		markCustom();
	}

	private void cycleLayerRotation(AnimationLayerConfig layer) {
		layer.rotation += 15.0F;
		if (layer.rotation > 90.0F) {
			layer.rotation = -90.0F;
		}
		markCustom();
	}

	private void cycleLayerOffset(AnimationLayerConfig layer) {
		if (layer.offsetX == 0.0F && layer.offsetY == 0.0F) {
			layer.offsetY = 18.0F;
		} else if (layer.offsetY > 0.0F) {
			layer.offsetY = -18.0F;
		} else if (layer.offsetY < 0.0F) {
			layer.offsetY = 0.0F;
			layer.offsetX = 24.0F;
		} else if (layer.offsetX > 0.0F) {
			layer.offsetX = -24.0F;
		} else {
			layer.offsetX = 0.0F;
			layer.offsetY = 0.0F;
		}
		markCustom();
	}

	private void cycleLayerBounce(AnimationLayerConfig layer) {
		layer.bounceStrength = cycleFloat(layer.bounceStrength, 0.25F, 0.0F, 3.0F);
		markCustom();
	}

	private void cycleLayerElastic(AnimationLayerConfig layer) {
		layer.elasticStrength = cycleFloat(layer.elasticStrength, 0.25F, 0.0F, 3.0F);
		markCustom();
	}

	private void cycleLayerBlur(AnimationLayerConfig layer) {
		layer.blurAmount = cycleFloat(layer.blurAmount, 1.0F, 0.0F, 12.0F);
		markCustom();
	}

	private void cycleLayerGlow(AnimationLayerConfig layer) {
		layer.glowIntensity = cycleFloat(layer.glowIntensity, 0.1F, 0.0F, 1.0F);
		markCustom();
	}

	private void resetDefaults() {
		draft = VanillaUiConfig.vanillaPlusPreset();
	}

	private void saveAndClose() {
		ConfigManager.set(draft);
		if (minecraft != null) {
			minecraft.setScreen(parent);
		}
	}

	private AnimationProfileConfig selectedProfile() {
		return switch (selectedTarget()) {
			case SCREEN -> draft.screenProfile(ScreenAnimationCategory.fromKey(draft.selectedScreenCategory));
			case BUTTON_HOVER -> draft.buttonHoverProfile;
			case BUTTON_PRESS -> draft.buttonPressProfile;
			case SLOT_HOVER -> draft.slotHoverProfile;
			case SLOT_CLICK -> draft.slotClickProfile;
			case ITEM_APPEAR -> draft.itemAppearProfile;
			case ITEM_MOVE -> draft.itemMoveProfile;
			case ITEM_DISAPPEAR -> draft.itemDisappearProfile;
			case HOTBAR -> draft.hotbarProfile;
			case CHAT -> draft.chatProfile;
			case NOTIFICATION -> draft.notificationProfile;
			case TOOLTIP -> draft.tooltipProfile;
			case SCROLLBAR -> draft.scrollbarProfile;
			case TEXT -> draft.textProfile;
		};
	}

	private AnimationLayerConfig selectedLayer(AnimationProfileConfig profile) {
		if (profile.layers.isEmpty()) {
			profile.layers.add(AnimationLayerConfig.of(profile.type));
		}
		return profile.layers.get(selectedLayerIndex(profile));
	}

	private int selectedLayerIndex(AnimationProfileConfig profile) {
		if (profile.layers.isEmpty()) {
			return 0;
		}
		draft.selectedLayerIndex = Math.max(0, Math.min(draft.selectedLayerIndex, profile.layers.size() - 1));
		return draft.selectedLayerIndex;
	}

	private ProfileTarget selectedTarget() {
		return ProfileTarget.fromKey(draft.selectedProfileTarget);
	}

	private void markCustom() {
		if (!"custom".equals(draft.preset)) {
			draft.preset = "custom";
		}
	}

	private String formatSpeed() {
		return String.format(Locale.ROOT, "%.2fx", draft.animationSpeed);
	}

	private static float cycleFloat(float value, float step, float min, float max) {
		float next = value + step;
		return next > max + 0.0001F ? min : next;
	}

	private static String format(float value) {
		return String.format(Locale.ROOT, "%.2f", value);
	}

	private static String toggleText(String label, boolean enabled) {
		return label + ": " + (enabled ? "On" : "Off");
	}

	private static String next(String[] values, String current) {
		for (int index = 0; index < values.length; index++) {
			if (values[index].equals(current)) {
				return values[(index + 1) % values.length];
			}
		}
		return values[0];
	}

	private static String shortName(String value) {
		return value.toLowerCase(Locale.ROOT).replace('_', ' ');
	}

	private static VanillaUiConfig copy(VanillaUiConfig source) {
		source.ensureDefaultProfiles();
		VanillaUiConfig copy = new VanillaUiConfig();
		copy.preset = source.preset;
		copy.animationType = source.animationType;
		copy.easing = source.easing;
		copy.animationSpeed = source.animationSpeed;
		copy.screenAnimations = source.screenAnimations;
		copy.itemAnimations = source.itemAnimations;
		copy.slotHoverAnimations = source.slotHoverAnimations;
		copy.itemPickupEffects = source.itemPickupEffects;
		copy.hudAnimations = source.hudAnimations;
		copy.notificationAnimations = source.notificationAnimations;
		copy.performanceMode = source.performanceMode;
		copy.animationQuality = source.animationQuality;
		copy.selectedProfileTarget = source.selectedProfileTarget;
		copy.selectedScreenCategory = source.selectedScreenCategory;
		copy.selectedLayerIndex = source.selectedLayerIndex;
		copy.screenProfiles = new LinkedHashMap<>();
		for (Map.Entry<String, AnimationProfileConfig> entry : source.screenProfiles.entrySet()) {
			copy.screenProfiles.put(entry.getKey(), entry.getValue().copy());
		}
		copy.buttonHoverProfile = source.buttonHoverProfile.copy();
		copy.buttonPressProfile = source.buttonPressProfile.copy();
		copy.slotHoverProfile = source.slotHoverProfile.copy();
		copy.slotClickProfile = source.slotClickProfile.copy();
		copy.itemAppearProfile = source.itemAppearProfile.copy();
		copy.itemMoveProfile = source.itemMoveProfile.copy();
		copy.itemDisappearProfile = source.itemDisappearProfile.copy();
		copy.hotbarProfile = source.hotbarProfile.copy();
		copy.chatProfile = source.chatProfile.copy();
		copy.notificationProfile = source.notificationProfile.copy();
		copy.tooltipProfile = source.tooltipProfile.copy();
		copy.scrollbarProfile = source.scrollbarProfile.copy();
		copy.textProfile = source.textProfile.copy();
		return copy;
	}

	private enum ProfileTarget {
		SCREEN("screen", "Screen"),
		BUTTON_HOVER("button_hover", "Button Hover"),
		BUTTON_PRESS("button_press", "Button Press"),
		SLOT_HOVER("slot_hover", "Slot Hover"),
		SLOT_CLICK("slot_click", "Slot Click"),
		ITEM_APPEAR("item_appear", "Item Appear"),
		ITEM_MOVE("item_move", "Item Move"),
		ITEM_DISAPPEAR("item_disappear", "Item Hide"),
		HOTBAR("hotbar", "Hotbar"),
		CHAT("chat", "Chat"),
		NOTIFICATION("notification", "Notification"),
		TOOLTIP("tooltip", "Tooltip"),
		SCROLLBAR("scrollbar", "Scrollbar"),
		TEXT("text", "Text");

		private final String key;
		private final String label;

		ProfileTarget(String key, String label) {
			this.key = key;
			this.label = label;
		}

		private static ProfileTarget fromKey(String key) {
			for (ProfileTarget target : values()) {
				if (target.key.equals(key)) {
					return target;
				}
			}
			return SCREEN;
		}
	}
}
