package noppes.npcs.api.event;

import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.gui.IScroll;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumScriptType;

public class CustomGuiEvent extends CustomNPCsEvent {

	public ICustomGui gui;
	public IPlayer<?> player;

	public CustomGuiEvent(IPlayer<?> playerIn, ICustomGui guiIn) {
		super();
		player = playerIn;
		gui = guiIn;
	}

	@EventName(EnumScriptType.CUSTOM_GUI_BUTTON)
	public static class ButtonEvent extends CustomGuiEvent {

		public final int buttonId;
		public final IButton button;

		public ButtonEvent(IPlayer<?> player, ICustomGui gui, IButton buttonIn) {
			super(player, gui);
			button = buttonIn;
			buttonId = buttonIn.getId();
		}
	}

	@EventName(EnumScriptType.CUSTOM_GUI_CLOSED)
	public static class CloseEvent extends CustomGuiEvent {
		public CloseEvent(IPlayer<?> player, ICustomGui gui) { super(player, gui); }
	}

	@EventName(EnumScriptType.CUSTOM_GUI_SCROLL)
	public static class ScrollEvent extends CustomGuiEvent {
		public final boolean doubleClick;
		public final int scrollId;
		public final int scrollIndex;
		public final String[] selection;
		public final IScroll scroll;

		public ScrollEvent(IPlayer<?> player, ICustomGui gui, IScroll scrollIn, int scrollIndexIn, String[] selectionIn, boolean doubleClickIn) {
			super(player, gui);
			scroll = scrollIn;
			scrollId = scrollIn.getId();
			selection = selectionIn;
			doubleClick = doubleClickIn;
			scrollIndex = scrollIndexIn;
		}
	}

	@Cancelable
	@EventName(EnumScriptType.CUSTOM_GUI_SLOT_CLICKED)
	public static class SlotClickEvent extends CustomGuiEvent {

		public String clickType;
		public int dragType;
		public IItemStack heldItem;
		public int slotId;
		public IItemStack stack;
		public Slot slot;

		public SlotClickEvent(IPlayer<?> player, ICustomGui gui, int slotId, IItemStack stack, int dragType,
				String clickType, IItemStack heldItem, Slot slot) {
			super(player, gui);
			this.slotId = slotId;
			this.stack = stack;
			this.dragType = dragType;
			this.clickType = clickType;
			this.heldItem = heldItem;
			this.slot = slot;
		}
	}

	@EventName(EnumScriptType.CUSTOM_GUI_SLOT)
	public static class SlotEvent extends CustomGuiEvent {

		public final int slotId;
		public final IItemStack stack;
		public final IItemStack heldItem;
		public final IItemSlot slot;

		public SlotEvent(IPlayer<?> player, ICustomGui gui, IItemSlot slotIn, IItemStack heldItemIn) {
			super(player, gui);
			slotId = slotIn.getId();
			stack = slotIn.getStack();
			slot = slotIn;
			heldItem = heldItemIn;
		}
	}

	// New from Unofficial (BetaZavr)
	@EventName(EnumScriptType.KEY_GUI_UP)
	public static class KeyPressedEvent extends CustomGuiEvent {
		public int key;

		public KeyPressedEvent(IPlayer<?> player, ICustomGui gui, int k) {
			super(player, gui);
			key = k;
		}
	}

}
