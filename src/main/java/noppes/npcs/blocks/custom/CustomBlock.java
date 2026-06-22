package noppes.npcs.blocks.custom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.mixin.block.properties.IPropertyIntegerMixin;
import noppes.npcs.util.Util;

import java.util.Collections;
import java.util.Objects;

public class CustomBlock extends BlockInterface implements ICustomElement {

	public static EnumBlockRenderType getNbtRenderType(String name) {
		while (name.contains(" ")) {
			name = name.replace(" ", "_");
		}
		switch (name.toLowerCase()) {
			case "invisible":
				return EnumBlockRenderType.INVISIBLE;
            case "entityblock_animated":
				return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
			default:
				return EnumBlockRenderType.MODEL;
		}
	}
	public static SoundType getNbtSoundType(String soundName) {
		switch (soundName.toLowerCase()) {
		case "wood":
			return SoundType.WOOD;
		case "ground":
			return SoundType.GROUND;
		case "plant":
			return SoundType.PLANT;
		case "metal":
			return SoundType.METAL;
		case "glass":
			return SoundType.GLASS;
		case "cloth":
			return SoundType.CLOTH;
		case "sand":
			return SoundType.SAND;
		case "snow":
			return SoundType.SNOW;
		case "ladder":
			return SoundType.LADDER;
		case "anvil":
			return SoundType.ANVIL;
		case "slime":
			return SoundType.SLIME;
		default:
			return SoundType.STONE;
		}
	}

	public @Nullable NBTTagCompound nbtData;
	public AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public AxisAlignedBB EAST_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public AxisAlignedBB SOUTH_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public AxisAlignedBB WEST_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	private EnumBlockRenderType renderType = EnumBlockRenderType.MODEL;
	public PropertyDirection FACING;
	public PropertyInteger INT;
	public PropertyBool BO;

