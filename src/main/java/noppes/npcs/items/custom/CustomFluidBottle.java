package noppes.npcs.items.custom;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.fluids.CustomFluid;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CustomFluidBottle extends Item {

    protected final CustomFluid fluid;
    protected final int color;

    public CustomFluidBottle(CustomFluid fluidIn, NBTTagCompound nbtBlock) {
        fluid = fluidIn;
        color = fluidIn.getColor();
        setRegistryName(CustomNpcs.MODID, "custom_bottle_" + nbtBlock.getString("RegistryName"));
        setUnlocalizedName(Objects.requireNonNull(getRegistryName()).toString());
        setMaxStackSize(1);
        setCreativeTab(CustomTabs.BLOCKS);
    }

    @Override
    public @Nonnull EnumAction getItemUseAction(@Nonnull ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public @Nonnull ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

}