package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.ScreenAnimationHandler;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Unique
	private boolean vanillaUiPlus$transformed;

	@Inject(method = "added", at = @At("HEAD"))
	private void vanillaUiPlus$onAdded(CallbackInfo ci) {
		ScreenAnimationHandler.onScreenAdded((Screen) (Object) this);
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void vanillaUiPlus$onRemoved(CallbackInfo ci) {
		ScreenAnimationHandler.onScreenRemoved((Screen) (Object) this);
	}

	@Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
	private void vanillaUiPlus$onClose(CallbackInfo ci) {
		if (ScreenAnimationHandler.requestClose((Screen) (Object) this)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderWithTooltip", at = @At("HEAD"))
	private void vanillaUiPlus$beforeRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		vanillaUiPlus$transformed = ScreenAnimationHandler.beforeRender((Screen) (Object) this, graphics);
	}

	@Inject(method = "renderWithTooltip", at = @At("RETURN"))
	private void vanillaUiPlus$afterRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		ScreenAnimationHandler.afterRender((Screen) (Object) this, graphics, vanillaUiPlus$transformed);
		vanillaUiPlus$transformed = false;
	}
}
