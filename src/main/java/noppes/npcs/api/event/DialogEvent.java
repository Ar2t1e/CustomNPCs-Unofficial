package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.constants.EnumScriptType;

public class DialogEvent extends NpcEvent {

	public IDialog dialog;
	public IPlayer<?> player;

	public DialogEvent(ICustomNpc<?> npc, EntityPlayer playerIn, IDialog dialogIn) {
		super(npc);
		dialog = dialogIn;
		player = (IPlayer<?>) API.getIEntity(playerIn);
	}

	@Cancelable
	@EventName(EnumScriptType.DIALOG_OPTION)
	public static class OptionEvent extends DialogEvent {
		public IDialogOption option;

		public OptionEvent(ICustomNpc<?> npc, EntityPlayer player, IDialog dialog, IDialogOption optionIn) {
			super(npc, player, dialog);
			option = optionIn;
		}
	}

	@EventName(EnumScriptType.DIALOG_CLOSE)
	public static class CloseEvent extends DialogEvent {
		public CloseEvent(ICustomNpc<?> npc, EntityPlayer player, IDialog dialog) { super(npc, player, dialog); }
	}

	@Cancelable
	@EventName(EnumScriptType.DIALOG)
	public static class OpenEvent extends DialogEvent {
		public OpenEvent(ICustomNpc<?> npc, EntityPlayer player, IDialog dialog) { super(npc, player, dialog); }
	}

}
