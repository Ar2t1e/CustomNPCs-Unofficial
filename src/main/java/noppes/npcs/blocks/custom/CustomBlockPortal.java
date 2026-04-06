package noppes.npcs.blocks.custom;

import java.util.Objects;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.NpcEvent.CustomNpcTeleport;
import noppes.npcs.api.event.PlayerEvent.CustomTeleport;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.util.Util;
import noppes.npcs.util.CustomNPCsScheduler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomBlockPortal extends BlockEndPortal implements ICustomElement {

	public static PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);
	protected static final AxisAlignedBB END_PORTAL_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 0.75D, 1.0D);
	protected static final AxisAlignedBB END_PORTAL_AABB_1 = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D);
	protected static final AxisAlignedBB END_PORTAL_AABB_2 = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);

	protected final NBTTagCompound nbtData;

	public CustomBlockPortal(Material material, NBTTagCompound nbtBlock) {
		super(material);
		nbtData = nbtBlock;
		String name = "custom_" + nbtBlock.getString("RegistryName");
		setRegistryName(CustomNpcs.MODID, name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());
		setDefaultState(blockState.getBaseState().withProperty(CustomBlockPortal.TYPE, 0));

		enableStats = true;
		blockParticleGravity = 1.0F;
		lightOpacity = fullBlock ? 255 : 0;
		translucent = !blockMaterial.blocksLight();

		setHardness(-1.0F);
		setResistance(6000000.0F);
		if (nbtBlock.hasKey("LightLevel", 5)) { setLightLevel(nbtBlock.getFloat("LightLevel")); }
		setCreativeTab(CustomTabs.BLOCKS);
	}

	@Override
	public boolean canEntityDestroy(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
		return false;
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new CustomTileEntityPortal();
	}

	@Override
	public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
		switch (state.getValue(TYPE)) {
			case 1: return END_PORTAL_AABB_1;
			case 2: return END_PORTAL_AABB_2;
			default: return END_PORTAL_AABB;
		}
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) { return state.getValue(TYPE); }

	@Override
	public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}

	@Override
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, meta % 3);
	}

	@Override
	public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.BLOCKS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.BLOCKS) { Util.instance.sort(items); }
		}
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable EntityLivingBase placer, @Nonnull ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof CustomTileEntityPortal) {
			int type = 0;
			if (placer != null) {
				type = placer.rotationPitch < -45 || placer.rotationPitch > 45 ? 0 : 1;
				if (type == 1 && (placer.getHorizontalFacing() == EnumFacing.EAST || placer.getHorizontalFacing() == EnumFacing.WEST)) { type = 2; }
			}
			world.setBlockState(pos, state.withProperty(CustomBlockPortal.TYPE, type));
			((CustomTileEntityPortal) tile).type = type;
			if (nbtData.hasKey("RenderData", 10)
					&& nbtData.getCompoundTag("RenderData").hasKey("SecondSpeed", 5)) {
				NBTTagCompound nbtRender = nbtData.getCompoundTag("RenderData");
				if (nbtRender.hasKey("SecondSpeed", 5)) {
					((CustomTileEntityPortal) tile).speed = nbtRender.getFloat("SecondSpeed");
					if (((CustomTileEntityPortal) tile).speed < 10.0f) {
						((CustomTileEntityPortal) tile).speed = 10.0f;
					} else if (((CustomTileEntityPortal) tile).speed > 10000.0f) {
						((CustomTileEntityPortal) tile).speed = 10000.0f;
					}
				}
				if (nbtRender.hasKey("Transparency", 5)) {
					((CustomTileEntityPortal) tile).alpha = nbtRender.getFloat("Transparency");
					if (((CustomTileEntityPortal) tile).alpha < 0.15f) {
						((CustomTileEntityPortal) tile).alpha = 0.15f;
					} else if (((CustomTileEntityPortal) tile).alpha > 1.0f) {
						((CustomTileEntityPortal) tile).alpha = 1.0f;
					}
				}
			}
			CustomTileEntityPortal adjacent = null;
			for (int i = 0; i < 6; i++) {
				switch (i) {
					case 0: adjacent = getTile(world, pos.south()); break;
					case 1: adjacent = getTile(world, pos.north()); break;
					case 2: adjacent = getTile(world, pos.east()); break;
					case 3: adjacent = getTile(world, pos.west()); break;
					case 4: adjacent = getTile(world, pos.up()); break;
					case 5: adjacent = getTile(world, pos.down()); break;
				}
				if (adjacent != null) { break; }
			}
			if (adjacent != null) {
				final CustomTileEntityPortal parent = adjacent;
				CustomNPCsScheduler.runTack(() -> {
					TileEntity t = world.getTileEntity(pos);
					if (t instanceof CustomTileEntityPortal) {
						CustomTileEntityPortal aTile = (CustomTileEntityPortal) t;
						if (parent.posTp.getY() > -1) { aTile.posTp = new BlockPos(parent.posTp); }
						if (parent.posHomeTp.getY() > -1) { aTile.posHomeTp = new BlockPos(parent.posHomeTp); }
						aTile.dimensionId = parent.dimensionId;
						aTile.homeDimensionId = parent.homeDimensionId;
						aTile.speed = parent.speed;
						aTile.alpha = parent.alpha;
						aTile.updateToClient();
					}
				}, 250);
			}
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
		if (!worldIn.isRemote && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))) {
			int id = nbtData.hasKey("DimensionID", 3) ? nbtData.getInteger("DimensionID") : 0;
			int homeId = nbtData.hasKey("HomeDimensionID", 3) ? nbtData.getInteger("HomeDimensionID") : 0;
			TileEntity blockTile = worldIn.getTileEntity(pos);
			if (blockTile instanceof CustomTileEntityPortal) {
				CustomTileEntityPortal tile = (CustomTileEntityPortal) blockTile;
				if (DimensionManager.isDimensionRegistered(tile.dimensionId)) { id = tile.dimensionId; }
				if (DimensionManager.isDimensionRegistered(tile.homeDimensionId)) { homeId = tile.homeDimensionId; }
			}
			if (DimensionManager.isDimensionRegistered(id)) { id = 0; }
			if (DimensionManager.isDimensionRegistered(homeId)) { homeId = 0; }

			boolean isHome = worldIn.provider.getDimension() == id;
			BlockPos p = null;
			if (blockTile instanceof CustomTileEntityPortal) { p = ((CustomTileEntityPortal) blockTile).getPosTp(isHome); }
			if (p == null) {
				WorldServer world = Objects.requireNonNull(worldIn.getMinecraftServer()).getWorld(isHome ? homeId : id);
				p = world.getSpawnCoordinate();
				if (p == null) { p = world.getSpawnPoint(); }
				if (!world.isAirBlock(p)) { p = world.getTopSolidOrLiquidBlock(p); }
				else if (!world.isAirBlock(p.up())) {
					while (world.isAirBlock(p) && p.getY() > 0) { p = p.down(); }
					if (p.getY() == 0) { p = world.getTopSolidOrLiquidBlock(p); }
				}
			}
			if (entityIn instanceof EntityPlayerMP) {
				CustomTeleport event = EventHooks.onPlayerTeleport((EntityPlayerMP) entityIn, p, pos, isHome ? homeId : id);
				if (!event.isCanceled()) {
					int dimension = event.dimension;
					if (DimensionManager.isDimensionRegistered(id)) { dimension = 0; }
					SPacketDimensionTeleport.teleportPlayer((EntityPlayerMP) entityIn, dimension, event.pos.getX() + 0.5d,
							event.pos.getY(), event.pos.getZ() + 0.5d, entityIn.rotationYaw,
							entityIn.rotationPitch);
				}
			}
			else {
				int dimension = isHome ? homeId : id;
				if (entityIn instanceof EntityNPCInterface) {
					CustomNpcTeleport event = EventHooks.onNpcTeleport((EntityNPCInterface) entityIn, p, pos, isHome ? homeId : id);
					if (event.isCanceled() || entityIn.isDead) { return; }
					dimension = event.dimension;
					if (DimensionManager.isDimensionRegistered(id)) { dimension = 0; }
				}
				entityIn = Util.instance.travelEntity(worldIn.getMinecraftServer(), entityIn, dimension);
				if (entityIn != null) { entityIn.setPosition(p.getX() + 0.5d, p.getY(), p.getZ() + 0.5d); }
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
		if (nbtData.hasKey("RenderData", 10)) {
			double d0 = (float) pos.getX() + rand.nextFloat();
			double d1 = (float) pos.getY() + 0.8F;
			double d2 = (float) pos.getZ() + rand.nextFloat();
			EnumParticleTypes p = EnumParticleTypes.CRIT;
			for (EnumParticleTypes ept : EnumParticleTypes.values()) {
				if (ept.name().equalsIgnoreCase(nbtData.getCompoundTag("RenderData").getString("SpawnParticle"))) {
					p = ept;
					break;
				}
			}
			worldIn.spawnParticle(p, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	private CustomTileEntityPortal getTile(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();
		if (tile instanceof CustomTileEntityPortal && block instanceof CustomBlockPortal
				&& ((CustomBlockPortal) block).getCustomName().equals(getCustomName())) {
			return (CustomTileEntityPortal) tile;
		}
		return null;
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
		return 5;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
