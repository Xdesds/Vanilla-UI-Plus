package vanilla.ui.plus.client.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.HudAnimationHandler;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
	@Shadow
	private int chatScrollbarPos;

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
	private void vanillaUiPlus$onSimpleMessage(Component message, CallbackInfo ci) {
		HudAnimationHandler.onChatMessageAdded();
	}

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"))
	private void vanillaUiPlus$onTaggedMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
		HudAnimationHandler.onChatMessageAdded();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void vanillaUiPlus$beforeRender(GuiGraphics graphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		HudAnimationHandler.beginChatRender(chatScrollbarPos);
	}

	@Inject(method = "scrollChat", at = @At("HEAD"))
	private void vanillaUiPlus$onScrollChat(int scroll, CallbackInfo ci) {
		HudAnimationHandler.onChatScrolled();
	}

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"
		)
	)
	private int vanillaUiPlus$renderMessageLine(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color) {
		return HudAnimationHandler.renderChatMessageLine(graphics, font, text, x, y, color);
	}
}
