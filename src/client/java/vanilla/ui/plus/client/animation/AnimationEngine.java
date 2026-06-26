package vanilla.ui.plus.client.animation;

import net.minecraft.util.Mth;

/**
 * A compact, reusable timeline that advances from real elapsed time.
 */
public final class AnimationEngine {
	private long startNanos;
	private long durationNanos;
	private EasingFunction easing;

	public AnimationEngine(long durationMillis, EasingFunction easing) {
		this.durationNanos = millisToNanos(durationMillis);
		this.easing = easing;
		restart();
	}

	public void restart() {
		this.startNanos = System.nanoTime();
	}

	public void configure(long durationMillis, EasingFunction easing) {
		this.durationNanos = millisToNanos(durationMillis);
		this.easing = easing;
	}

	public float progress() {
		return easing.apply(rawProgress());
	}

	public float rawProgress() {
		if (durationNanos <= 0L) {
			return 1.0F;
		}
		float raw = (System.nanoTime() - startNanos) / (float) durationNanos;
		return Mth.clamp(raw, 0.0F, 1.0F);
	}

	public boolean isDone() {
		return System.nanoTime() - startNanos >= durationNanos;
	}

	public static float damp(float current, float target, float deltaSeconds, float response) {
		float factor = 1.0F - (float) Math.exp(-response * Math.max(0.0F, deltaSeconds));
		return Mth.lerp(Mth.clamp(factor, 0.0F, 1.0F), current, target);
	}

	private static long millisToNanos(long millis) {
		return Math.max(1L, millis) * 1_000_000L;
	}
}
