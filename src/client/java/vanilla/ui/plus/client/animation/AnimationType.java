package vanilla.ui.plus.client.animation;

public enum AnimationType {
	NONE,
	FADE,
	SCALE,
	ZOOM_IN,
	ZOOM_OUT,
	SLIDE,
	SLIDE_LEFT,
	SLIDE_RIGHT,
	SLIDE_TOP,
	SLIDE_BOTTOM,
	FADE_SLIDE,
	FADE_SCALE,
	SCALE_SLIDE,
	BOUNCE,
	ELASTIC,
	OVERSHOOT,
	ROTATE,
	SWING,
	FLIP,
	BLUR_FADE,
	GLOW,
	LIFT,
	SHADOW,
	BORDER,
	PULSE,
	PRESS,
	RIPPLE,
	FLASH,
	POP,
	FLOAT,
	ARC,
	BEZIER,
	MAGNETIC,
	INERTIA,
	SPIN,
	DROP,
	SHRINK,
	FADE_UP,
	SCALE_DOWN,
	TYPEWRITER,
	CUSTOM;

	public static AnimationType fromName(String name) {
		if (name == null || name.isBlank()) {
			return FADE_SLIDE;
		}
		String normalized = name.trim().replace('-', '_').replace(' ', '_');
		for (AnimationType type : values()) {
			if (type.name().equalsIgnoreCase(normalized)) {
				return type;
			}
		}
		return FADE_SLIDE;
	}
}
