package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.ScrollbarAnimationHandler;

@Mixin(AbstractScrollArea.class)
public abstract class AbstractScrollAreaMixin {
	@Unique
	private boolean vanillaUiPlus$scrollbarTransformed;

	@Inject(method = "renderScrollbar", at = @At("HEAD"))
	private void vanillaUiPlus$beforeScrollbar(GuiGraphics graphics, CallbackInfo ci) {
		vanillaUiPlus$scrollbarTransformed = ScrollbarAnimationHandler.beforeScrollbar((AbstractScrollArea) (Object) this, graphics);
	}

	@Inject(method = "renderScrollbar", at = @At("RETURN"))
	private void vanillaUiPlus$afterScrollbar(GuiGraphics graphics, CallbackInfo ci) {
		ScrollbarAnimationHandler.afterScrollbar((AbstractScrollArea) (Object) this, graphics);
		vanillaUiPlus$scrollbarTransformed = false;
	}
}
