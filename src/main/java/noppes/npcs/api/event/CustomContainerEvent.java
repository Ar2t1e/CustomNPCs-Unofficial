package noppes.npcs.api.event;

import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumScriptType;

public class CustomContainerEvent extends CustomNPCsEvent {

	public IContainer container;
	public IPlayer<?> player;

	public CustomContainerEvent(IPlayer<?> playerIn, IContainer containerIn) {
		super();
		container = containerIn;
		player = playerIn;
	}

	@EventName(EnumScriptType.CUSTOM_CHEST_CLOSED)
	public static class CloseEvent extends CustomContainerEvent {
		public CloseEvent(IPlayer<?> player, IContainer container) { super(player, container); }
	}

	@EventName(EnumScriptType.CUSTOM_CHEST_CLICKED)
	public static class SlotClickedEvent extends CustomContainerEvent {
		public IItemStack heldItem;
		public int slot;
		public IItemStack slotItem;

		public SlotClickedEvent(IPlayer<?> player, IContainer container, int slotIdIn, IItemStack slotItemIn, IItemStack heldItemIn) {
			super(player, container);
			slotItem = slotItemIn;
			heldItem = heldItemIn;
			slot = slotIdIn;
		}
	}

}
