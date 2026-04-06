package noppes.npcs.api.gui;

import net.minecraft.inventory.Slot;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiItemSlotUpdate;
import noppes.npcs.api.item.IItemStack;

public interface IItemSlot extends ICustomGuiComponent {

	boolean hasStack();

	IItemStack getStack();

	IItemSlot setStack(@ParamName("stack") IItemStack stack);

	int getGuiType();

	IItemSlot setGuiType(@ParamName("type") int type);

	boolean isPlayerSlot();

	IItemSlot setOnUpdate(@ParamName("onPress") GuiItemSlotUpdate onPress);

	Slot getMCSlot();

}
