package vanilla.ui.plus.client.handler;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

public final class ScrollbarAnimationHandler {
	private static final Map<AbstractScrollArea, State> STATES = new IdentityHashMap<>();

	private ScrollbarAnimationHandler() {
	}

	public static boolean beforeScrollbar(AbstractScrollArea area, GuiGraphics graphics) {
		AnimationProfileConfig profile = AnimationManager.getInstance().config().scrollbarProfile;
		if (area == null || profile == null || !profile.enabled || area.maxScrollAmount() <= 0) {
			return false;
		}

		State state = STATES.computeIfAbsent(area, ignored -> new State());
		float delta = state.elapsedSeconds();
		double target = area.scrollAmount();
		if (!state.initialized) {
			state.smoothAmount = target;
			state.initialized = true;
		}

		float response = response(profile);
		state.velocity = AnimationEngine.damp(state.velocity, (float) (target - state.smoothAmount), delta, response * 0.7F);
		state.smoothAmount = AnimationEngine.damp((float) state.smoothAmount, (float) target, delta, response);

		double max = Math.max(1.0D, area.maxScrollAmount());
		float track = Math.max(1.0F, area.getHeight() - 8.0F);
		float translateY = (float) ((state.smoothAmount - target) / max) * track;
		float glow = Math.min(1.0F, Math.abs(state.velocity) * profile.glowIntensity * 0.08F);
		if (Math.abs(translateY) <= 0.01F && glow <= 0.001F) {
			return false;
		}

		graphics.pose().pushPose();
		if (glow > 0.0F) {
			RenderUtilities.renderSoftGlow(graphics, area.getRight() - 8, area.getY(), 8, area.getHeight(), glow, 0xFFFFFF);
		}
		graphics.pose().translate(0.0F, translateY, 0.0F);
		state.pushed = true;
		return true;
	}

	public static void afterScrollbar(AbstractScrollArea area, GuiGraphics graphics) {
		State state = area == null ? null : STATES.get(area);
		if (state != null && state.pushed) {
			graphics.pose().popPose();
			state.pushed = false;
		}
	}

	private static float response(AnimationProfileConfig profile) {
		float friction = Math.max(0.25F, profile.elasticStrength);
		return (1000.0F / Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier))) * friction;
	}

	private static final class State {
		private boolean initialized;
		private boolean pushed;
		private double smoothAmount;
		private float velocity;
		private long lastNanos = System.nanoTime();

		private float elapsedSeconds() {
			long now = System.nanoTime();
			float seconds = Math.min(0.1F, (now - lastNanos) / 1_000_000_000.0F);
			lastNanos = now;
			return seconds;
		}
	}
}
