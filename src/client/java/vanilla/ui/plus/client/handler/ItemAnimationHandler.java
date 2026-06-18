package vanilla.ui.plus.client.handler;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vanilla.ui.plus.client.animation.AnimationEngine;
import vanilla.ui.plus.client.animation.AnimationManager;

/**
 * Handles low-cost item and slot micro-interactions.
 */
public final class ItemAnimationHandler {
	private static final Map<Slot, SlotState> STATES = new IdentityHashMap<>();
	private static boolean slotPushed;

	private ItemAnimationHandler() {
	}

	public static void beforeSlot(GuiGraphics graphics, Slot slot, Slot hoveredSlot) {
		slotPushed = false;
		if (!AnimationManager.getInstance().config().itemAnimations || slot == null || !slot.isActive()) {
			return;
		}

		SlotState state = STATES.computeIfAbsent(slot, ignored -> new SlotState());
		ItemStack stack = slot.getItem();
		int count = stack.isEmpty() ? 0 : stack.getCount();
		String itemId = stack.isEmpty() ? "empty" : stack.getItem().toString();
		if (state.initialized && (state.count != count || !state.itemId.equals(itemId))) {
			state.pulse = 1.0F;
		}
		state.initialized = true;
		state.count = count;
		state.itemId = itemId;

		boolean hovered = slot == hoveredSlot && AnimationManager.getInstance().config().slotHoverAnimations;
		float targetHover = hovered ? 1.0F : 0.0F;
		float deltaSeconds = state.elapsedSeconds();
		state.hover = AnimationEngine.damp(state.hover, targetHover, deltaSeconds, AnimationManager.getInstance().hudResponse());
		state.pulse = AnimationEngine.damp(state.pulse, 0.0F, deltaSeconds, 12.0F);

		float scale = 1.0F + state.hover * 0.045F + state.pulse * 0.04F;
		float translateY = -state.pulse * 2.25F;
		if (Math.abs(scale - 1.0F) > 0.001F || translateY != 0.0F) {
			float centerX = slot.x + 8.0F;
			float centerY = slot.y + 8.0F;
			graphics.pose().pushPose();
			graphics.pose().translate(centerX, centerY + translateY, 90.0F);
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

	private static final class SlotState {
		private boolean initialized;
		private String itemId = "empty";
		private int count;
		private float hover;
		private float pulse;
		private long lastNanos = System.nanoTime();

		private float elapsedSeconds() {
			long now = System.nanoTime();
			float seconds = Math.min(0.1F, (now - lastNanos) / 1_000_000_000.0F);
			lastNanos = now;
			return seconds;
		}
	}
}