	public CustomBlock(@Nonnull Material material, @Nonnull NBTTagCompound nbtBlock) {
		super(material);
		nbtData = nbtBlock;

		setName("custom_" + nbtBlock.getString("RegistryName"));

		enableStats = true;
		blockSoundType = SoundType.STONE;
		blockParticleGravity = 1.0F;
		lightOpacity = fullBlock ? 255 : 0;
		translucent = !blockMaterial.blocksLight();
		setHardness(1.5f);
		setResistance(10.0f);
		if (nbtBlock.hasKey("Hardness", 5)) { setHardness(nbtBlock.getFloat("Hardness")); }
		if (nbtBlock.hasKey("Resistance", 5)) { setResistance(nbtBlock.getFloat("Resistance")); }
		if (nbtBlock.hasKey("LightLevel", 5)) { setLightLevel(nbtBlock.getFloat("LightLevel")); }
		if (nbtBlock.hasKey("BlockRenderType", 8)) {
			renderType = CustomBlock.getNbtRenderType(nbtBlock.getString("BlockRenderType"));
			setSoundType(CustomBlock.getNbtSoundType(nbtBlock.getString("SoundType")));
		}
		if (nbtBlock.hasKey("AABB", 9)) { setAABB(nbtBlock.getTagList("AABB", 6)); }
		if (BO != null) { setDefaultState(blockState.getBaseState().withProperty(BO, false)); }
		else if (INT != null) {
			setDefaultState(blockState.getBaseState().withProperty(INT, Collections.min(((IPropertyIntegerMixin) INT).getAllowedValues())));
		}
		else if (FACING != null) { setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)); }
		setCreativeTab(CustomTabs.BLOCKS);
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		if (CustomBlocks.registryNbt.hasKey("Property", 10)) {
			NBTTagCompound nbtProperty = CustomBlocks.registryNbt.getCompoundTag("Property");
			switch (nbtProperty.getByte("Type")) {
				case (byte) 1: {
					BO = PropertyBool.create(nbtProperty.getString("Name"));
					return new BlockStateContainer(this, BO);
				}
				case (byte) 3: {
					INT = PropertyInteger.create(nbtProperty.getString("Name"), nbtProperty.getInteger("Min"),
							nbtProperty.getInteger("Max"));
					return new BlockStateContainer(this, INT);
				}
				case (byte) 4: {
					FACING = PropertyDirection.create(nbtProperty.getString("Name"), EnumFacing.Plane.HORIZONTAL);
					return new BlockStateContainer(this, FACING);
				}
			}
		}
		return new BlockStateContainer(this);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) { return null; }

	@SideOnly(Side.CLIENT)
	public @Nonnull BlockRenderLayer getBlockLayer() {
		String name = "";
		if (nbtData == null) {
			if (CustomBlocks.registryNbt != null && CustomBlocks.registryNbt.hasKey("BlockLayer", 8)) { name = CustomBlocks.registryNbt.getString("BlockLayer"); }
		}
		else if (nbtData.hasKey("BlockLayer", 8)) { name = nbtData.getString("BlockLayer"); }
		while (name.contains(" ")) { name = name.replace(" ", "_"); }
		switch (name.toLowerCase()) {
			case "cutout": return BlockRenderLayer.CUTOUT;
			case "cutout_mipped": return BlockRenderLayer.CUTOUT_MIPPED;
			case "translucent": return BlockRenderLayer.TRANSLUCENT;
			default: return BlockRenderLayer.SOLID;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
		if (FACING != null) {
			EnumFacing v = state.getValue(FACING);
			if (v == EnumFacing.EAST) { return EAST_BLOCK_AABB; }
			else if (v == EnumFacing.SOUTH) { return SOUTH_BLOCK_AABB; }
			else if (v == EnumFacing.WEST) { return WEST_BLOCK_AABB; }
		}
		return FULL_BLOCK_AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nullable AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		if (nbtData == null) {
			if (CustomBlocks.registryNbt != null && CustomBlocks.registryNbt.getBoolean("IsPassable")) { return NULL_AABB; }
		}
		else {
			if (nbtData.getBoolean("IsPassable")) { return NULL_AABB; }
			if (FACING != null) {
				EnumFacing v = blockState.getValue(FACING);
				if (v == EnumFacing.NORTH) { return FULL_BLOCK_AABB; }
				else if (v == EnumFacing.EAST) { return EAST_BLOCK_AABB; }
				else if (v == EnumFacing.SOUTH) { return SOUTH_BLOCK_AABB; }
				else if (v == EnumFacing.WEST) { return WEST_BLOCK_AABB; }
			}
		}
		return blockState.getBoundingBox(worldIn, pos);
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		if (FACING != null) { return state.getValue(FACING).getIndex(); }
		return super.getMetaFromState(state);
	}

	@Override
	public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) { return renderType; }

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer) {
		if (FACING != null) {
			return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
		}
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState();
		if (FACING != null) {
			EnumFacing enumfacing = EnumFacing.getFront(meta);
			if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
				enumfacing = EnumFacing.NORTH;
			}
			return state.withProperty(FACING, enumfacing);
		}
		if (INT != null) {
			return state.withProperty(INT, meta);
		}
		if (BO != null) {
			return state.withProperty(BO, meta != 0);
		}
		return state;
	}

	public boolean hasProperty() { return BO != null || INT != null || FACING != null; }

	@Override
	@SuppressWarnings("deprecation")
	public boolean hasTileEntity() { return false; }

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(@Nonnull IBlockState state) {
		if (nbtData == null) {
			return CustomBlocks.registryNbt == null || !CustomBlocks.registryNbt.hasKey("IsFullCube") || CustomBlocks.registryNbt.getBoolean("IsFullCube");
		}
		return !nbtData.hasKey("IsFullCube") || nbtData.getBoolean("IsFullCube");
	}

	@Override
	public boolean isLadder(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity) {
		if (nbtData == null) {
			return CustomBlocks.registryNbt != null && !CustomBlocks.registryNbt.getBoolean("IsLadder");
		}
		return nbtData.getBoolean("IsLadder");
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(@Nonnull IBlockState state) {
		if (nbtData == null) {
			return CustomBlocks.registryNbt == null || !CustomBlocks.registryNbt.hasKey("IsOpaqueCube") || CustomBlocks.registryNbt.getBoolean("IsOpaqueCube");
		}
		return !nbtData.hasKey("IsOpaqueCube") || nbtData.getBoolean("IsOpaqueCube");
	}

	@Override
	public boolean isPassable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		if (nbtData == null) {
			return CustomBlocks.registryNbt == null || !CustomBlocks.registryNbt.hasKey("IsPassable", 3) ? !blockMaterial.blocksMovement() : CustomBlocks.registryNbt.getBoolean("IsPassable");
		}
		return !nbtData.hasKey("IsPassable", 3) ? !blockMaterial.blocksMovement() : nbtData.getBoolean("IsPassable");
	}

	@Override
	public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		if (worldIn.isRemote || FACING == null) {
			return;
		}
		IBlockState iblockstate = worldIn.getBlockState(pos.north());
		IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
		IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
		IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
		EnumFacing enumfacing = state.getValue(FACING);
		if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
			enumfacing = EnumFacing.SOUTH;
		} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
			enumfacing = EnumFacing.NORTH;
		} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
			enumfacing = EnumFacing.EAST;
		} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
			enumfacing = EnumFacing.WEST;
		}
		worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable EntityLivingBase placer, @Nonnull ItemStack stack) {
		if (FACING != null && placer != null) {
			worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		}
	}

	private void setAABB(NBTTagList tagList) {
		if (tagList != null && tagList.getTagType() == 6 && tagList.tagCount() > 5) {
			double[] v = new double[] { 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D };
			for (int i = 0; i < 6; i++) {
				double s = i < 3 ? 0.0d : 1.0d;
				if (i < tagList.tagCount()) {
					s = tagList.getDoubleAt(i);
				}
				v[i] = s;
			}
			FULL_BLOCK_AABB = new AxisAlignedBB(v[0], v[1], v[2], v[3], v[4], v[5]);
			WEST_BLOCK_AABB = new AxisAlignedBB(v[2], v[1], 1 - v[3], v[5], v[4], 1 - v[0]);
			SOUTH_BLOCK_AABB = new AxisAlignedBB(1 - v[3], v[1], 1 - v[5], 1 - v[0], v[4], 1 - v[2]);
			EAST_BLOCK_AABB = new AxisAlignedBB(1 - v[5], v[1], v[0], 1 - v[2], v[4], v[3]);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState withMirror(@Nonnull IBlockState state, @Nonnull Mirror mirrorIn) {
		if (FACING != null) {
			return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
		}
		return super.withMirror(state, mirrorIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState withRotation(@Nonnull IBlockState state, @Nonnull Rotation rot) {
		if (FACING != null) {
			return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
		}
		return super.withRotation(state, rot);
	}

	@Override
	public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.BLOCKS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.BLOCKS) { Util.instance.sort(items); }
		}
	}

	@Override
	public String getCustomName() {
		if (nbtData == null) { return CustomBlocks.registryNbt.getString("RegistryName"); }
		return nbtData.getString("RegistryName");
	}

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }
	
	@Override
	public int getElementType() {
		if (nbtData == null) {
			return CustomBlocks.registryNbt == null || !CustomBlocks.registryNbt.hasKey("BlockType", 1) ? 0 : CustomBlocks.registryNbt.getByte("BlockType");
		}
		return !nbtData.hasKey("BlockType", 1) ? 0 : nbtData.getByte("BlockType");
	}

	@Override
	public boolean showInCreative() {
		if (nbtData == null) {
			return CustomBlocks.registryNbt == null || !CustomBlocks.registryNbt.hasKey("ShowInCreative", 1) || CustomBlocks.registryNbt.getBoolean("ShowInCreative");
		}
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
