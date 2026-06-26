package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.ButtonAnimationHandler;

@Mixin(AbstractButton.class)
public abstract class ButtonMixin {
	@Unique
	private boolean vanillaUiPlus$buttonTransformed;

	@Inject(method = "renderWidget", at = @At("HEAD"))
	private void vanillaUiPlus$beforeRenderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		vanillaUiPlus$buttonTransformed = ButtonAnimationHandler.beforeButton((AbstractWidget) (Object) this, graphics);
	}

	@Inject(method = "renderWidget", at = @At("RETURN"))
	private void vanillaUiPlus$afterRenderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		ButtonAnimationHandler.afterButton(graphics, vanillaUiPlus$buttonTransformed);
		vanillaUiPlus$buttonTransformed = false;
	}
}
