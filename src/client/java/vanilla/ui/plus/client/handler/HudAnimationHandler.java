package vanilla.ui.plus.client.handler;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Scoreboard;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.render.RenderUtilities;
import net.minecraft.util.FormattedCharSequence;

/**
 * Keeps HUD animations isolated so hotbar, chat, tab list, and scoreboard never
 * trigger each other.
 */
public final class HudAnimationHandler {
	private static float hotbarSelectionX = Float.NaN;
	private static float lastXp = -1.0F;
	private static float xpPulse;
	private static int lastHealth = -1;
	private static float healthPulse;
	private static int lastFood = -1;
	private static float foodPulse;
	private static int lastSelectedSlot = -1;
	private static float hotbarSelectionPulse;
	private static boolean pulsePushed;
	private static boolean notificationPushed;
	private static boolean tabPushed;
	private static boolean scoreboardPushed;
	private static AnimationEngine tabAnimation;
	private static AnimationEngine chatAnimation;
	private static AnimationEngine scoreboardAnimation;
	private static AnimationEngine notificationAnimation;
	private static boolean tabVisible;
	private static int scoreboardHash;
	private static long hotbarLastNanos = System.nanoTime();
	private static long xpLastNanos = System.nanoTime();
	private static long healthLastNanos = System.nanoTime();
	private static long foodLastNanos = System.nanoTime();
	private static int chatLineRenderIndex;
	private static int animatedChatLines;

	private HudAnimationHandler() {
	}

	public static boolean enabled() {
		return AnimationManager.getInstance().config().hudAnimations;
	}

	public static void beforeHotbar(GuiGraphics graphics, DeltaTracker tracker) {
		if (!enabled()) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}

