package vanilla.ui.plus.client.config;

import java.util.Locale;

import net.minecraft.client.gui.screens.Screen;

public enum ScreenAnimationCategory {
	INVENTORY("inventory", "inventoryscreen"),
	CREATIVE_INVENTORY("creative_inventory", "creativemodeinventoryscreen"),
	CHEST("chest", "chestscreen", "containerscreen"),
	FURNACE("furnace", "furnacescreen", "blastfurnacescreen", "smokerscreen"),
	CRAFTING_TABLE("crafting_table", "craftingscreen"),
	PAUSE_MENU("pause_menu", "pause"),
	OPTIONS("options", "optionsscreen", "controls", "soundoptions", "video", "accessibility", "language"),
	MULTIPLAYER("multiplayer", "multiplayer", "joinmultiplayer", "disconnected"),
	SINGLEPLAYER("singleplayer", "selectworld", "createworld"),
	CHAT("chat", "chat"),
	ADVANCEMENTS("advancements", "advancements"),
	ANVIL("anvil", "anvilscreen"),
	ENCHANTING("enchanting", "enchantmentscreen"),
	BEACON("beacon", "beaconscreen"),
	SMITHING("smithing", "smithingscreen"),
	VILLAGER_TRADING("villager_trading", "merchantscreen"),
	CARTOGRAPHY("cartography", "cartographytablescreen"),
	LOOM("loom", "loomscreen"),
	GRINDSTONE("grindstone", "grindstonescreen"),
	STONECUTTER("stonecutter", "stonecutterscreen"),
	RECIPE_BOOK("recipe_book", "recipebook"),
	TITLE("title", "titlescreen"),
	GENERIC("generic");

	public final String key;
	private final String[] markers;

	ScreenAnimationCategory(String key, String... markers) {
		this.key = key;
		this.markers = markers;
	}

	public static ScreenAnimationCategory fromScreen(Screen screen) {
		if (screen == null) {
			return GENERIC;
		}
		String name = screen.getClass().getName().toLowerCase(Locale.ROOT);
		for (ScreenAnimationCategory category : values()) {
			for (String marker : category.markers) {
				if (name.contains(marker)) {
					return category;
				}
			}
		}
		return GENERIC;
	}

	public static ScreenAnimationCategory fromKey(String key) {
		if (key == null) {
			return GENERIC;
		}
		for (ScreenAnimationCategory category : values()) {
			if (category.key.equalsIgnoreCase(key) || category.name().equalsIgnoreCase(key)) {
				return category;
			}
		}
		return GENERIC;
	}
}
