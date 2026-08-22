package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.entity.EntityNPCInterface;

public class BlockScriptedDoorWrapper extends BlockWrapper implements IBlockScriptedDoor {

	protected TileScriptedDoor tile;

	public BlockScriptedDoorWrapper(World world, IBlockState state, BlockPos pos) {
		super(world, state, pos);
		tile = (TileScriptedDoor) super.tile;
	}

	@Override
	public boolean getOpen() { return getMCBlockState().getValue(BlockDoor.OPEN).equals(true); }

	@Override
	public void setOpen(boolean open) {
		if (getOpen() != open && !isRemoved() && world != null) {
			((BlockDoor) getMCBlock()).toggleDoor(world.getMCWorld(), iPos.blockPos, open);
		}
	}

	@Override
	public void setBlockModel(String name) {
		Block b = null;
		if (name != null) { b = Block.getBlockFromName(name); }
		tile.setItemModel(b);
	}

	@Override
	public String getBlockModel() { return Block.REGISTRY.getNameForObject(tile.blockModel) + ""; }

	@Override
	public ITimers getTimers() { return tile.timers; }

	@Override
	public float getHardness() { return tile.blockHardness; }

	@Override
	public void setHardness(float hardness) { tile.blockHardness = hardness; }

	@Override
	public float getResistance() { return tile.blockResistance; }

	@Override
	public void setResistance(float resistance) { tile.blockResistance = resistance; }

	@Override
	public void setTile(TileEntity tileIn) {
		if (tileIn instanceof TileScriptedDoor) {
			tile = (TileScriptedDoor) tileIn;
			super.setTile(tile);
		}
	}

	@Override
	public String getSound(boolean isOpen) { return tile.getSound(isOpen); }

	@Override
	public void setSound(boolean isOpen, String song) { tile.setSound(isOpen, song); }

	@Override
	@SuppressWarnings("ConstantConditions")
	public String executeCommand(String command) {
		if (tile == null || tile.getWorld() == null || tile.getWorld().getMinecraftServer() == null) {
			throw new CustomNPCsException("There is no world or server to execute the command!");
		}
		if (!tile.getWorld().getMinecraftServer().isCommandBlockEnabled()) {
			throw new CustomNPCsException("Command blocks need to be enabled to executeCommands");
		}
		FakePlayer player = EntityNPCInterface.CommandPlayer;
		player.setWorld(tile.getWorld());
		player.setPosition(getX(), getY(), getZ());
		return NoppesUtilServer.runCommand(player.world, tile.getPos(), "ScriptBlock: " + tile.getPos(), command, null, player);
	}

}
