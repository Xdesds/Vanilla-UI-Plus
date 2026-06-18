# VanillaUi+

VanillaUi+ is a client-side Fabric mod that adds subtle animation polish to Minecraft's user interface while preserving the vanilla visual language. The goal is simple: menus, inventory interactions, and HUD updates should feel smoother without looking like a separate UI pack.

## Features

- Screen open animations with fade, scale, slide, fade + slide, and optional bounce modes.
- Supported vanilla UI families include inventory, chests, creative inventory, pause/options menus, multiplayer, chat, advancements, anvil, enchanting, and villager trading screens.
- Container polish for item updates, shift-click style transfers, and slot hover movement.
- HUD polish for hotbar selection, XP changes, health changes, hunger changes, and overlay notifications.
- Delta-time based smoothing and easing functions: ease in, ease out, ease in out, cubic, quartic, exponential, and bounce.
- Vanilla+ and Modern presets.
- Performance Mode for shorter, cheaper transitions.

## Installation

1. Install Fabric Loader for Minecraft 1.21.4.
2. Install Fabric API.
3. Place the VanillaUi+ jar in your `mods` folder.
4. Optional: install Mod Menu to configure VanillaUi+ in-game.

VanillaUi+ is client-side. Servers do not need to install it.

## Screenshots

Screenshots will be added for the first public release.

- Inventory animation placeholder
- HUD animation placeholder
- Mod Menu configuration placeholder

## Configuration

If Mod Menu is installed, open `Mods > VanillaUi+ > Configure`.

Without Mod Menu, edit:

```text
config/vanillaui-plus.json
```

Available options:

- `animationType`: `FADE`, `SCALE`, `SLIDE`, `FADE_SLIDE`, or `BOUNCE`
- `easing`: `ease_in`, `ease_out`, `ease_in_out`, `cubic`, `quartic`, or `exponential`
- `animationSpeed`: from `0.25` to `3.0`
- `screenAnimations`: enable or disable screen transitions
- `itemAnimations`: enable or disable container item polish
- `slotHoverAnimations`: enable or disable slot hover animation
- `hudAnimations`: enable or disable HUD animation
- `notificationAnimations`: enable or disable overlay notification motion
- `performanceMode`: use shorter, lower-cost transitions
- `preset`: `vanilla_plus`, `modern`, or `custom`

## Compatibility

- Minecraft: 1.21.4
- Loader: Fabric Loader 0.19.3 or newer
- Requires Fabric API
- Optional Mod Menu integration

VanillaUi+ does not replace vanilla screen textures, widgets, fonts, or layouts. It should remain compatible with most resource packs and lightweight HUD mods, though mods that deeply rewrite the same GUI render methods may need compatibility testing.

## Performance Notes

VanillaUi+ avoids per-frame object creation in render hooks and reuses animation state for live screens and slots. Animations are time-based rather than frame-count based, so they remain consistent at both high and low FPS.

Performance Mode shortens animation durations and reduces the amount of visible motion for players who want the smoothness without extra render work.

## FAQ

### Does this change the vanilla UI design?

No. VanillaUi+ only adds motion and subtle feedback. It does not redesign vanilla screens.

### Is it required on servers?

No. This is a client-side visual mod.

### Can I disable HUD animations?

Yes. Disable `hudAnimations` through Mod Menu or the config file.

### Why are the animations subtle?

The mod is designed to feel like an official vanilla enhancement, not a flashy UI overhaul.

### Does it work with Mod Menu?

Yes. Mod Menu is optional, but when installed it exposes an in-game configuration screen.

## Credits

- Mojang Studios for Minecraft's original UI style.
- FabricMC for the modding toolchain.
- TerraformersMC for Mod Menu.
