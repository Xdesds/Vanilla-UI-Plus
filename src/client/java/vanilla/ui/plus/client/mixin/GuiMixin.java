package vanilla.ui.plus.client.mixin;

import java.util.function.Function;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanilla.ui.plus.client.handler.HudAnimationHandler;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	@Final
	private static ResourceLocation HOTBAR_SELECTION_SPRITE;

	@Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
	private void vanillaUiPlus$beforeHotbar(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
		HudAnimationHandler.beforeHotbar(graphics, tracker);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At("RETURN"))
	private void vanillaUiPlus$afterHotbar(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
		HudAnimationHandler.afterHotbar(graphics);
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void vanillaUiPlus$beforeExperience(GuiGraphics graphics, int y, CallbackInfo ci) {
		HudAnimationHandler.beforeExperience(graphics);
	}

	@Inject(method = "renderExperienceBar", at = @At("RETURN"))
	private void vanillaUiPlus$afterExperience(GuiGraphics graphics, int y, CallbackInfo ci) {
		HudAnimationHandler.afterPulse(graphics);
	}

	@Inject(method = "renderPlayerHealth", at = @At("HEAD"))
	private void vanillaUiPlus$beforeHealth(GuiGraphics graphics, CallbackInfo ci) {
		HudAnimationHandler.beforeHealth(graphics);
	}

	@Inject(method = "renderPlayerHealth", at = @At("RETURN"))
	private void vanillaUiPlus$afterHealth(GuiGraphics graphics, CallbackInfo ci) {
		HudAnimationHandler.afterPulse(graphics);
	}

	@Inject(method = "renderFood", at = @At("HEAD"))
	private void vanillaUiPlus$beforeFood(GuiGraphics graphics, Player player, int top, int right, CallbackInfo ci) {
		HudAnimationHandler.beforeFood(graphics);
	}

	@Inject(method = "renderFood", at = @At("RETURN"))
	private void vanillaUiPlus$afterFood(GuiGraphics graphics, Player player, int top, int right, CallbackInfo ci) {
		HudAnimationHandler.afterPulse(graphics);
	}

	@Inject(method = "renderOverlayMessage", at = @At("HEAD"))
	private void vanillaUiPlus$beforeOverlayMessage(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
		HudAnimationHandler.beforeNotification(graphics);
	}

	@Inject(method = "renderOverlayMessage", at = @At("RETURN"))
	private void vanillaUiPlus$afterOverlayMessage(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
		HudAnimationHandler.afterNotification(graphics);
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("HEAD"))
	private void vanillaUiPlus$beforeScoreboard(GuiGraphics graphics, Objective objective, CallbackInfo ci) {
		HudAnimationHandler.beforeScoreboard(graphics, objective);
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
	private void vanillaUiPlus$afterScoreboard(GuiGraphics graphics, Objective objective, CallbackInfo ci) {
		HudAnimationHandler.afterScoreboard(graphics);
	}

	@Redirect(
		method = "renderItemHotbar",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"
		)
	)
	private void vanillaUiPlus$renderHotbarSprite(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int width, int height) {
		if (HOTBAR_SELECTION_SPRITE.equals(sprite)) {
			HudAnimationHandler.renderHotbarSelection(graphics, renderType, sprite, x, y, width, height);
			return;
		}
		graphics.blitSprite(renderType, sprite, x, y, width, height);
	}
}
