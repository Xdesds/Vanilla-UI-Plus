package vanilla.ui.plus.client.animation;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.gui.screens.Screen;

/**
 * Stores one animation timeline per live screen instance.
 */
public final class AnimationRegistry {
	private final Map<Screen, AnimationEngine> screenAnimations = new IdentityHashMap<>();

	public AnimationEngine getOrCreate(Screen screen, long durationMillis, EasingFunction easing) {
		AnimationEngine animation = screenAnimations.get(screen);
		if (animation == null) {
			animation = new AnimationEngine(durationMillis, easing);
			screenAnimations.put(screen, animation);
		} else {
			animation.configure(durationMillis, easing);
		}
		return animation;
	}

	public void restart(Screen screen, long durationMillis, EasingFunction easing) {
		getOrCreate(screen, durationMillis, easing).restart();
	}

	public void remove(Screen screen) {
		screenAnimations.remove(screen);
	}

	public void clear() {
		screenAnimations.clear();
	}
}