		float delta = tracker.getGameTimeDeltaPartialTick(false) / 20.0F;
		int selected = minecraft.player.getInventory().selected;
		float target = selected * 20.0F;
		if (lastSelectedSlot >= 0 && lastSelectedSlot != selected) {
			hotbarSelectionPulse = 1.0F;
		}
		lastSelectedSlot = selected;
		if (Float.isNaN(hotbarSelectionX)) {
			hotbarSelectionX = target;
		}
		hotbarSelectionX = AnimationEngine.damp(hotbarSelectionX, target, delta, AnimationManager.getInstance().hudResponse());
		hotbarSelectionPulse = AnimationEngine.damp(hotbarSelectionPulse, 0.0F, elapsedSeconds(Clock.HOTBAR), 12.0F);
	}

	public static void afterHotbar(GuiGraphics graphics) {
	}

	public static void renderHotbarSelection(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int width, int height) {
		if (!enabled()) {
			graphics.blitSprite(renderType, sprite, x, y, width, height);
			return;
		}
		float offsetX = hotbarSelectionOffset();
		float scale = 1.0F + hotbarSelectionPulse * 0.075F;
		graphics.pose().pushPose();
		graphics.pose().translate(x + width * 0.5F + offsetX, y + height * 0.5F, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-(x + width * 0.5F), -(y + height * 0.5F), 0.0F);
		graphics.blitSprite(renderType, sprite, x, y, width, height);
		graphics.pose().popPose();
	}

	public static void beforeExperience(GuiGraphics graphics) {
		if (!enabled()) {
			return;
		}
		pulsePushed = false;
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}
		float current = minecraft.player.experienceProgress;
		if (lastXp >= 0.0F && Math.abs(current - lastXp) > 0.001F) {
			xpPulse = 1.0F;
		}
		lastXp = current;
		applyBottomCenterPulse(graphics, xpPulse * 0.028F);
		xpPulse = AnimationEngine.damp(xpPulse, 0.0F, elapsedSeconds(Clock.XP), 13.0F);
	}

	public static void beforeHealth(GuiGraphics graphics) {
		if (!enabled()) {
			return;
		}
		pulsePushed = false;
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}
		int health = Mth.ceil(minecraft.player.getHealth());
		if (lastHealth >= 0 && health != lastHealth) {
			healthPulse = 1.0F;
		}
		lastHealth = health;
		applyBottomCenterPulse(graphics, healthPulse * 0.024F);
		healthPulse = AnimationEngine.damp(healthPulse, 0.0F, elapsedSeconds(Clock.HEALTH), 15.0F);
	}

	public static void beforeFood(GuiGraphics graphics) {
		if (!enabled()) {
			return;
		}
		pulsePushed = false;
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}
		int food = minecraft.player.getFoodData().getFoodLevel();
		if (lastFood >= 0 && food != lastFood) {
			foodPulse = 1.0F;
		}
		lastFood = food;
		applyBottomCenterPulse(graphics, foodPulse * 0.024F);
		foodPulse = AnimationEngine.damp(foodPulse, 0.0F, elapsedSeconds(Clock.FOOD), 15.0F);
	}

	public static void afterPulse(GuiGraphics graphics) {
		if (pulsePushed) {
			graphics.pose().popPose();
			pulsePushed = false;
		}
	}

	public static void beforeNotification(GuiGraphics graphics) {
		if (enabled() && AnimationManager.getInstance().config().notificationAnimations) {
			notificationAnimation = ensure(notificationAnimation, 190L);
			graphics.pose().pushPose();
			RenderUtilities.applyHudPanelTransform(graphics, notificationAnimation.progress(), -8.0F, 0.025F);
			notificationPushed = true;
		}
	}

	public static void afterNotification(GuiGraphics graphics) {
		if (notificationPushed) {
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
			notificationPushed = false;
		}
	}

	public static void onTabVisibilityChanged(boolean visible) {
		if (!enabled() || tabVisible == visible) {
			return;
		}
		tabVisible = visible;
		tabAnimation = ensure(tabAnimation, 170L);
		tabAnimation.restart();
	}

	public static void beforeTabList(GuiGraphics graphics) {
		tabPushed = false;
		if (!enabled()) {
			return;
		}
		tabAnimation = ensure(tabAnimation, 170L);
		graphics.pose().pushPose();
		RenderUtilities.applyHudPanelTransform(graphics, tabAnimation.progress(), -14.0F, 0.035F);
		tabPushed = true;
	}

	public static void afterTabList(GuiGraphics graphics) {
		if (tabPushed) {
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
			tabPushed = false;
		}
	}

	public static void onChatMessageAdded() {
		if (!enabled()) {
			return;
		}
		chatAnimation = ensure(chatAnimation, 190L);
		chatAnimation.restart();
		animatedChatLines = 3;
	}

	public static void beginChatRender() {
		chatLineRenderIndex = 0;
	}

	public static int renderChatMessageLine(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color) {
		int lineIndex = chatLineRenderIndex++;
		if (!enabled() || chatAnimation == null || chatAnimation.isDone() || lineIndex >= animatedChatLines) {
			return graphics.drawString(font, text, x, y, color);
		}
		float progress = chatAnimation.progress();
		int alpha = color >>> 24;
		int animatedAlpha = Mth.clamp((int) (alpha * Math.max(0.18F, progress)), 0, 255);
		int animatedColor = (animatedAlpha << 24) | (color & 0x00FFFFFF);
		graphics.pose().pushPose();
		graphics.pose().translate((1.0F - progress) * -8.0F, (1.0F - progress) * 3.0F, 0.0F);
		int result = graphics.drawString(font, text, x, y, animatedColor);
		graphics.pose().popPose();
		return result;
	}

	public static void beforeScoreboard(GuiGraphics graphics, Objective objective) {
		scoreboardPushed = false;
		if (!enabled()) {
			return;
		}
		int currentHash = scoreboardHash(objective);
		if (scoreboardHash != 0 && scoreboardHash != currentHash) {
			scoreboardAnimation = ensure(scoreboardAnimation, 180L);
			scoreboardAnimation.restart();
		}
		scoreboardHash = currentHash;
		if (scoreboardAnimation == null || scoreboardAnimation.isDone()) {
			return;
		}
		graphics.pose().pushPose();
		RenderUtilities.applyHudPanelTransform(graphics, scoreboardAnimation.progress(), 0.0F, 0.02F);
		scoreboardPushed = true;
	}

	public static void afterScoreboard(GuiGraphics graphics) {
		if (scoreboardPushed) {
			graphics.pose().popPose();
			RenderUtilities.resetShaderColor();
			scoreboardPushed = false;
		}
	}

	private static void applyBottomCenterPulse(GuiGraphics graphics, float amount) {
		graphics.pose().pushPose();
		pulsePushed = true;
		if (amount <= 0.0F) {
			return;
		}
		float scale = 1.0F + amount;
		float x = graphics.guiWidth() * 0.5F;
		float y = graphics.guiHeight() - 32.0F;
		graphics.pose().translate(x, y, 0.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-x, -y, 0.0F);
	}

	private static AnimationEngine ensure(AnimationEngine animation, long durationMillis) {
		if (animation == null) {
			return new AnimationEngine(durationMillis, AnimationManager.getInstance().easing());
		}
		animation.configure(durationMillis, AnimationManager.getInstance().easing());
		return animation;
	}

	private static float elapsedSeconds(Clock clock) {
		long now = System.nanoTime();
		long last = switch (clock) {
			case HOTBAR -> hotbarLastNanos;
			case XP -> xpLastNanos;
			case HEALTH -> healthLastNanos;
			case FOOD -> foodLastNanos;
		};
		float seconds = Math.min(0.1F, (now - last) / 1_000_000_000.0F);
		switch (clock) {
			case HOTBAR -> hotbarLastNanos = now;
			case XP -> xpLastNanos = now;
			case HEALTH -> healthLastNanos = now;
			case FOOD -> foodLastNanos = now;
		}
		return seconds;
	}

	private static int scoreboardHash(Objective objective) {
		if (objective == null) {
			return 0;
		}
		int hash = objective.getDisplayName().getString().hashCode();
		Scoreboard scoreboard = objective.getScoreboard();
		for (PlayerScoreEntry entry : scoreboard.listPlayerScores(objective)) {
			hash = 31 * hash + entry.owner().hashCode();
			hash = 31 * hash + entry.value();
		}
		return hash;
	}

	private static float hotbarSelectionOffset() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || Float.isNaN(hotbarSelectionX)) {
			return 0.0F;
		}
		return hotbarSelectionX - minecraft.player.getInventory().selected * 20.0F;
	}

	private enum Clock {
		HOTBAR,
		XP,
		HEALTH,
		FOOD
	}
}
