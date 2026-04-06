package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptPlayers extends GuiScriptInterface {

	protected final PlayerScriptData script = new PlayerScriptData(null);

	public GuiScriptPlayers() {
		super(4);
		handler = script;
		Packets.sendServer(new SPacketScriptGet(type));
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
