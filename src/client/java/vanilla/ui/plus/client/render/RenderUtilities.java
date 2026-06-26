package vanilla.ui.plus.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import vanilla.ui.plus.client.animation.AnimationType;
import vanilla.ui.plus.client.animation.EasingUtilities;
import vanilla.ui.plus.client.config.AnimationLayerConfig;
import vanilla.ui.plus.client.config.AnimationProfileConfig;

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

	public static void applyProfileTransform(GuiGraphics graphics, int width, int height, AnimationProfileConfig profile, float rawProgress) {
		if (profile == null || !profile.enabled) {
			return;
		}

		float centerX = width * 0.5F;
		float centerY = height * 0.5F;
		float alpha = 1.0F;
		for (AnimationLayerConfig layer : profile.layers) {
			if (layer == null || !layer.enabled || layer.type == AnimationType.NONE) {
				continue;
			}
			float progress = layerProgress(rawProgress, layer, profile);
			float missing = 1.0F - progress;
			float scale = layerScale(layer, profile, missing);
			float rotation = layerRotation(layer, profile, missing);
			float translateX = layerTranslateX(layer, profile, missing);
			float translateY = layerTranslateY(layer, profile, missing);

			graphics.pose().translate(translateX, translateY, 0.0F);
			if (rotation != 0.0F || scale != 1.0F) {
				graphics.pose().translate(centerX, centerY, 0.0F);
				if (rotation != 0.0F) {
					graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotation));
				}
				if (scale != 1.0F) {
					graphics.pose().scale(scale, scale, 1.0F);
				}
				graphics.pose().translate(-centerX, -centerY, 0.0F);
			}
			if (usesAlpha(layer.type)) {
				alpha = Math.min(alpha, Math.max(0.0F, 1.0F - missing * layer.opacity));
			}
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
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

	public static void applyInventoryOpenTransform(GuiGraphics graphics, int width, int height, float progress) {
		float clamped = EasingUtilities.clamp(progress);
		float eased = EasingUtilities.BOUNCE.apply(clamped);
		float scale = 0.72F + eased * 0.28F;
		float centerX = width * 0.5F;
		float centerY = height * 0.5F;

		graphics.pose().translate(centerX, centerY, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-centerX, -centerY, 0.0F);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.max(0.2F, clamped));
	}

	private static float layerProgress(float rawProgress, AnimationLayerConfig layer, AnimationProfileConfig profile) {
		float duration = Math.max(1.0F, layer.durationMillis / Math.max(0.1F, profile.speedMultiplier));
		float elapsed = rawProgress * Math.max(1, profile.durationMillis) - layer.delayMillis;
		float normalized = Mth.clamp(elapsed / duration, 0.0F, 1.0F);
		return EasingUtilities.byName(layer.easing).apply(normalized);
	}

	private static float layerScale(AnimationLayerConfig layer, AnimationProfileConfig profile, float missing) {
		float amount = layer.scale > 0.0F ? layer.scale : profile.scale;
		return switch (layer.type) {
			case SCALE, FADE_SCALE, SCALE_SLIDE, ZOOM_IN, POP -> 1.0F - missing * amount * layer.strength;
			case ZOOM_OUT -> 1.0F + missing * amount * layer.strength;
			case BOUNCE, ELASTIC, OVERSHOOT -> 1.0F - missing * amount * layer.bounceStrength;
			case FLIP -> Math.max(0.05F, 1.0F - missing);
			default -> 1.0F;
		};
	}

	private static float layerRotation(AnimationLayerConfig layer, AnimationProfileConfig profile, float missing) {
		float amount = layer.rotation != 0.0F ? layer.rotation : profile.rotation;
		return switch (layer.type) {
			case ROTATE, SPIN -> missing * amount * layer.strength;
			case SWING -> (float) Math.sin(missing * Math.PI) * amount * layer.strength;
			case FLIP -> missing * 90.0F * layer.strength;
			default -> 0.0F;
		};
	}

	private static float layerTranslateX(AnimationLayerConfig layer, AnimationProfileConfig profile, float missing) {
		float offset = layer.offsetX != 0.0F ? layer.offsetX : profile.offsetX;
		return switch (layer.type) {
			case SLIDE_LEFT -> -Math.max(Math.abs(offset), 24.0F) * missing * layer.strength;
			case SLIDE_RIGHT -> Math.max(Math.abs(offset), 24.0F) * missing * layer.strength;
			case SLIDE, FADE_SLIDE, SCALE_SLIDE -> offset * missing * layer.strength;
			default -> 0.0F;
		};
	}

	private static float layerTranslateY(AnimationLayerConfig layer, AnimationProfileConfig profile, float missing) {
		float offset = layer.offsetY != 0.0F ? layer.offsetY : profile.offsetY;
		return switch (layer.type) {
			case SLIDE_TOP, LIFT -> -Math.max(Math.abs(offset), 18.0F) * missing * layer.strength;
			case SLIDE_BOTTOM, DROP -> Math.max(Math.abs(offset), 18.0F) * missing * layer.strength;
			case SLIDE, FADE_SLIDE, SCALE_SLIDE, FLOAT -> offset * missing * layer.strength;
			default -> 0.0F;
		};
	}

	private static boolean usesAlpha(AnimationType type) {
		return type == AnimationType.FADE
			|| type == AnimationType.FADE_SLIDE
			|| type == AnimationType.FADE_SCALE
			|| type == AnimationType.BLUR_FADE;
	}

	public static void resetShaderColor() {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderSoftGlow(GuiGraphics graphics, int x, int y, int width, int height, float amount, int color) {
		float clamped = Mth.clamp(amount, 0.0F, 1.0F);
		if (clamped <= 0.001F) {
			return;
		}
		int outerAlpha = Mth.clamp((int) (34.0F * clamped), 0, 255);
		int innerAlpha = Mth.clamp((int) (22.0F * clamped), 0, 255);
		graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, withAlpha(color, outerAlpha));
		graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, withAlpha(color, innerAlpha));
	}

	public static void renderOutlinePulse(GuiGraphics graphics, int x, int y, int width, int height, float amount, int color) {
		float clamped = Mth.clamp(amount, 0.0F, 1.0F);
		if (clamped <= 0.001F) {
			return;
		}
		int alpha = Mth.clamp((int) (155.0F * clamped), 0, 255);
		graphics.renderOutline(x, y, width, height, withAlpha(color, alpha));
	}

	public static void renderRipple(GuiGraphics graphics, int x, int y, int width, int height, float amount, int color) {
		float clamped = Mth.clamp(amount, 0.0F, 1.0F);
		if (clamped <= 0.001F) {
			return;
		}
		float missing = 1.0F - clamped;
		int insetX = Math.max(0, (int) (width * 0.5F * missing));
		int insetY = Math.max(0, (int) (height * 0.5F * missing));
		int alpha = Mth.clamp((int) (80.0F * missing), 0, 255);
		graphics.fill(x + insetX, y + insetY, x + width - insetX, y + height - insetY, withAlpha(color, alpha));
		graphics.renderOutline(x + insetX, y + insetY, Math.max(1, width - insetX * 2), Math.max(1, height - insetY * 2), withAlpha(color, alpha + 35));
	}

	public static int withAlpha(int color, int alpha) {
		return (alpha << 24) | (color & 0x00FFFFFF);
	}
}
