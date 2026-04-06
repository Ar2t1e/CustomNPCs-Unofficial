package noppes.npcs.controllers;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.event.CustomGuiEvent;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.containers.ContainerCustomGui;

public class CustomGuiController {

	static boolean checkGui(CustomGuiEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (!(player.openContainer instanceof ContainerCustomGui)) { return false; }
		else { return ((ContainerCustomGui)player.openContainer).customGui.getId() == event.gui.getId(); }
	}

	public static void onButton(CustomGuiEvent.ButtonEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (checkGui(event)) {
			CustomGuiWrapper gui = getOpenGui(player);
			if (gui != null && gui.getScriptHandler() != null) {
				((CustomGuiWrapper) event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_BUTTON.function, event);
			}
		}
		WrapperNpcAPI.EVENT_BUS.post(event);
	}

	public static void onQuickCraft(CustomGuiEvent.SlotEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (checkGui(event)) {
			CustomGuiWrapper gui = getOpenGui(player);
			if (gui != null && gui.getScriptHandler() != null) {
				((CustomGuiWrapper) event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SLOT.function, event);
			}
		}
		WrapperNpcAPI.EVENT_BUS.post(event);
	}

	public static void onScrollClick(CustomGuiEvent.ScrollEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (checkGui(event)) {
			CustomGuiWrapper gui = getOpenGui(player);
			if (gui != null && gui.getScriptHandler() != null) {
				((CustomGuiWrapper) event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SCROLL.function, event);
			}
		}
		WrapperNpcAPI.EVENT_BUS.post(event);
	}

	public static boolean onSlotClick(CustomGuiEvent.SlotClickEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (checkGui(event)) {
			CustomGuiWrapper gui = getOpenGui(player);
			if (gui != null && gui.getScriptHandler() != null) {
				((CustomGuiWrapper) event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SLOT_CLICKED.function, event);
			}
		}
		return WrapperNpcAPI.EVENT_BUS.post(event);
	}

	public static void onClose(CustomGuiEvent.CloseEvent event) {
		EntityPlayer player = event.player.getMCEntity();
		if (checkGui(event)) {
			CustomGuiWrapper gui = getOpenGui(player);
			if (gui != null && gui.getScriptHandler() != null) {
				((CustomGuiWrapper) event.gui).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_CLOSED.function, event);
			}
		}
		WrapperNpcAPI.EVENT_BUS.post(event);
	}

	public static CustomGuiWrapper getOpenGui(EntityPlayer player) {
		return player.openContainer instanceof ContainerCustomGui ? ((ContainerCustomGui) player.openContainer).customGui : null;
	}
}
