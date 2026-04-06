package noppes.npcs.client.gui.script;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptDoor extends GuiScriptInterface {

	protected final TileScriptedDoor script;

	public GuiScriptDoor(BlockPos pos) {
		super(5);
		handler = script = (TileScriptedDoor) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketScriptGet(type));
	}

	@Override
	public void save() {
		super.save();
		sendToServer(script.getNBT(new NBTTagCompound()));
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		script.setNBT(compound);
		super.setGuiData(compound);
	}

}
