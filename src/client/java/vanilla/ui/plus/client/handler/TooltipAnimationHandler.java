package vanilla.ui.plus.client.handler;

import net.minecraft.client.gui.GuiGraphics;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.animation.EasingUtilities;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

public final class TooltipAnimationHandler {
	private static float smoothX;
	private static float smoothY;
	private static float appear;
	private static long lastNanos = System.nanoTime();
	private static boolean initialized;
	private static boolean pushed;

	private TooltipAnimationHandler() {
	}

	public static boolean beforeTooltip(GuiGraphics graphics, int x, int y) {
		pushed = false;
		AnimationProfileConfig profile = AnimationManager.getInstance().config().tooltipProfile;
		if (profile == null || !profile.enabled) {
			return false;
		}

		long now = System.nanoTime();
		float delta = Math.min(0.1F, (now - lastNanos) / 1_000_000_000.0F);
		boolean stale = now - lastNanos > 140_000_000L;
		lastNanos = now;
		if (!initialized || stale) {
			smoothX = x;
			smoothY = y;
			appear = 0.0F;
			initialized = true;
		}

		float response = 1000.0F / Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier));
		smoothX = AnimationEngine.damp(smoothX, x, delta, response);
		smoothY = AnimationEngine.damp(smoothY, y, delta, response);
		appear = AnimationEngine.damp(appear, 1.0F, delta, response);

		float progress = EasingUtilities.byName(profile.easing).apply(appear);
		float missing = 1.0F - progress;
		float scale = 1.0F - missing * profile.scale;
		float translateX = smoothX - x + missing * profile.offsetX;
		float translateY = smoothY - y + missing * profile.offsetY;
		float alpha = 1.0F - missing * profile.opacity;

		graphics.pose().pushPose();
		TextAnimationHandler.pushSuppress();
		graphics.pose().translate(x + translateX, y + translateY, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-x, -y, 0.0F);
		com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		pushed = true;
		return true;
	}

	public static void afterTooltip(GuiGraphics graphics) {
		if (pushed) {
			graphics.pose().popPose();
			TextAnimationHandler.popSuppress();
			RenderUtilities.resetShaderColor();
			pushed = false;
		}
	}
}
