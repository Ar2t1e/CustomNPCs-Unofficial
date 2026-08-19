package noppes.npcs.blocks.custom;

import java.util.Objects;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.*;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.event.NpcEvent.CustomNpcTeleport;
import noppes.npcs.api.event.PlayerEvent.CustomTeleport;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

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
		setDefaultState(blockState.getBaseState().withProperty(TYPE, 0));

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
	protected @Nonnull BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int meta) { return new CustomTileEntityPortal(); }

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
	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("ConstantConditions")
	public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
													 float hitX, float hitY, float hitZ, int meta,
													 @Nullable EntityLivingBase placer, @Nonnull EnumHand hand) {
		if (placer != null) {
			CustomTileEntityPortal adjacent = null;
			for (EnumFacing f : EnumFacing.values()) {
				adjacent = getAdjacentTile(world, pos.offset(f));
				if (adjacent != null) break;
			}
			int type;
			if (adjacent != null) { type = adjacent.type; }
			else {
				type = placer.rotationPitch < -45 || placer.rotationPitch > 45 ? 0 : 1;
				if (type == 1 && (placer.getHorizontalFacing() == EnumFacing.EAST || placer.getHorizontalFacing() == EnumFacing.WEST)) { type = 2; }
			}
			return getDefaultState().withProperty(TYPE, type);
		}
		else { return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand); }
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,
								@Nullable EntityLivingBase placer, @Nonnull ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof CustomTileEntityPortal) {
			CustomTileEntityPortal portalTile = (CustomTileEntityPortal) tile;
			if (nbtData.hasKey("RenderData", 10)) {
				NBTTagCompound nbtRender = nbtData.getCompoundTag("RenderData");
				if (nbtRender.hasKey("Transparency", 5)) {
					portalTile.setAlpha(nbtRender.getFloat("Transparency"));
				}
			}
			CustomTileEntityPortal adjacent = null;
			for (EnumFacing facing : EnumFacing.VALUES) {
				adjacent = getAdjacentTile(world, pos.offset(facing));
				if (adjacent != null) break;
			}
			if (adjacent != null) {
				if (adjacent.posTp.getY() > -1) { portalTile.posTp = new BlockPos(adjacent.posTp); }
				if (adjacent.posHomeTp.getY() > -1) { portalTile.posHomeTp = new BlockPos(adjacent.posHomeTp); }
				portalTile.dimensionId = adjacent.dimensionId;
				portalTile.homeDimensionId = adjacent.homeDimensionId;
				portalTile.setAlpha(adjacent.getAlpha());
				portalTile.availability.load(adjacent.availability.save(new NBTTagCompound()));
			} else {
				portalTile.homeDimensionId = world.provider.getDimension();
				portalTile.dimensionId = world.provider.getDimension() == 0 ? -1 : 0;
			}
			portalTile.type = state.getValue(TYPE);

			if (placer instanceof EntityPlayerMP && !world.isRemote) {
				if (adjacent == null) { SPacketGuiOpen.sendOpenGui((EntityPlayerMP) placer, EnumGuiType.Portal, null, pos); }
				else {
					placer.sendMessage(Component.translatable("copy.settings.adjacent.block",
							TextFormatting.GRAY + "" + adjacent.getPos().getX(),
							TextFormatting.GRAY + "" + adjacent.getPos().getY(),
							TextFormatting.GRAY + "" + adjacent.getPos().getZ()).getParent());
				} // Copy
			}
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack currentItem = player.inventory.getCurrentItem();
			if (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter) {
				SPacketGuiOpen.sendOpenGui((EntityPlayerMP) player, EnumGuiType.Portal, null, pos);
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
		if (!worldIn.isRemote && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss() &&
				entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))) {
			if (entityIn instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entityIn;
				if (player.getHeldItemMainhand().getItem() instanceof INPCToolItem ||
						(player.getHeldItemMainhand().getItem() instanceof ItemNpcBlock &&
								((ItemNpcBlock) player.getHeldItemMainhand().getItem()).getBlock() instanceof CustomBlockPortal)) {
					return;
				}
			}
			int id = 0;
			int homeId = worldIn.provider.getDimension();
			TileEntity blockTile = worldIn.getTileEntity(pos);
			if (blockTile instanceof CustomTileEntityPortal) {
				CustomTileEntityPortal tile = (CustomTileEntityPortal) blockTile;
				if (entityIn instanceof EntityPlayerMP && !tile.availability.isAvailable((EntityPlayerMP) entityIn)) { return; }
				id = tile.dimensionId;
				homeId = tile.homeDimensionId;
			} else {
				if (nbtData.hasKey("DimensionID", 3)) { id = nbtData.getInteger("DimensionID"); }
				if (nbtData.hasKey("HomeDimensionID", 3)) { homeId = nbtData.getInteger("HomeDimensionID"); }
			}
			if (!DimensionManager.isDimensionRegistered(id)) { id = 0; }
			if (!DimensionManager.isDimensionRegistered(homeId)) { homeId = worldIn.provider.getDimension(); }

			boolean getHome = worldIn.provider.getDimension() == id  && id != homeId;
			BlockPos p = null;
			if (blockTile instanceof CustomTileEntityPortal) { p = ((CustomTileEntityPortal) blockTile).getPosTp(getHome); }
			if (p == null) {
				WorldServer world = Objects.requireNonNull(worldIn.getMinecraftServer()).getWorld(getHome ? homeId : id);
				p = world.getSpawnPoint();
				if (!world.isAirBlock(p)) { p = world.getTopSolidOrLiquidBlock(p); }
				else if (!world.isAirBlock(p.up())) {
					while (world.isAirBlock(p) && p.getY() > 0) { p = p.down(); }
					if (p.getY() == 0) { p = world.getTopSolidOrLiquidBlock(p); }
				}
				p = p.up();
			}
			if (entityIn instanceof EntityPlayerMP) {
				CustomTeleport event = EventHooks.onPlayerTeleport((EntityPlayerMP) entityIn, p, pos, getHome ? homeId : id);
				if (!event.isCanceled()) {
					int dimension = event.dimension;
					if (!DimensionManager.isDimensionRegistered(id)) { dimension = 0; }
					SPacketDimensionTeleport.teleportPlayer((EntityPlayerMP) entityIn, dimension, event.pos.getX() + 0.5d,
							event.pos.getY(), event.pos.getZ() + 0.5d, entityIn.rotationYaw,
							entityIn.rotationPitch);
				}
			}
			else {
				int dimension = getHome ? homeId : id;
				if (entityIn instanceof EntityNPCInterface) {
					CustomNpcTeleport event = EventHooks.onNpcTeleport((EntityNPCInterface) entityIn, p, pos, getHome ? homeId : id);
					if (event.isCanceled() || entityIn.isDead) { return; }
					dimension = event.dimension;
					if (!DimensionManager.isDimensionRegistered(id)) { dimension = 0; }
				}
				MinecraftServer server = worldIn.getMinecraftServer();
				if (server != null) {
					WorldServer world = server.getWorld(dimension);
					if (world != null && entityIn.world.provider.getDimension() != dimension) {
						try { Util.instance.teleportEntity(server, entityIn, dimension, p); }
						catch (CommandException e) { LogWriter.error("[DEBUG] ", e); }
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
		if (nbtData.hasKey("RenderData", 10)) {
			NBTTagCompound compound = nbtData.getCompoundTag("RenderData");
			float f0 = compound.hasKey("ChanceParticle", 3) ?
					ValueUtil.correctInt(compound.getInteger("ChanceParticle"), 0, 100) / 100.0f : 0.1f;
			if (Math.random() < f0) {
				double d0 = (float) pos.getX() + rand.nextFloat();
				double d1 = (float) pos.getY() + 0.8F;
				double d2 = (float) pos.getZ() + rand.nextFloat();
				EnumParticleTypes p = EnumParticleTypes.CRIT;
				for (EnumParticleTypes ept : EnumParticleTypes.values()) {
					if (ept.name().equalsIgnoreCase(compound.getString("SpawnParticle"))) {
						p = ept;
						break;
					}
				}
				worldIn.spawnParticle(p, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	private CustomTileEntityPortal getAdjacentTile(World world, BlockPos pos) {
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
	public INbt getCustomNbt() { return new NBTWrapper(nbtData); }

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
