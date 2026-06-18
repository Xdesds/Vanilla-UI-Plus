package vanilla.ui.plus.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import vanilla.ui.plus.client.animation.AnimationType;
import vanilla.ui.plus.client.animation.EasingUtilities;

/**
 * Small render helpers shared by mixins and handlers.
 */
public final class RenderUtilities {
	private RenderUtilities() {
	}

	public static void applyScreenTransform(GuiGraphics graphics, int width, int height, AnimationType type, float progress) {
		float clamped = EasingUtilities.clamp(progress);
		float centerX = width * 0.5F;
		float centerY = height * 0.5F;
		float offsetY = (1.0F - clamped) * 18.0F;
		float scale = switch (type) {
			case SCALE -> 0.95F + clamped * 0.05F;
			case BOUNCE -> 0.94F + EasingUtilities.BOUNCE.apply(clamped) * 0.06F;
			case FADE_SLIDE -> 0.955F + clamped * 0.045F;
			default -> 1.0F;
		};

		if (type == AnimationType.SLIDE || type == AnimationType.FADE_SLIDE || type == AnimationType.BOUNCE) {
			graphics.pose().translate(0.0F, offsetY, 0.0F);
		}
		if (scale != 1.0F) {
			graphics.pose().translate(centerX, centerY, 0.0F);
			graphics.pose().scale(scale, scale, 1.0F);
			graphics.pose().translate(-centerX, -centerY, 0.0F);
		}
		if (type == AnimationType.FADE || type == AnimationType.FADE_SLIDE || type == AnimationType.BOUNCE) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, clamped);
		}
	}

	public static void applyHudPanelTransform(GuiGraphics graphics, float progress, float offsetY, float scaleAmount) {
		float clamped = EasingUtilities.clamp(progress);
		float scale = 1.0F - (1.0F - clamped) * scaleAmount;
		float centerX = graphics.guiWidth() * 0.5F;
		graphics.pose().translate(0.0F, (1.0F - clamped) * offsetY, 0.0F);
		if (scale != 1.0F) {
			graphics.pose().translate(centerX, 0.0F, 0.0F);
			graphics.pose().scale(scale, scale, 1.0F);
			graphics.pose().translate(-centerX, 0.0F, 0.0F);
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, clamped);
	}

	public static void resetShaderColor() {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static int withAlpha(int color, int alpha) {
		return (alpha << 24) | (color & 0x00FFFFFF);
	}
}
