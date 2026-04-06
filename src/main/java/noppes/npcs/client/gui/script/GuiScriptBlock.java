package noppes.npcs.client.gui.script;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptEncrypt;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.blocks.tiles.TileScripted;

public class GuiScriptBlock extends GuiScriptInterface {

	protected final TileScripted script;

	public GuiScriptBlock(BlockPos pos) {
		super(1);
		handler = script = (TileScripted) player.world.getTileEntity(pos);
		Packets.sendServer(new SPacketScriptGet(1));
	}

	@Override
	public void setGuiData(NBTTagCompound compound) {
		script.setNBT(compound);
		super.setGuiData(compound);
	}

	@Override
	public void save() {
		super.save();
		sendToServer(script.getNBT(new NBTTagCompound()));
	}

}
