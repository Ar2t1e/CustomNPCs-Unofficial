package noppes.npcs.roles;

import java.util.Objects;
import java.util.Stack;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobBuilder;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

public class JobBuilder extends JobInterface implements IJobBuilder {

	public TileBuilder build = null;
	protected BlockPos possibleBuildPos = null;
	protected Stack<BlockData> placingList = null;
	protected BlockData placing = null;
	protected int tryTicks = 0;
	protected int ticks = 0;

	public JobBuilder(EntityNPCInterface npc) {
		super(npc);
		overrideMainHand = true;
		type = JobType.BUILDER;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.BUILDER;
		if (compound.hasKey("BuildX")) {
			possibleBuildPos = new BlockPos(compound.getInteger("BuildX"), compound.getInteger("BuildY"),
					compound.getInteger("BuildZ"));
		}
		if (possibleBuildPos != null && compound.hasKey("Placing")) {
			Stack<BlockData> placing = new Stack<>();
			NBTTagList list = compound.getTagList("Placing", 10);
			for (int i = 0; i < list.tagCount(); ++i) {
				placing.add(BlockData.getData(list.getCompoundTagAt(i)));
			}
			placingList = placing;
		}
		if (npc != null) { npc.ais.doorInteract = 1; }
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		if (build != null) {
			compound.setInteger("BuildX", build.getPos().getX());
			compound.setInteger("BuildY", build.getPos().getY());
			compound.setInteger("BuildZ", build.getPos().getZ());
			if (placingList != null && !placingList.isEmpty()) {
				NBTTagList list = new NBTTagList();
				for (BlockData data : placingList) { list.appendTag(data.getNBT()); }
				if (placing != null) { list.appendTag(placing.getNBT()); }
				compound.setTag("Placing", list);
			}
		}
		return compound;
	}

	@Override
	public IItemStack getMainhand() {
		if (npc != null) {
			String name = npc.getJobData();
			ItemStack item = stringToItem(name);
			return item.isEmpty() ? npc.inventory.weapons.get(0) : Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(item);
		}
		return ItemStackWrapper.AIR;
	}

	@Override
	public boolean aiShouldExecute() {
		if (possibleBuildPos != null && npc != null) {
			TileEntity tile = npc.world.getTileEntity(possibleBuildPos);
			if (tile instanceof TileBuilder) { build = (TileBuilder) tile; }
			else { placingList.clear(); }
			possibleBuildPos = null;
		}
		return build != null;
	}

	@Override
	public void aiUpdateTask() {
		if (npc != null) {
			if ((!build.finished || placingList != null) && build.enabled && !build.isInvalid()) {
				if (ticks++ >= 10) {
					ticks = 0;
					if ((placingList == null || placingList.isEmpty()) && placing == null) {
						placingList = build.getBlock();
						npc.setJobData("");
					}
					else {
						if (placing == null) {
							placing = placingList.pop();
							if (placing.state.getBlock() == Blocks.STRUCTURE_VOID) {
								placing = null;
								return;
							}
							tryTicks = 0;
							npc.setJobData(blockToString(placing));
						}
						npc.getNavigator().tryMoveToXYZ(placing.pos.getX(), placing.pos.getY() + 1, placing.pos.getZ(), 1.0D);
						if (tryTicks++ > 40 || npc.nearPosition(placing.pos)) {
							BlockPos blockPos = placing.pos;
							placeBlock();
							if (tryTicks > 40) {
								blockPos = NoppesUtilServer.getClosePos(blockPos, npc.world);
								npc.setPositionAndUpdate(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
							}
						}
					}
				}
			}
			else {
				build = null;
				npc.getNavigator().tryMoveToXYZ(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), 1.0);
			}
		}
	}

	@Override
	public void stop() { reset(); }

	@Override
	public void reset() {
		build = null;
		if (npc != null) { npc.setJobData(""); }
	}

	@Override
	public boolean isBuilding() { return build != null && build.enabled && !build.finished && build.started; }

	private String blockToString(BlockData data) {
		return data.state.getBlock() == Blocks.AIR ? Objects.requireNonNull(Items.IRON_PICKAXE.getRegistryName()).toString() : itemToString(data.getStack());
	}

	public void placeBlock() {
		if (placing != null && npc != null) {
			npc.getNavigator().clearPath();
			npc.swingArm(EnumHand.MAIN_HAND);
			npc.world.setBlockState(placing.pos, placing.state, 2);
			if (placing.state.getBlock() instanceof ITileEntityProvider && placing.tile != null) {
				TileEntity tile = npc.world.getTileEntity(placing.pos);
				if (tile != null) {
					try { tile.readFromNBT(placing.tile); } catch (Exception e) { LogWriter.error(e); }
				}
			}
			placing = null;
		}

	}

	// New from Unofficial (BetaZavr)
	@Override
	public boolean isWorking() { return build != null && !build.finished && placingList != null && build.enabled && !build.isInvalid(); }

}
