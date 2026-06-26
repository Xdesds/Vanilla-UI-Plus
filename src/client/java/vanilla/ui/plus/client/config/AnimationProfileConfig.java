package vanilla.ui.plus.client.config;

import java.util.ArrayList;
import java.util.List;

import vanilla.ui.plus.client.animation.AnimationType;

public final class AnimationProfileConfig {
	public boolean enabled = true;
	public AnimationType type = AnimationType.FADE_SCALE;
	public int durationMillis = 180;
	public int delayMillis = 0;
	public float speedMultiplier = 1.0F;
	public String easing = "cubic";
	public float scale = 0.08F;
	public float opacity = 1.0F;
	public float rotation = 0.0F;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public float bounceStrength = 1.0F;
	public float elasticStrength = 1.0F;
	public float blurAmount = 0.0F;
	public float glowIntensity = 0.0F;
	public List<AnimationLayerConfig> layers = new ArrayList<>();

	public static AnimationProfileConfig single(AnimationType type, int durationMillis, String easing) {
		AnimationProfileConfig profile = new AnimationProfileConfig();
		profile.type = type;
		profile.durationMillis = durationMillis;
		profile.easing = easing;
		profile.layers.add(AnimationLayerConfig.of(type));
		return profile;
	}

	public AnimationProfileConfig copy() {
		AnimationProfileConfig copy = new AnimationProfileConfig();
		copy.enabled = enabled;
		copy.type = type;
		copy.durationMillis = durationMillis;
		copy.delayMillis = delayMillis;
		copy.speedMultiplier = speedMultiplier;
		copy.easing = easing;
		copy.scale = scale;
		copy.opacity = opacity;
		copy.rotation = rotation;
		copy.offsetX = offsetX;
		copy.offsetY = offsetY;
		copy.bounceStrength = bounceStrength;
		copy.elasticStrength = elasticStrength;
		copy.blurAmount = blurAmount;
		copy.glowIntensity = glowIntensity;
		copy.layers = new ArrayList<>();
		if (layers != null) {
			for (AnimationLayerConfig layer : layers) {
				if (layer != null) {
					copy.layers.add(layer.copy());
				}
			}
		}
		return copy;
	}

	public void sanitize(AnimationType fallbackType, String fallbackEasing) {
		if (type == null) {
			type = fallbackType;
		}
		if (easing == null || easing.isBlank()) {
			easing = fallbackEasing;
		}
		durationMillis = clamp(durationMillis, 20, 2500);
		delayMillis = clamp(delayMillis, 0, 2000);
		speedMultiplier = clamp(speedMultiplier, 0.1F, 5.0F);
		scale = clamp(scale, 0.0F, 1.5F);
		opacity = clamp(opacity, 0.0F, 1.0F);
		rotation = clamp(rotation, -360.0F, 360.0F);
		offsetX = clamp(offsetX, -500.0F, 500.0F);
		offsetY = clamp(offsetY, -500.0F, 500.0F);
		bounceStrength = clamp(bounceStrength, 0.0F, 4.0F);
		elasticStrength = clamp(elasticStrength, 0.0F, 4.0F);
		blurAmount = clamp(blurAmount, 0.0F, 32.0F);
		glowIntensity = clamp(glowIntensity, 0.0F, 1.0F);
		if (layers == null) {
			layers = new ArrayList<>();
		}
		layers.removeIf(layer -> layer == null);
		if (layers.isEmpty()) {
			AnimationLayerConfig layer = AnimationLayerConfig.of(type);
			layer.durationMillis = durationMillis;
			layer.delayMillis = delayMillis;
			layer.easing = easing;
			layer.scale = scale;
			layer.opacity = opacity;
			layer.rotation = rotation;
			layer.offsetX = offsetX;
			layer.offsetY = offsetY;
			layer.bounceStrength = bounceStrength;
			layer.elasticStrength = elasticStrength;
			layer.blurAmount = blurAmount;
			layer.glowIntensity = glowIntensity;
			layers.add(layer);
		}
		while (layers.size() > 8) {
			layers.remove(layers.size() - 1);
		}
		int totalDurationMillis = durationMillis;
		for (AnimationLayerConfig layer : layers) {
			layer.sanitize(type, easing);
			totalDurationMillis = Math.max(totalDurationMillis, layer.delayMillis + layer.durationMillis);
		}
		durationMillis = clamp(totalDurationMillis, 20, 2500);
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
