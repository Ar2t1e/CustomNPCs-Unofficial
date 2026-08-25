package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.ForgeScriptData;

public class GuiScriptForge extends GuiScriptInterface {

	protected final ForgeScriptData script = new ForgeScriptData();

	public GuiScriptForge() {
		super(3);
		handler = script;
	}

	@Override
	public void save() {
		super.save();
		sendToServer(script.save(new NBTTagCompound()));
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		script.load(compound);
		super.setGuiData(compound);
	}

}
