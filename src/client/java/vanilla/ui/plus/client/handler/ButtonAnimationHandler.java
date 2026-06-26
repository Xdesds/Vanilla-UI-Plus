package vanilla.ui.plus.client.handler;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

/**
 * Adds a small hover lift to vanilla buttons without changing their layout box.
 */
public final class ButtonAnimationHandler {
	private static final Map<AbstractWidget, State> STATES = new IdentityHashMap<>();

	private ButtonAnimationHandler() {
	}

	public static boolean beforeButton(AbstractWidget widget, GuiGraphics graphics) {
		AnimationProfileConfig profile = AnimationManager.getInstance().config().buttonHoverProfile;
		AnimationProfileConfig pressProfile = AnimationManager.getInstance().config().buttonPressProfile;
		if (!AnimationManager.getInstance().config().screenAnimations || widget == null) {
			return false;
		}

		State state = STATES.computeIfAbsent(widget, ignored -> new State());
		float delta = state.elapsedSeconds();
		float hoverTarget = widget.active && profile != null && profile.enabled && widget.isHoveredOrFocused() ? 1.0F : 0.0F;
		state.hover = AnimationEngine.damp(state.hover, hoverTarget, delta, response(profile, AnimationManager.getInstance().hudResponse()));
		state.press = AnimationEngine.damp(state.press, 0.0F, delta, response(pressProfile, 18.0F));
		state.disabled = AnimationEngine.damp(state.disabled, widget.active ? 0.0F : 1.0F, delta, 10.0F);

		float hoverScale = profile == null ? 0.0F : profile.scale * profile.speedMultiplier;
		float pressScale = pressProfile == null ? 0.0F : pressProfile.scale * pressProfile.speedMultiplier;
		float scale = 1.0F + state.hover * hoverScale - state.press * pressScale;
		float translateY = -(profile == null ? 0.0F : state.hover * profile.offsetY) + (pressProfile == null ? 0.0F : state.press * pressProfile.offsetY);
		float alpha = 1.0F - state.disabled * 0.35F;
		renderButtonEffects(graphics, widget, profile, pressProfile, state);
		if (Math.abs(scale - 1.0F) <= 0.001F && Math.abs(translateY) <= 0.001F && state.disabled <= 0.001F) {
			return false;
		}

		float centerX = widget.getX() + widget.getWidth() * 0.5F;
		float centerY = widget.getY() + widget.getHeight() * 0.5F;
		graphics.pose().pushPose();
		graphics.pose().translate(centerX, centerY + translateY, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-centerX, -centerY, 0.0F);
		com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		return true;
	}

	public static void afterButton(GuiGraphics graphics, boolean transformed) {
		if (transformed) {
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
		}
	}

	public static void onPress(AbstractWidget widget) {
		AnimationProfileConfig profile = AnimationManager.getInstance().config().buttonPressProfile;
		if (widget != null && profile != null && profile.enabled) {
			STATES.computeIfAbsent(widget, ignored -> new State()).press = 1.0F;
		}
	}

	private static final class State {
		private float hover;
		private float press;
		private float disabled;
		private long lastNanos = System.nanoTime();

		private float elapsedSeconds() {
			long now = System.nanoTime();
			float elapsed = (now - lastNanos) / 1_000_000_000.0F;
			lastNanos = now;
			return elapsed;
		}
	}

	private static float response(AnimationProfileConfig profile, float fallback) {
		if (profile == null) {
			return fallback;
		}
		return 1000.0F / Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier));
	}

	private static void renderButtonEffects(GuiGraphics graphics, AbstractWidget widget, AnimationProfileConfig hoverProfile, AnimationProfileConfig pressProfile, State state) {
		int color = 0xFFFFFF;
		if (hoverProfile != null && hoverProfile.glowIntensity > 0.0F) {
			float amount = state.hover * hoverProfile.glowIntensity;
			RenderUtilities.renderSoftGlow(graphics, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), amount, color);
			RenderUtilities.renderOutlinePulse(graphics, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), amount, color);
		}
		if (pressProfile != null && pressProfile.glowIntensity > 0.0F) {
			RenderUtilities.renderRipple(graphics, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), state.press * pressProfile.glowIntensity, color);
		}
	}
}
