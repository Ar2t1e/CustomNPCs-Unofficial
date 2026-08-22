package noppes.npcs.blocks.tiles;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.NBTTags;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.schematics.SchematicWrapper;

import javax.annotation.Nonnull;

public class TileBuilder extends TileEntity implements ITickable {

	protected final Stack<Integer> positions = new Stack<>();
	protected final Stack<Integer> positionsSecond = new Stack<>();
	protected SchematicWrapper schematic = null;
	protected boolean show = false;
	protected int ticks = 20;
	public Availability availability = new Availability();
	public int rotation = 0;
	public int yOffset = 0;
	public boolean enabled = false;
	public boolean started = false;
	public boolean finished = false;

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		positions.clear();
		positions.addAll(NBTTags.getIntegerList(compound.getTagList("Positions", 10)));
		positionsSecond.clear();
		positionsSecond.addAll(NBTTags.getIntegerList(compound.getTagList("PositionsSecond", 10)));
		loadPartNBT(compound);
	}

	public void loadPartNBT(NBTTagCompound compound) {
		if (compound.hasKey("SchematicName")) {
			schematic = SchematicController.Instance.load(compound.getString("SchematicName"));
		}
		rotation = compound.getInteger("Rotation");
		yOffset = compound.getInteger("YOffset");
		enabled = compound.getBoolean("Enabled");
		started = compound.getBoolean("Started");
		finished = compound.getBoolean("Finished");
		show = compound.getBoolean("IsShow");
		availability.load(compound.getCompoundTag("Availability"));
		if (show && schematic != null) { ClientEventHandler.addShowThis(this); }
	}

	@Override
	public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Positions", NBTTags.nbtIntegerCollection(new ArrayList<>(positions)));
		compound.setTag("PositionsSecond", NBTTags.nbtIntegerCollection(new ArrayList<>(positionsSecond)));
		savePartNBT(compound);
		return compound;
	}

	public NBTTagCompound savePartNBT(NBTTagCompound compound) {
		if (schematic != null) { compound.setString("SchematicName", schematic.schema.getName()); }
		compound.setInteger("Rotation", rotation);
		compound.setInteger("YOffset", yOffset);
		compound.setBoolean("Enabled", enabled);
		compound.setBoolean("Started", started);
		compound.setBoolean("Finished", finished);
		compound.setBoolean("IsShow", show);
		compound.setTag("Availability", availability.save(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(Objects.requireNonNull(pkt.getNbtCompound()));
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound compound) { loadPartNBT(compound); }

	public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }

	public @Nonnull NBTTagCompound getUpdateTag() { return savePartNBT(new NBTTagCompound()); }

	@SideOnly(Side.CLIENT)
	public void setDrawSchematic(SchematicWrapper schematics, boolean showIn) {
		schematic = schematics;
		show = showIn;
		if (show && schematic != null) { ClientEventHandler.addShowThis(this); }
	}

	public void setSchematic(SchematicWrapper schematics) {
		schematic = schematics;
		if (schematics == null) {
			positions.clear();
			positionsSecond.clear();
		}
		else {
			positions.clear();
			for(int y = 0; y < schematics.schema.getHeight(); ++y) {
				int z;
				int x;
				for(z = 0; z < schematics.schema.getLength() / 2; ++z) {
					for(x = 0; x < schematics.schema.getWidth() / 2; ++x) { positions.add(0, xyzToIndex(x, y, z)); }
				}
				for(z = 0; z < schematics.schema.getLength() / 2; ++z) {
					for(x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) { positions.add(0, xyzToIndex(x, y, z)); }
				}
				for(z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
					for(x = 0; x < schematics.schema.getWidth() / 2; ++x) { positions.add(0, xyzToIndex(x, y, z)); }
				}
				for(z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
					for(x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) { positions.add(0, xyzToIndex(x, y, z)); }
				}
			}
			positionsSecond.clear();
		}
	}

	public int xyzToIndex(int x, int y, int z) {
		return (y * schematic.schema.getLength() + z) * schematic.schema.getWidth() + x;
	}

	public SchematicWrapper getSchematic() { return schematic; }

	public boolean hasSchematic() { return schematic != null; }

	@Override
	public void update() {
		if (!world.isRemote && hasSchematic() && !finished) {
			--ticks;
			if (ticks <= 0) {
				ticks = 200;
				if (positions.isEmpty() && positionsSecond.isEmpty()) { finished = true; }
				else {
					if (!started) {
						for (EntityPlayer player : getPlayerList()) {
							if (availability.isAvailable(player)) {
								started = true;
								break;
							}
						}
						if (!started) { return; }
					}
					List<EntityNPCInterface> list = world.getEntitiesWithinAABB(EntityNPCInterface.class, (new AxisAlignedBB(pos, pos)).grow(32.0D, 32.0D, 32.0D));
					for (EntityNPCInterface npc : list) {
						if (npc.job.getType() == 10) {
							JobBuilder job = (JobBuilder) npc.job;
							if (job.build == null) { job.build = this; }
						}
					}
				}
			}
		}
	}

	private List<EntityPlayer> getPlayerList() {
		if (world == null) { return Collections.emptyList(); }
		return world.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).grow(10.0D, 10.0D, 10.0D));
	}

	public Stack<BlockData> getBlock() {
		if (enabled && !finished && hasSchematic()) {
			boolean bo = positions.isEmpty();
			Stack<BlockData> list = new Stack<>();
			int size = schematic.schema.getWidth() * schematic.schema.getLength() / 4;
			if (size > 30) { size = 30; }
			for(int i = 0; i < size; ++i) {
				if (positions.isEmpty() && !bo || positionsSecond.isEmpty() && bo) { return list; }
				int pos = bo ? positionsSecond.pop() : positions.pop();
				if (pos < schematic.size) {
					int x = pos % schematic.schema.getWidth();
					int z = (pos - x) / schematic.schema.getWidth() % schematic.schema.getLength();
					int y = ((pos - x) / schematic.schema.getWidth() - z) / schematic.schema.getLength();
					IBlockState state = schematic.schema.getBlockState(x, y, z);
					if (!state.isFullBlock() && !bo && state.getBlock() != Blocks.AIR) { positionsSecond.add(0, pos); }
					else {
						BlockPos blockPos = getPos().add(1, yOffset, 1).add(schematic.rotatePos(x, y, z, rotation));
						if (world != null) {
							IBlockState original = world.getBlockState(blockPos);
							if (Block.getStateId(state) != Block.getStateId(original)) {
								NBTTagCompound tile = null;
								if (state.getBlock() instanceof ITileEntityProvider) { tile = schematic.getTileEntity(x, y, z, blockPos); }
								list.add(0, new BlockData(blockPos, state, tile));
							}
						}
					}
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public @Nonnull AxisAlignedBB getRenderBoundingBox() {
		return schematic == null ? super.getRenderBoundingBox() : new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + schematic.schema.getWidth() + 1, pos.getY() + schematic.schema.getHeight() + 1, pos.getZ() + schematic.schema.getLength() + 1);
	}

	public boolean getShow() { return show; }

}
