package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.ClientScriptData;

public class GuiScriptClient extends GuiScriptInterface {

	protected final ClientScriptData script;

	public GuiScriptClient() {
		super(6);
		script = new ClientScriptData();
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
