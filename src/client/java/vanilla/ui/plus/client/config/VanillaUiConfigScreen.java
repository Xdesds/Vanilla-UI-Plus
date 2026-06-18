package vanilla.ui.plus.client.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import vanilla.ui.plus.client.animation.AnimationType;

public final class VanillaUiConfigScreen extends Screen {
	private static final String[] EASINGS = { "ease_in", "ease_out", "ease_in_out", "cubic", "quartic", "exponential" };

	private final Screen parent;
	private VanillaUiConfig draft;

	public VanillaUiConfigScreen(Screen parent) {
		super(Component.literal("VanillaUi+"));
		this.parent = parent;
		this.draft = copy(ConfigManager.get());
	}

	@Override
	protected void init() {
		int center = width / 2;
		int y = 42;
		addRenderableWidget(button(center - 155, y, 150, "Preset: " + draft.preset, ignored -> cyclePreset()));
		addRenderableWidget(button(center + 5, y, 150, "Type: " + draft.animationType.name(), ignored -> cycleAnimationType()));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, "Easing: " + draft.easing, ignored -> cycleEasing()));
		addRenderableWidget(button(center + 5, y, 150, "Speed: " + formatSpeed(), ignored -> cycleSpeed()));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, toggleText("Screens", draft.screenAnimations), ignored -> draft.screenAnimations = !draft.screenAnimations));
		addRenderableWidget(button(center + 5, y, 150, toggleText("Items", draft.itemAnimations), ignored -> draft.itemAnimations = !draft.itemAnimations));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, toggleText("Slot Hover", draft.slotHoverAnimations), ignored -> draft.slotHoverAnimations = !draft.slotHoverAnimations));
		addRenderableWidget(button(center + 5, y, 150, toggleText("HUD", draft.hudAnimations), ignored -> draft.hudAnimations = !draft.hudAnimations));
		y += 24;
		addRenderableWidget(button(center - 155, y, 150, toggleText("Notifications", draft.notificationAnimations), ignored -> draft.notificationAnimations = !draft.notificationAnimations));
		addRenderableWidget(button(center + 5, y, 150, toggleText("Performance", draft.performanceMode), ignored -> draft.performanceMode = !draft.performanceMode));
		y += 36;
		addRenderableWidget(button(center - 155, y, 150, "Done", ignored -> saveAndClose()));
		addRenderableWidget(button(center + 5, y, 150, "Reset", ignored -> resetDefaults()));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 18, 0xFFFFFF);
		graphics.drawCenteredString(font, Component.literal("Subtle client-side animation polish"), width / 2, 30, 0xA0A0A0);
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
		draft = switch (draft.preset) {
			case "vanilla_plus" -> VanillaUiConfig.modernPreset();
			case "modern" -> {
				VanillaUiConfig custom = copy(draft);
				custom.preset = "custom";
				yield custom;
			}
			default -> VanillaUiConfig.vanillaPlusPreset();
		};
	}

	private void cycleAnimationType() {
		AnimationType[] values = AnimationType.values();
		draft.animationType = values[(draft.animationType.ordinal() + 1) % values.length];
		draft.preset = "custom";
	}

	private void cycleEasing() {
		for (int index = 0; index < EASINGS.length; index++) {
			if (EASINGS[index].equals(draft.easing)) {
				draft.easing = EASINGS[(index + 1) % EASINGS.length];
				draft.preset = "custom";
				return;
			}
		}
		draft.easing = EASINGS[0];
	}

	private void cycleSpeed() {
		draft.animationSpeed += 0.25F;
		if (draft.animationSpeed > 2.0F) {
			draft.animationSpeed = 0.5F;
		}
		draft.preset = "custom";
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

	private String formatSpeed() {
		return String.format(java.util.Locale.ROOT, "%.2fx", draft.animationSpeed);
	}

	private static String toggleText(String label, boolean enabled) {
		return label + ": " + (enabled ? "On" : "Off");
	}

	private static VanillaUiConfig copy(VanillaUiConfig source) {
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
		return copy;
	}
}
