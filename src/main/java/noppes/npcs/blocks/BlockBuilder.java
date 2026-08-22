package noppes.npcs.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTabs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nonnull;

public class BlockBuilder extends BlockInterface {

	public static PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);

	public BlockBuilder() {
		super(Material.ROCK);
		setName("npcbuilderblock");
		setHardness(5.0f);
		setResistance(10.0f);
		setCreativeTab(CustomTabs.TOOLS);
		setSoundType(SoundType.STONE);
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockBuilder.ROTATION);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileBuilder();
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(BlockBuilder.ROTATION);
	}

	@Override
	public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BlockBuilder.ROTATION, meta);
	}

	@Override
	public boolean onBlockActivated(@Nonnull World par1World, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if (par1World.isRemote) {
			return true;
		}
		ItemStack currentItem = player.inventory.getCurrentItem();
		if (currentItem.getItem() == CustomItems.wand
				|| currentItem.getItem() == Item.getItemFromBlock(CustomBlocks.builder)) {
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) player, EnumGuiType.BuilderBlock, null, pos);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase entity, @Nonnull ItemStack stack) {
		int var6 = MathHelper.floor(entity.rotationYaw / 90.0f + 0.5) & 0x3;
		world.setBlockState(pos, state.withProperty(BlockBuilder.ROTATION, var6), 2);
		if (entity instanceof EntityPlayerMP && !world.isRemote) {
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) entity, EnumGuiType.BuilderBlock, null, pos);
		}
	}

}
