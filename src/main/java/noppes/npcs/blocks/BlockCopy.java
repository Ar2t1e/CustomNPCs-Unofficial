package noppes.npcs.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTabs;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nonnull;

public class BlockCopy extends BlockInterface {

	public BlockCopy() {
		super(Material.ROCK);
		setName("npccopyblock");
		setHardness(5.0f);
		setResistance(10.0f);
		setCreativeTab(CustomTabs.TOOLS);
		setSoundType(SoundType.STONE);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileCopy();
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(@Nonnull IBlockState state) { return false; }

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

	@Override
	public boolean onBlockActivated(@Nonnull World par1World, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if (par1World.isRemote) {
			return true;
		}
		ItemStack currentItem = player.inventory.getCurrentItem();
		if (currentItem.getItem() == CustomItems.wand) {
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) player, EnumGuiType.CopyBlock, null, pos);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase entity, @Nonnull ItemStack stack) {
		if (entity instanceof EntityPlayerMP && !world.isRemote) {
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) entity, EnumGuiType.CopyBlock, null, pos);
		}
	}
}
