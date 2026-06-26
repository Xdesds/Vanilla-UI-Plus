package vanilla.ui.plus.client.handler;

import net.minecraft.client.gui.GuiGraphics;
import vanilla.ui.plus.client.animation.EasingUtilities;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

public final class TextAnimationHandler {
	private static long screenStartNanos = System.nanoTime();
	private static boolean pushed;
	private static int suppressDepth;

	private TextAnimationHandler() {
	}

	public static void restartScreenText() {
		screenStartNanos = System.nanoTime();
	}

	public static void pushSuppress() {
		suppressDepth++;
	}

	public static void popSuppress() {
		if (suppressDepth > 0) {
			suppressDepth--;
		}
	}

	public static boolean beforeText(GuiGraphics graphics, int x, int y) {
		pushed = false;
		AnimationProfileConfig profile = AnimationManager.getInstance().config().textProfile;
		if (suppressDepth > 0 || profile == null || !profile.enabled) {
			return false;
		}
		float duration = Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier));
		float raw = Math.min(1.0F, (System.nanoTime() - screenStartNanos) / 1_000_000.0F / duration);
		if (raw >= 1.0F) {
			return false;
		}

		float progress = EasingUtilities.byName(profile.easing).apply(raw);
		float missing = 1.0F - progress;
		float scale = 1.0F - missing * profile.scale;
		float alpha = 1.0F - missing * profile.opacity;
		graphics.pose().pushPose();
		graphics.pose().translate(x + missing * profile.offsetX, y + missing * profile.offsetY, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-x, -y, 0.0F);
		com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		pushed = true;
		return true;
	}

	public static void afterText(GuiGraphics graphics) {
		if (pushed) {
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
			pushed = false;
		}
	}
}
