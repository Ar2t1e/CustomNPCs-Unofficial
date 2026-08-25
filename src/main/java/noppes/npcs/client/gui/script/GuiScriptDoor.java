package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileScriptedDoor;

public class GuiScriptDoor extends GuiScriptInterface {

	protected final TileScriptedDoor script;

	public GuiScriptDoor(BlockPos pos) {
		super(5);
		handler = script = (TileScriptedDoor) player.world.getTileEntity(pos);
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
