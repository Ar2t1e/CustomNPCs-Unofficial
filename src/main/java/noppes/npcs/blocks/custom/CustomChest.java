package noppes.npcs.blocks.custom;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CustomChest extends BlockInterface implements ICustomElement {

	protected static final PropertyDirection FACING = BlockHorizontal.FACING;
	protected static final AxisAlignedBB CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
	protected AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	protected final NBTTagCompound nbtData;

	public final boolean isChest;

	public CustomChest(Material material, NBTTagCompound nbtBlock) {
		super(material);
		nbtData = nbtBlock;
		setName("custom_" + nbtBlock.getString("RegistryName"));
		hasTileEntity = true;
		setSoundType(CustomBlock.getNbtSoundType(nbtBlock.getString("SoundType")));
		setHardness(0.0f);
		setResistance(10.0f);
		if (nbtBlock.hasKey("Hardness", 5)) { setHardness(nbtBlock.getFloat("Hardness")); }
		if (nbtBlock.hasKey("Resistance", 5)) { setResistance(nbtBlock.getFloat("Resistance")); }
		if (nbtBlock.hasKey("LightLevel", 5)) { setLightLevel(nbtBlock.getFloat("LightLevel")); }
		setCreativeTab(CustomTabs.BLOCKS);

		isChest = nbtBlock.hasKey("IsChest", 1) && nbtData.getBoolean("IsChest");
		if (nbtBlock.getTag("AABB") instanceof NBTTagList && ((NBTTagList) nbtBlock.getTag("AABB")).getTagType() == 6
				&& ((NBTTagList) nbtBlock.getTag("AABB")).tagCount() > 5) {
			NBTTagList tagList = nbtBlock.getTagList("AABB", 6);
			FULL_BLOCK_AABB = new AxisAlignedBB(tagList.getDoubleAt(0), tagList.getDoubleAt(1), tagList.getDoubleAt(2),
					tagList.getDoubleAt(3), tagList.getDoubleAt(4), tagList.getDoubleAt(5));
		}
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof CustomTileEntityChest) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (CustomTileEntityChest) tile);
		}
		worldIn.updateComparatorOutputLevel(pos, this);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CustomChest.FACING);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) { return new CustomTileEntityChest(); }

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
		if (isChest) { return CHEST_AABB; }
		return FULL_BLOCK_AABB;
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(CustomChest.FACING).getIndex();
	}

	@Override
	public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
		if (isChest) { return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(CustomChest.FACING, enumfacing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean hasCustomBreakingProgress(@Nonnull IBlockState state) { return true; }

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(@Nonnull IBlockState state) { return false; }

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP) {
			if (isChest && worldIn.getBlockState(pos.up()).doesSideBlockChestOpening(worldIn, pos.up(), EnumFacing.DOWN)) {
				return true;
			}
			EntityPlayerMP player = (EntityPlayerMP) playerIn;
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof CustomTileEntityChest) {
				if (((CustomTileEntityChest) tile).isLocked()) {
					boolean isOwner = false;
					ITextComponent message = new TextComponentTranslation("container.isLocked", ((char) 167) + "r" + ((CustomTileEntityChest) tile).getName());
					message.getStyle().setColor(TextFormatting.RED);
					if (!((CustomTileEntityChest) tile).getLockCode().isEmpty()) {
						String locked = ((CustomTileEntityChest) tile).getLockCode().getLock();
						isOwner = locked.contains(player.getName());
						ITextComponent added = new TextComponentString(" ");
						added.getStyle().setColor(TextFormatting.GRAY);
						added.appendSibling(new TextComponentTranslation("companion.owner"));
						ITextComponent names = new TextComponentString(": " + locked);
						names.getStyle().setColor(TextFormatting.RESET);
						added.appendSibling(names);
						message.appendSibling(added);
					}
					if (!isOwner) {
						player.sendMessage(message);
						player.connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS,
										pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
						if (!player.isCreative()) { return true; }
						player.sendMessage(new TextComponentTranslation("gui.allowed"));
					}
				}
				if (nbtData.hasKey("GUIColor", 3)) {
					((CustomTileEntityChest) tile).guiColor = nbtData.getInteger("GUIColor");
				}
				if (nbtData.hasKey("GUIColor", 11)) {
					((CustomTileEntityChest) tile).guiColor = -1;
					((CustomTileEntityChest) tile).guiColorArr = nbtData.getIntArray("GUIColor");
				}
				Packets.sendAll(new SPacketTileEntitySave(tile.writeToNBT(new NBTTagCompound())));
				SPacketGuiOpen.sendOpenGui(player, EnumGuiType.CustomContainer, null, pos);
			}
		}
		return true;
	}

	@Override
	public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		if (worldIn.isRemote || CustomChest.FACING == null) {
			return;
		}
		IBlockState iblockstate = worldIn.getBlockState(pos.north());
		IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
		IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
		IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
		EnumFacing enumfacing = state.getValue(CustomChest.FACING);
		if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
			enumfacing = EnumFacing.SOUTH;
		} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
			enumfacing = EnumFacing.NORTH;
		} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
			enumfacing = EnumFacing.EAST;
		} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
			enumfacing = EnumFacing.WEST;
		}
		worldIn.setBlockState(pos, state.withProperty(CustomChest.FACING, enumfacing), 2);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof CustomTileEntityChest) {
			((CustomTileEntityChest) tile).setBlock(this);
		}
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer,  @Nonnull ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(CustomChest.FACING, placer.getHorizontalFacing().getOpposite()),
				2);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof CustomTileEntityChest) {
			if (stack.hasTagCompound() && stack.getTagCompound() != null  && stack.getTagCompound().hasKey("BlockEntityTag")) {
				tile.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			} else {
				((CustomTileEntityChest) tile).setBlock(this);
			}
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState withMirror(@Nonnull IBlockState state, @Nonnull Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(CustomChest.FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState withRotation(@Nonnull IBlockState state, @Nonnull Rotation rot) {
		return state.withProperty(CustomChest.FACING, rot.rotate(state.getValue(CustomChest.FACING)));
	}

	@Override
	public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.BLOCKS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.BLOCKS) { Util.instance.sort(items); }
		}
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
		return 2;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}
	
}
