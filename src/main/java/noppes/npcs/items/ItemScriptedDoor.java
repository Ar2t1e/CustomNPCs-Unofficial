package noppes.npcs.items;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;

import javax.annotation.Nonnull;

public class ItemScriptedDoor extends ItemDoor {

	public ItemScriptedDoor(Block block) {
		super(block);
		this.setRegistryName(CustomNpcs.MODID, "npcscripteddoortool");
		this.setUnlocalizedName("npcscripteddoortool");
		this.setFull3D();
		this.maxStackSize = 1;
		this.setCreativeTab(CustomTabs.TOOLS);
	}

	public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer playerIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		EnumActionResult res = super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
		if (res == EnumActionResult.SUCCESS && !worldIn.isRemote) {
			PlayerData.get(playerIn).scriptBlockPos = pos;
			SPacketGuiOpen.sendOpenGui((EntityPlayerMP) playerIn, EnumGuiType.ScriptDoor, null, pos.up());
			return EnumActionResult.SUCCESS;
		}
		return res;
	}

	public @Nonnull ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityLivingBase playerIn) {
		return stack;
	}
}
