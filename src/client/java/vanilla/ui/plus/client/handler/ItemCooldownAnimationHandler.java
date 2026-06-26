package vanilla.ui.plus.client.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

public final class ItemCooldownAnimationHandler {
	private static final Map<String, State> STATES = new HashMap<>();
	private static final int MAX_STATES = 64;

	private ItemCooldownAnimationHandler() {
	}

	public static void beforeCooldown(GuiGraphics graphics, ItemStack stack, int x, int y) {
		if (!AnimationManager.getInstance().config().hudAnimations || stack == null || stack.isEmpty()) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}
		float cooldown = minecraft.player.getCooldowns().getCooldownPercent(stack, 0.0F);
		if (cooldown <= 0.0F) {
			return;
		}
		AnimationProfileConfig profile = AnimationManager.getInstance().config().hotbarProfile;
		if (profile == null || !profile.enabled) {
			return;
		}

		State state = STATES.computeIfAbsent(stack.getItem().toString(), ignored -> new State());
		if (STATES.size() > MAX_STATES) {
			STATES.clear();
		}
		float delta = state.elapsedSeconds();
		if (cooldown > state.lastCooldown + 0.05F) {
			state.pulse = 1.0F;
		}
		state.lastCooldown = cooldown;
		state.pulse = AnimationEngine.damp(state.pulse, cooldown, delta, response(profile));

		float amount = Math.min(1.0F, Math.max(cooldown, state.pulse) * Math.max(0.2F, profile.glowIntensity));
		RenderUtilities.renderSoftGlow(graphics, x, y, 16, 16, amount, 0xFFFFFF);
		RenderUtilities.renderOutlinePulse(graphics, x, y, 16, 16, amount, 0xFFFFFF);
		if (state.pulse > cooldown + 0.05F) {
			RenderUtilities.renderRipple(graphics, x, y, 16, 16, Math.min(1.0F, state.pulse), 0xFFFFFF);
		}
	}

	private static float response(AnimationProfileConfig profile) {
		return 1000.0F / Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier));
	}

	private static final class State {
		private float lastCooldown;
		private float pulse;
		private long lastNanos = System.nanoTime();

		private float elapsedSeconds() {
			long now = System.nanoTime();
			float seconds = Math.min(0.1F, (now - lastNanos) / 1_000_000_000.0F);
			lastNanos = now;
			return seconds;
		}
	}
}
