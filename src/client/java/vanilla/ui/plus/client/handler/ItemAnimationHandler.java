package vanilla.ui.plus.client.handler;

import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import vanilla.ui.plus.client.animation.AnimationType;
import vanilla.ui.plus.client.animation.EasingUtilities;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;
import vanilla.ui.plus.client.config.AnimationProfileConfig;
import vanilla.ui.plus.client.render.RenderUtilities;

/**
 * Handles low-cost item and slot micro-interactions.
 */
public final class ItemAnimationHandler {
	private static final Map<Slot, SlotState> STATES = new IdentityHashMap<>();
	private static final Map<String, ItemTrail> TRAILS = new HashMap<>();
	private static final int MAX_TRAILS = 96;
	private static final long TRAIL_TTL_NANOS = 350_000_000L;
	private static final float MAX_TRAIL_DISTANCE = 220.0F;
	private static boolean slotPushed;

	private ItemAnimationHandler() {
	}

	public static void beforeSlot(GuiGraphics graphics, Slot slot, Slot hoveredSlot) {
		slotPushed = false;
		if (!AnimationManager.getInstance().config().itemAnimations || slot == null || !slot.isActive()) {
			return;
		}
		AnimationProfileConfig hoverProfile = AnimationManager.getInstance().config().slotHoverProfile;
		AnimationProfileConfig appearProfile = AnimationManager.getInstance().config().itemAppearProfile;
		AnimationProfileConfig moveProfile = AnimationManager.getInstance().config().itemMoveProfile;
		AnimationProfileConfig disappearProfile = AnimationManager.getInstance().config().itemDisappearProfile;
		AnimationProfileConfig clickProfile = AnimationManager.getInstance().config().slotClickProfile;

		SlotState state = STATES.computeIfAbsent(slot, ignored -> new SlotState());
		ItemStack stack = slot.getItem();
		int count = stack.isEmpty() ? 0 : stack.getCount();
		String itemId = itemKey(stack);
		boolean changed = state.initialized && (state.count != count || !state.itemId.equals(itemId));
		if (changed) {
			state.pulse = 1.0F;
			handleItemChange(state, slot, stack, moveProfile, disappearProfile);
		}
		state.initialized = true;
		state.count = count;
		state.itemId = itemId;
		if (!stack.isEmpty() && state.lastStack.isEmpty()) {
			state.lastStack = stack.copy();
		}

		boolean hovered = slot == hoveredSlot
			&& AnimationManager.getInstance().config().slotHoverAnimations
			&& hoverProfile != null
			&& hoverProfile.enabled;
		float targetHover = hovered ? 1.0F : 0.0F;
		float deltaSeconds = state.elapsedSeconds();
		state.hover = AnimationEngine.damp(state.hover, targetHover, deltaSeconds, response(hoverProfile, AnimationManager.getInstance().hudResponse()));
		state.pulse = AnimationEngine.damp(state.pulse, 0.0F, deltaSeconds, response(appearProfile, 12.0F));
		state.click = AnimationEngine.damp(state.click, 0.0F, deltaSeconds, response(clickProfile, 16.0F));
		state.move = AnimationEngine.damp(state.move, 1.0F, deltaSeconds, response(moveProfile, 14.0F));
		state.disappear = AnimationEngine.damp(state.disappear, 0.0F, deltaSeconds, response(disappearProfile, 12.0F));

		float hoverScale = hoverProfile == null ? 0.045F : hoverProfile.scale;
		float pulseScale = appearProfile == null ? 0.04F : appearProfile.scale;
		float clickScale = clickProfile == null ? 0.035F : clickProfile.scale;
		float lift = hoverProfile == null ? 0.0F : hoverProfile.offsetY;
		float popLift = appearProfile == null ? 2.25F : appearProfile.offsetY;
		float clickOffset = clickProfile == null ? 0.0F : clickProfile.offsetY;
		float scale = 1.0F + state.hover * hoverScale + state.pulse * pulseScale + state.click * clickScale;
		float translateY = -state.hover * lift - state.pulse * popLift + state.click * clickOffset;
		renderDisappearingItem(graphics, state, slot, disappearProfile);
		renderSlotEffects(graphics, slot, hoverProfile, clickProfile, state);
		float[] moveOffset = moveOffset(state, slot, moveProfile);
		translateY += moveOffset[1];
		float translateX = moveOffset[0];
		if (Math.abs(scale - 1.0F) > 0.001F || Math.abs(translateX) > 0.001F || Math.abs(translateY) > 0.001F) {
			float centerX = slot.x + 8.0F;
			float centerY = slot.y + 8.0F;
			graphics.pose().pushPose();
			graphics.pose().translate(centerX + translateX, centerY + translateY, 90.0F);
			graphics.pose().scale(scale, scale, 1.0F);
			graphics.pose().translate(-centerX, -centerY, -90.0F);
			slotPushed = true;
		}
	}

