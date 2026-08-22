package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTabs;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBorder extends BlockInterface {

	public static PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);

	public BlockBorder() {
		super(Material.ROCK);
		setName("npcborder");
		setSoundType(SoundType.STONE);
		setHardness(5.0f);
		setResistance(10.0f);
		setCreativeTab(CustomTabs.TOOLS);
		setBlockUnbreakable();
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() { return new BlockStateContainer(this, ROTATION); }

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int meta) { return new TileBorder(); }

	@SideOnly(Side.CLIENT)
	public @Nonnull BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) { return state.getValue(ROTATION); }

	@Override
	public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) { return EnumBlockRenderType.MODEL; }

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ROTATION, meta);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(@Nonnull IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(@Nonnull IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack currentItem = player.inventory.getCurrentItem();
		if (!world.isRemote && currentItem.getItem() == CustomItems.wand) {
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) player, EnumGuiType.Border, null, pos);
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
													 float hitX, float hitY, float hitZ, int meta,
													 @Nullable EntityLivingBase placer, @Nonnull EnumHand hand) {
		if (placer != null) { return getDefaultState().withProperty(ROTATION, placer.getHorizontalFacing().getHorizontalIndex()); }
		else { return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand); }
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,
								@Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileBorder) {
			TileBorder borderTile = (TileBorder) tile;
			TileBorder adjacent = null;
			for (EnumFacing facing : EnumFacing.VALUES) {
				adjacent = getAdjacentTile(world, pos.offset(facing));
				if (adjacent != null) break;
			}
			if (adjacent == null) {
				for (int i = 0; i < 3; i++) {
					BlockPos tempPos = i == 0 ? pos : i == 1 ? pos.up() : pos.down();
					if (i != 0) {
						for (int j = 0; j < 4; j++) {
							BlockPos p;
							switch (j) {
								case 1: p = tempPos.south(); break;
								case 2: p = tempPos.west(); break;
								case 3: p = tempPos.north(); break;
								default: p = tempPos.east(); break;
							}
							adjacent = getAdjacentTile(world, p);
							if (adjacent != null) break;
						}
						if (adjacent != null) break;
					}
					for (int j = 0; j < 4; j++) {
						BlockPos p;
						switch (j) {
							case 1: p = tempPos.south().east(); break;
							case 2: p = tempPos.south().west(); break;
							case 3: p = tempPos.north().east(); break;
							default: p = tempPos.north().west(); break;
						}
						adjacent = getAdjacentTile(world, p);
						if (adjacent != null) break;
					}
					if (adjacent != null) break;
				}
			}
			if (adjacent != null) {
				NBTTagCompound compound = new NBTTagCompound();
				adjacent.writeExtraNBT(compound);
				borderTile.readExtraNBT(compound);
			}
			borderTile.rotation = state.getValue(ROTATION);

			if (placer instanceof EntityPlayerMP && !world.isRemote) {
				if (adjacent == null) { SPacketGuiOpen.sendOpenGui((EntityPlayerMP) placer, EnumGuiType.Border, null, pos); }
				else {
					placer.sendMessage(Component.translatable("copy.settings.adjacent.block",
							TextFormatting.GRAY + "" + adjacent.getPos().getX(),
							TextFormatting.GRAY + "" + adjacent.getPos().getY(),
							TextFormatting.GRAY + "" + adjacent.getPos().getZ()).getParent());
				} // Copy
			}
		}
	}

	private TileBorder getAdjacentTile(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();
		if (tile instanceof TileBorder && block instanceof BlockBorder) {
			return (TileBorder) tile;
		}
		return null;
	}

}
