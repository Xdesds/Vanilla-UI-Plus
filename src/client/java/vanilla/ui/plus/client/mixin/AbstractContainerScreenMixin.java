package vanilla.ui.plus.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanilla.ui.plus.client.handler.ItemAnimationHandler;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	@Shadow
	protected Slot hoveredSlot;

	@Inject(method = "renderSlot", at = @At("HEAD"))
	private void vanillaUiPlus$beforeSlot(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
		ItemAnimationHandler.beforeSlot(graphics, slot, hoveredSlot);
	}

	@Inject(method = "renderSlot", at = @At("RETURN"))
	private void vanillaUiPlus$afterSlot(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
		ItemAnimationHandler.afterSlot(graphics);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void vanillaUiPlus$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (button == 0 || button == 1) {
			ItemAnimationHandler.onSlotClicked(hoveredSlot);
		}
	}
}
