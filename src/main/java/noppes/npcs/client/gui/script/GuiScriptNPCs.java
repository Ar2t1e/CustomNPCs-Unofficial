package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.NpcScriptData;

public class GuiScriptNPCs extends GuiScriptInterface {

	protected final NpcScriptData script = new NpcScriptData();

	public GuiScriptNPCs() {
		super(8);
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
