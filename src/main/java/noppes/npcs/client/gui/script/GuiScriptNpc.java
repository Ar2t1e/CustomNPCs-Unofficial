package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptNpc extends GuiScriptInterface {

	protected final DataScript script;
	private boolean inited = false;

	public GuiScriptNpc(EntityNPCInterface npc) {
		super(0);
		handler = script = npc.script;
		Packets.sendServer(new SPacketScriptGet(type));
	}

	@Override
	public void save() {
		super.save();
		if (inited) { sendToServer(script.save(new NBTTagCompound())); }
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		script.load(compound);
		inited = true;
		super.setGuiData(compound);
	}

}
