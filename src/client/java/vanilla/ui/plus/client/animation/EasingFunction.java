package vanilla.ui.plus.client.animation;

/**
 * Stateless easing curve used by all VanillaUi+ animations.
 */
@FunctionalInterface
public interface EasingFunction {
	float apply(float progress);
}
