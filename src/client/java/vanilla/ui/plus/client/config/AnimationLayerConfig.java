package vanilla.ui.plus.client.config;

import vanilla.ui.plus.client.animation.AnimationType;

public final class AnimationLayerConfig {
	public boolean enabled = true;
	public AnimationType type = AnimationType.FADE_SCALE;
	public int durationMillis = 180;
	public int delayMillis = 0;
	public String easing = "cubic";
	public float strength = 1.0F;
	public float scale = 0.08F;
	public float opacity = 1.0F;
	public float rotation = 0.0F;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public float bounceStrength = 1.0F;
	public float elasticStrength = 1.0F;
	public float blurAmount = 0.0F;
	public float glowIntensity = 0.0F;

	public static AnimationLayerConfig of(AnimationType type) {
		AnimationLayerConfig layer = new AnimationLayerConfig();
		layer.type = type;
		return layer;
	}

	public AnimationLayerConfig copy() {
		AnimationLayerConfig copy = new AnimationLayerConfig();
		copy.enabled = enabled;
		copy.type = type;
		copy.durationMillis = durationMillis;
		copy.delayMillis = delayMillis;
		copy.easing = easing;
		copy.strength = strength;
		copy.scale = scale;
		copy.opacity = opacity;
		copy.rotation = rotation;
		copy.offsetX = offsetX;
		copy.offsetY = offsetY;
		copy.bounceStrength = bounceStrength;
		copy.elasticStrength = elasticStrength;
		copy.blurAmount = blurAmount;
		copy.glowIntensity = glowIntensity;
		return copy;
	}

	public void sanitize(AnimationType fallbackType, String fallbackEasing) {
		if (type == null) {
			type = fallbackType;
		}
		if (easing == null || easing.isBlank()) {
			easing = fallbackEasing;
		}
		durationMillis = clamp(durationMillis, 20, 2000);
		delayMillis = clamp(delayMillis, 0, 2000);
		strength = clamp(strength, 0.0F, 4.0F);
		scale = clamp(scale, 0.0F, 1.5F);
		opacity = clamp(opacity, 0.0F, 1.0F);
		rotation = clamp(rotation, -360.0F, 360.0F);
		offsetX = clamp(offsetX, -500.0F, 500.0F);
		offsetY = clamp(offsetY, -500.0F, 500.0F);
		bounceStrength = clamp(bounceStrength, 0.0F, 4.0F);
		elasticStrength = clamp(elasticStrength, 0.0F, 4.0F);
		blurAmount = clamp(blurAmount, 0.0F, 32.0F);
		glowIntensity = clamp(glowIntensity, 0.0F, 1.0F);
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
