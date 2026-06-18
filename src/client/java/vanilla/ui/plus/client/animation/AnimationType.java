package vanilla.ui.plus.client.animation;

public enum AnimationType {
	FADE,
	SCALE,
	SLIDE,
	FADE_SLIDE,
	BOUNCE;

	public static AnimationType fromName(String name) {
		for (AnimationType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return FADE_SLIDE;
	}
}
