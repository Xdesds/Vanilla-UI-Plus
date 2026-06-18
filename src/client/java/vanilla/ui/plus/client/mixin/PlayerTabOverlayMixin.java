package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.HudAnimationHandler;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {
	@Inject(method = "setVisible", at = @At("HEAD"))
	private void vanillaUiPlus$onVisibleChanged(boolean visible, CallbackInfo ci) {
		HudAnimationHandler.onTabVisibilityChanged(visible);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void vanillaUiPlus$beforeRender(GuiGraphics graphics, int width, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
		HudAnimationHandler.beforeTabList(graphics);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void vanillaUiPlus$afterRender(GuiGraphics graphics, int width, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
		HudAnimationHandler.afterTabList(graphics);
	}
}