	public static void afterSlot(GuiGraphics graphics) {
		if (slotPushed) {
			graphics.pose().popPose();
			slotPushed = false;
		}
	}

	public static void onSlotClicked(Slot slot) {
		AnimationProfileConfig profile = AnimationManager.getInstance().config().slotClickProfile;
		if (slot != null && profile != null && profile.enabled && AnimationManager.getInstance().config().itemAnimations) {
			STATES.computeIfAbsent(slot, ignored -> new SlotState()).click = 1.0F;
		}
	}

	private static final class SlotState {
		private boolean initialized;
		private String itemId = "empty";
		private int count;
		private ItemStack lastStack = ItemStack.EMPTY;
		private ItemStack disappearingStack = ItemStack.EMPTY;
		private float hover;
		private float pulse;
		private float click;
		private float move = 1.0F;
		private float moveFromX;
		private float moveFromY;
		private float disappear;
		private long lastNanos = System.nanoTime();

		private float elapsedSeconds() {
			long now = System.nanoTime();
			float seconds = Math.min(0.1F, (now - lastNanos) / 1_000_000_000.0F);
			lastNanos = now;
			return seconds;
		}
	}

	private static float response(AnimationProfileConfig profile, float fallback) {
		if (profile == null) {
			return fallback;
		}
		return 1000.0F / Math.max(20.0F, profile.durationMillis / Math.max(0.1F, profile.speedMultiplier));
	}

	private static void handleItemChange(SlotState state, Slot slot, ItemStack stack, AnimationProfileConfig moveProfile, AnimationProfileConfig disappearProfile) {
		if (!"empty".equals(state.itemId)) {
			recordTrail(state.itemId, slot.x, slot.y);
			boolean movingOut = moveProfile != null && moveProfile.enabled;
			if (stack.isEmpty() && !movingOut && disappearProfile != null && disappearProfile.enabled && !state.lastStack.isEmpty()) {
				state.disappearingStack = state.lastStack.copy();
				state.disappear = 1.0F;
			}
		}
		if (!stack.isEmpty()) {
			ItemTrail trail = consumeTrail(itemKey(stack));
			if (trail != null && moveProfile != null && moveProfile.enabled && (trail.x != slot.x || trail.y != slot.y)) {
				float dx = trail.x - slot.x;
				float dy = trail.y - slot.y;
				if (dx * dx + dy * dy <= MAX_TRAIL_DISTANCE * MAX_TRAIL_DISTANCE) {
					state.moveFromX = trail.x;
					state.moveFromY = trail.y;
					state.move = 0.0F;
				}
			}
			state.lastStack = stack.copy();
		} else {
			state.lastStack = ItemStack.EMPTY;
		}
	}

