package vanilla.ui.plus.client.mixin;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanilla.ui.plus.client.handler.TooltipAnimationHandler;
import vanilla.ui.plus.client.handler.TextAnimationHandler;
import vanilla.ui.plus.client.handler.ItemCooldownAnimationHandler;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@Inject(
		method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;II)V",
		at = @At("HEAD")
	)
	private void vanillaUiPlus$beforeTooltip(Font font, List<FormattedCharSequence> text, ClientTooltipPositioner positioner, int x, int y, CallbackInfo ci) {
		TooltipAnimationHandler.beforeTooltip((GuiGraphics) (Object) this, x, y);
	}

	@Inject(
		method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;II)V",
		at = @At("RETURN")
	)
	private void vanillaUiPlus$afterTooltip(Font font, List<FormattedCharSequence> text, ClientTooltipPositioner positioner, int x, int y, CallbackInfo ci) {
		TooltipAnimationHandler.afterTooltip((GuiGraphics) (Object) this);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", at = @At("HEAD"))
	private void vanillaUiPlus$beforeString(Font font, String text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.beforeText((GuiGraphics) (Object) this, x, y);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", at = @At("RETURN"))
	private void vanillaUiPlus$afterString(Font font, String text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.afterText((GuiGraphics) (Object) this);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I", at = @At("HEAD"))
	private void vanillaUiPlus$beforeFormatted(Font font, FormattedCharSequence text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.beforeText((GuiGraphics) (Object) this, x, y);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I", at = @At("RETURN"))
	private void vanillaUiPlus$afterFormatted(Font font, FormattedCharSequence text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.afterText((GuiGraphics) (Object) this);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I", at = @At("HEAD"))
	private void vanillaUiPlus$beforeComponent(Font font, net.minecraft.network.chat.Component text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.beforeText((GuiGraphics) (Object) this, x, y);
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I", at = @At("RETURN"))
	private void vanillaUiPlus$afterComponent(Font font, net.minecraft.network.chat.Component text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
		TextAnimationHandler.afterText((GuiGraphics) (Object) this);
	}

	@Inject(method = "renderItemCooldown", at = @At("HEAD"))
	private void vanillaUiPlus$beforeItemCooldown(ItemStack stack, int x, int y, CallbackInfo ci) {
		ItemCooldownAnimationHandler.beforeCooldown((GuiGraphics) (Object) this, stack, x, y);
	}
}
