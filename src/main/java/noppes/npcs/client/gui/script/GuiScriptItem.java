package noppes.npcs.client.gui.script;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;

public class GuiScriptItem extends GuiScriptInterface {

	protected final ItemScriptedWrapper item;

	public GuiScriptItem() {
		super(2);
		handler = item = new ItemScriptedWrapper(new ItemStack(CustomItems.scripter_item));
	}

	@Override
	public void save() {
		super.save();
		sendToServer(item.getMCNbt());
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		item.setMCNbt(compound);
		super.setGuiData(compound);
	}

}