	private static void renderDisappearingItem(GuiGraphics graphics, SlotState state, Slot slot, AnimationProfileConfig profile) {
		if (state.disappearingStack.isEmpty() || state.disappear <= 0.01F || profile == null || !profile.enabled) {
			return;
		}
		float progress = 1.0F - state.disappear;
		float eased = EasingUtilities.byName(profile.easing).apply(progress);
		float missing = 1.0F - eased;
		float scale = switch (profile.type) {
			case DROP -> 1.0F;
			case SPIN, FADE, FADE_SCALE, SHRINK, SCALE_DOWN -> Math.max(0.05F, 1.0F - eased * Math.max(0.05F, profile.scale));
			default -> Math.max(0.05F, 1.0F - eased * 0.2F);
		};
		float offsetY = switch (profile.type) {
			case DROP -> eased * Math.max(8.0F, profile.offsetY);
			case FADE_UP -> -eased * Math.max(6.0F, Math.abs(profile.offsetY));
			default -> missing * profile.offsetY;
		};
		float alpha = Mth.clamp(missing * profile.opacity, 0.0F, 1.0F);
		graphics.pose().pushPose();
		graphics.pose().translate(slot.x + 8.0F, slot.y + 8.0F + offsetY, 120.0F);
		if (profile.type == AnimationType.SPIN || profile.rotation != 0.0F) {
			graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(eased * (profile.rotation == 0.0F ? 180.0F : profile.rotation)));
		}
		graphics.pose().scale(scale, scale, 1.0F);
		graphics.pose().translate(-8.0F, -8.0F, 0.0F);
		com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		graphics.renderItem(state.disappearingStack, 0, 0);
		RenderUtilities.resetShaderColor();
		graphics.pose().popPose();
		if (state.disappear <= 0.02F) {
			state.disappearingStack = ItemStack.EMPTY;
		}
	}

	private static float[] moveOffset(SlotState state, Slot slot, AnimationProfileConfig profile) {
		if (state.move >= 0.999F || profile == null || !profile.enabled) {
			return ZERO_OFFSET;
		}
		float eased = EasingUtilities.byName(profile.easing).apply(state.move);
		float missing = 1.0F - eased;
		float dx = (state.moveFromX - slot.x) * missing;
		float dy = (state.moveFromY - slot.y) * missing;
		if (profile.type == AnimationType.FLOAT) {
			dy -= (float) Math.sin(eased * Math.PI) * Math.max(6.0F, profile.offsetY);
		} else if (profile.type == AnimationType.ARC || profile.type == AnimationType.BEZIER) {
			float arcHeight = Math.max(8.0F, Math.abs(profile.offsetY));
			float curve = (float) Math.sin(eased * Math.PI);
			dy -= curve * arcHeight * Math.max(0.25F, profile.bounceStrength);
			if (profile.type == AnimationType.BEZIER) {
				float side = profile.offsetX == 0.0F ? 4.0F : profile.offsetX;
				dx += curve * side;
			}
		} else if (profile.type == AnimationType.MAGNETIC) {
			float magnet = eased * eased;
			dx = (state.moveFromX - slot.x) * (1.0F - magnet);
			dy = (state.moveFromY - slot.y) * (1.0F - magnet);
			float pull = (float) Math.sin(eased * Math.PI) * profile.elasticStrength * 2.0F;
			dx -= Math.signum(dx) * pull;
			dy -= Math.signum(dy) * pull;
		} else if (profile.type == AnimationType.INERTIA) {
			float overshoot = (float) Math.sin(eased * Math.PI) * profile.elasticStrength * 0.16F;
			dx = (state.moveFromX - slot.x) * (missing - overshoot);
			dy = (state.moveFromY - slot.y) * (missing - overshoot);
		} else if (profile.type == AnimationType.ELASTIC) {
			float wobble = (float) Math.sin(eased * Math.PI * 3.0D) * missing * profile.elasticStrength * 4.0F;
			dx += wobble;
			dy -= wobble * 0.5F;
		} else if (profile.type == AnimationType.BOUNCE || profile.type == AnimationType.POP) {
			dy -= (float) Math.sin(eased * Math.PI) * profile.bounceStrength * 5.0F;
		} else if (profile.type == AnimationType.SLIDE || profile.type == AnimationType.SCALE_SLIDE) {
			dx += profile.offsetX * missing;
			dy += profile.offsetY * missing;
		}
		MOVE_OFFSET[0] = dx;
		MOVE_OFFSET[1] = dy;
		return MOVE_OFFSET;
	}

	private static void recordTrail(String itemId, int x, int y) {
		if ("empty".equals(itemId)) {
			return;
		}
		if (TRAILS.size() > MAX_TRAILS) {
			TRAILS.clear();
		}
		ItemTrail trail = TRAILS.computeIfAbsent(itemId, ignored -> new ItemTrail());
		trail.x = x;
		trail.y = y;
		trail.createdNanos = System.nanoTime();
	}

	private static ItemTrail consumeTrail(String itemId) {
		ItemTrail trail = TRAILS.remove(itemId);
		if (trail == null || System.nanoTime() - trail.createdNanos > TRAIL_TTL_NANOS) {
			return null;
		}
		return trail;
	}

	private static String itemKey(ItemStack stack) {
		return stack.isEmpty() ? "empty" : stack.getItem().toString();
	}

	private static void renderSlotEffects(GuiGraphics graphics, Slot slot, AnimationProfileConfig hoverProfile, AnimationProfileConfig clickProfile, SlotState state) {
		int color = 0xFFFFFF;
		if (hoverProfile != null && hoverProfile.glowIntensity > 0.0F) {
			float amount = state.hover * hoverProfile.glowIntensity;
			RenderUtilities.renderSoftGlow(graphics, slot.x, slot.y, 16, 16, amount, color);
			RenderUtilities.renderOutlinePulse(graphics, slot.x, slot.y, 16, 16, amount, color);
		}
		if (clickProfile != null && clickProfile.glowIntensity > 0.0F) {
			RenderUtilities.renderRipple(graphics, slot.x, slot.y, 16, 16, state.click * clickProfile.glowIntensity, color);
		}
	}

	private static final float[] ZERO_OFFSET = { 0.0F, 0.0F };
	private static final float[] MOVE_OFFSET = { 0.0F, 0.0F };

	private static final class ItemTrail {
		private int x;
		private int y;
		private long createdNanos;
	}
}
