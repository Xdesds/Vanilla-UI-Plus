package vanilla.ui.plus.client.animation;

import net.minecraft.util.Mth;

/**
 * Central easing library for framerate-independent UI transitions.
 */
public final class EasingUtilities {
	public static final EasingFunction LINEAR = progress -> Mth.clamp(progress, 0.0F, 1.0F);
	public static final EasingFunction EASE_IN = progress -> square(clamp(progress));
	public static final EasingFunction EASE_OUT = progress -> 1.0F - square(1.0F - clamp(progress));
	public static final EasingFunction EASE_IN_OUT = progress -> {
		float value = clamp(progress);
		return value < 0.5F ? 2.0F * value * value : 1.0F - square(-2.0F * value + 2.0F) * 0.5F;
	};
	public static final EasingFunction CUBIC = progress -> {
		float value = clamp(progress);
		return 1.0F - cube(1.0F - value);
	};
	public static final EasingFunction QUARTIC = progress -> {
		float value = clamp(progress);
		return 1.0F - (float) Math.pow(1.0F - value, 4.0D);
	};
	public static final EasingFunction EXPONENTIAL = progress -> {
		float value = clamp(progress);
		if (value <= 0.0F) {
			return 0.0F;
		}
		if (value >= 1.0F) {
			return 1.0F;
		}
		return 1.0F - (float) Math.pow(2.0D, -10.0F * value);
	};
	public static final EasingFunction BOUNCE = progress -> {
		float value = clamp(progress);
		float overshoot = 1.70158F;
		float shifted = value - 1.0F;
		return 1.0F + shifted * shifted * ((overshoot + 1.0F) * shifted + overshoot);
	};

	private EasingUtilities() {
	}

	public static EasingFunction byName(String name) {
		return switch (name) {
			case "ease_in" -> EASE_IN;
			case "ease_out" -> EASE_OUT;
			case "ease_in_out" -> EASE_IN_OUT;
			case "cubic" -> CUBIC;
			case "quartic" -> QUARTIC;
			case "exponential" -> EXPONENTIAL;
			case "bounce" -> BOUNCE;
			default -> LINEAR;
		};
	}

	public static float clamp(float progress) {
		return Mth.clamp(progress, 0.0F, 1.0F);
	}

	private static float square(float value) {
		return value * value;
	}

	private static float cube(float value) {
		return value * value * value;
	}
}
