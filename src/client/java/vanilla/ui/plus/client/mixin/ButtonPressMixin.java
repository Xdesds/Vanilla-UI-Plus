package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.ButtonAnimationHandler;

@Mixin(Button.class)
public abstract class ButtonPressMixin {
	@Inject(method = "onPress", at = @At("HEAD"))
	private void vanillaUiPlus$onPress(CallbackInfo ci) {
		ButtonAnimationHandler.onPress((AbstractWidget) (Object) this);
	}
}
