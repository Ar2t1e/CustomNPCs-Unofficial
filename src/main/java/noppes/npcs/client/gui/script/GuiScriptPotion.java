package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.PotionScriptData;

public class GuiScriptPotion extends GuiScriptInterface {

	protected final PotionScriptData script = new PotionScriptData();

	public GuiScriptPotion() {
		super(7);
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
