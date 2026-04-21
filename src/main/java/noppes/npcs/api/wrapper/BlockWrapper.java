package noppes.npcs.api.wrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.wrapper.data.Data;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.CustomNPCsScheduler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockWrapper implements IBlock {

	/*
	 * Used in:
	 * A large number of Forge events
	 * When checking vision when an NPC is looking at a target
	 * Mod events and scripts
	 */
	public static volatile ConcurrentHashMap<Long, BlockWrapper> blockCache = new ConcurrentHashMap<>(2500);
	public static BlockWrapper AIR = new BlockWrapper(null, Blocks.AIR.getDefaultState(), null);
	public static void clearCache() { blockCache.clear(); }
	public static void checkClearCache() {
		if (blockCache.size() > 2500) {
			blockCache.keySet().stream()
					.limit(blockCache.size() - 2500)
					.forEach(blockCache::remove);
		}
	}
	public static BlockWrapper createNew(@Nullable World world, @Nullable BlockPos pos, @Nonnull IBlockState state) {
		Long key = makeKey(world, state, pos);
		BlockWrapper wrapper = blockCache.get(key);
		if (wrapper == null) {
			wrapper = createBlockWrapper(world, state, pos);
			blockCache.put(key, wrapper);
		}
        return wrapper;
	}
	private static BlockWrapper createBlockWrapper(@Nullable World world, @Nonnull IBlockState state, @Nullable BlockPos pos) {
		Block block = state.getBlock();
		BlockWrapper wrapper;
		if (block instanceof BlockScripted) { wrapper = new BlockScriptedWrapper(world, state, pos); }
		else if (block instanceof BlockScriptedDoor) { wrapper = new BlockScriptedDoorWrapper(world, state, pos); }
		else if (block instanceof BlockFluidBase) { wrapper = new BlockFluidContainerWrapper(world, state, pos); }
		else { wrapper = new BlockWrapper(world, state, pos); }
		if (world != null && pos != null) { wrapper.setTile(world.getTileEntity(pos)); }
		return wrapper;
	}
	@SuppressWarnings("deprecation")
	public static BlockWrapper of(NBTTagCompound compound) {
		World world = CustomNpcs.proxy.overworld();
		IBlockState state;
		Block b = Block.getBlockFromName(compound.getString("Block"));
		if (b == null) { b = Blocks.AIR; }
		if (world == null) { state = Blocks.AIR.getDefaultState(); }
		else { state = b.getStateFromMeta(compound.getInteger("Meta")); }
		BlockPos pos = BlockPos.fromLong(compound.getLong("BlockPos"));
		Block block = state.getBlock();
		if (block instanceof BlockScripted) { return new BlockScriptedWrapper(world, state, pos); }
		else if (block instanceof BlockScriptedDoor) { return new BlockScriptedDoorWrapper(world, state, pos); }
		else if (block instanceof IFluidBlock) { return new BlockFluidContainerWrapper(world, state, pos); }
		return new BlockWrapper(world, state, pos);
	}
	private static Long makeKey(@Nullable World world, @Nonnull IBlockState state, @Nullable BlockPos pos) {
		return (pos == null ? 0 : pos.toLong() << 32) |
				(world == null ? 0 : world.provider.getDimension()) |
				state.getBlock().hashCode();
	}

	protected final @Nullable IWorld world;
	protected final @Nonnull BlockPosWrapper iPos;
	protected @Nullable TileEntity tile;
	protected IBlockState state;
	protected TileNpcEntity storage;

	private IData storeddata = new Data();
	private IData tempdata = new Data();

	public BlockWrapper(@Nullable World worldIn, @Nonnull IBlockState stateIn, @Nullable BlockPos posIn) {
		world = worldIn == null ? null : Objects.requireNonNull(NpcAPI.Instance()).getIWorld(worldIn);
		state = stateIn;
		iPos = posIn == null ? BlockPosWrapper.ORIGIN : new BlockPosWrapper(posIn);
        if (world != null) { setTile(world.getMCWorld().getTileEntity(iPos.blockPos)); }
	}

	@Override
	public int getX() { return iPos.blockPos.getX(); }

	@Override
	public int getY() { return iPos.blockPos.getY(); }

	@Override
	public int getZ() { return iPos.blockPos.getZ(); }

	@Override
	public IPos getPos() { return iPos; }

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>> T getProperty(String name) {
		IBlockState st = getMCBlockState();
		for (Map.Entry<IProperty<?>, Comparable<?>> entry : st.getProperties().entrySet()) {
			IProperty<?> p = entry.getKey();
			if (p.getName().equalsIgnoreCase(name)) { return (T) st.getValue(p); }
		}
		throw new CustomNPCsException("Unknown property: " + name + " for block " + st);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>> void setProperty(String name, Comparable<T> value) {
		IBlockState st = getMCBlockState();
		for (Map.Entry<IProperty<?>, Comparable<? >> entry : st.getProperties().entrySet()) {
			IProperty<?> p = entry.getKey();
			if (p.getName().equalsIgnoreCase(name)) {
				setPropertyValue((IProperty<T>) p, value);
				return;
			}
		}
		throw new CustomNPCsException("Unknown property: " + name + " for block " + st);
	}

	private <T extends Comparable<T>> void setPropertyValue(IProperty<T> p, Comparable<T> c) {
		if (world != null) {
			world.getMCWorld().setBlockState(iPos.blockPos, getMCBlockState().withProperty(p, p.getValueClass().cast(c)), 3);
			CustomNPCsScheduler.runTack(() -> setTile(world.getMCWorld().getTileEntity(iPos.blockPos)), 60);
		}
	}

	@Override
	public List<String> getProperties() {
		ImmutableMap<IProperty<?>, Comparable<?>> props = getMCBlockState().getProperties();
		List<String> list = new ArrayList<>();
		for (IProperty<?> prop : props.keySet()) { list.add(prop.getName()); }
		return list;
	}

	@Override
	public void remove() {
		if (world != null) { world.getMCWorld().setBlockToAir(iPos.blockPos); }
	}

	@Override
	public boolean isRemoved() {
		return world == null || !world.getMCWorld().getBlockState(iPos.blockPos).equals(state);
	}

	@Override
	public boolean isAir() {
		IBlockState st = getMCBlockState();
		return world == null ? st.getMaterial() == Material.AIR :
				st.getBlock().isAir(world.getMCWorld().getBlockState(iPos.blockPos), world.getMCWorld(), iPos.blockPos);
	}

	@Override
	public BlockWrapper setBlock(String name) {
		if (world != null) {
			Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
			if (block != null) {
				IBlockState st = block.getDefaultState();
				world.getMCWorld().setBlockState(iPos.blockPos, st, 2);
				return new BlockWrapper(world.getMCWorld(), st, iPos.blockPos);
			}
		}
		return this;
	}

	@Override
	public BlockWrapper setBlock(IBlock iBlock) {
		IWorld iWorld = iBlock.getWorld();
		if (iWorld == null) { iWorld = world; }
		if (iWorld != null) {
			IBlockState st = iBlock.getMCBlockState();
			iWorld.getMCWorld().setBlockState(iPos.blockPos, st, 2);
			return new BlockWrapper(iWorld.getMCWorld(), st, iPos.blockPos);
		}
		return new BlockWrapper(null, iBlock.getMCBlockState(), iPos.blockPos);
	}

	@Override
	public boolean isContainer() { return tile != null && tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0; }

	@Override
	public IContainer getContainer() {
		if (!isContainer()) { throw new CustomNPCsException("This block is not a container"); }
		return Objects.requireNonNull(NpcAPI.Instance()).getIContainer((IInventory) tile);
	}

	@Override
	public IData getTempdata() { return tempdata; }

	@Override
	public IData getStoreddata() { return storeddata; }

	@Override
	public String getName() { return Objects.requireNonNull(Block.REGISTRY.getNameForObject(getMCBlockState().getBlock())).toString(); }

	@Override
	public String getStateName() { return getMCBlockState().toString(); }

	@Override
	public String getDisplayName() { return tile != null ? Objects.requireNonNull(tile.getDisplayName()).getUnformattedText() : getName(); }

	@Override
	public @Nullable IWorld getWorld() { return world; }

	@Override
	public Block getMCBlock() { return getMCBlockState().getBlock(); }

	@Override
	public boolean hasTileEntity() { return tile != null; }

	public void setTile(TileEntity tileIn) {
		tile = tileIn;
		if (tile instanceof TileNpcEntity) {
			storage = (TileNpcEntity) tile;
			tempdata = storage.tempData;
			storeddata = storage.storedData;
		}
	}

	@Override
	public INbt getBlockEntityNBT() {
		if (tile == null) { throw new CustomNPCsException("This block is not a entity"); }
		NBTTagCompound compound = new NBTTagCompound();
		tile.writeToNBT(compound);
		return new NBTWrapper(compound);
	}

	@SuppressWarnings("unused")
	public INbt getTileEntityNBT() { return getBlockEntityNBT(); }

	@SuppressWarnings("unused")
	public void setBlockEntityNBT(INbt nbt) { setTileEntityNBT(nbt); }

	@Override
	public void setTileEntityNBT(INbt nbt) {
		if (tile == null) { throw new CustomNPCsException("This block is not a entity"); }
		tile.readFromNBT(nbt.getMCNBT());
		tile.markDirty();
		if (world != null) {
			IBlockState st = getMCBlockState();
			world.getMCWorld().notifyBlockUpdate(iPos.blockPos, st, st, 3);
		}
	}

	@Override
	public TileEntity getMCTileEntity() { return tile; }

	@Override
	public @Nonnull IBlockState getMCBlockState() { return world == null ? state : world.getMCWorld().getBlockState(iPos.blockPos); }

	@Override
	public void blockEvent(int type, int data) {
		if (world != null) { world.getMCWorld().addBlockEvent(iPos.blockPos, getMCBlock(), type, data); }
	}

	@Override
	public void interact(int side) {
		if (world != null) {
			EntityPlayer player = EntityNPCInterface.GenericPlayer;
			player.setWorld(world.getMCWorld());
			player.setPosition(iPos.getX(), iPos.getY(), iPos.getZ());
			getMCBlock().onBlockActivated(player.world, iPos.blockPos, player.world.getBlockState(iPos.blockPos),
					EntityNPCInterface.CommandPlayer, EnumHand.MAIN_HAND, EnumFacing.values()[side], 0.0f,
					0.0f, 0.0f);
		}
	}

	@Override
	public boolean isEmpty() { return getMCBlock() == Blocks.AIR; }

	public TileNpcEntity getStorage() { return storage; }

	public @Nullable TileEntity getTile() { return tile; }

	public @Nonnull IBlockState getState() { return state; }

	@Override
	public int getMetadata() { return getMCBlock().getMetaFromState(getMCBlockState()); }

	@Override
	public void setMetadata(int i) {
		if (world != null) {
			world.getMCWorld().setBlockState(iPos.blockPos, getMCBlock().getStateFromMeta(i), 3);
		}
	}

}
