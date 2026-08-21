package noppes.npcs.items.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.mixin.item.IItemToolMixin;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class CustomShovel extends ItemSpade implements ICustomElement {

    protected final Multimap<String, AttributeModifier> defaultModifiers = HashMultimap.create();
    protected final @Nonnull NBTTagCompound nbtData;

    protected Material collectionMaterial = null;
    protected float speedCollectionMaterial = 1.0f;
    protected ItemStack repairItemStack;
    protected int enchantability = 0;
    protected int harvestLevel = 0;

    public CustomShovel(float attackDamageIn, float attackSpeedIn, Item.ToolMaterial materialIn, Set<Block> effectiveBlocksIn, @Nonnull NBTTagCompound nbtItem) {
        super(materialIn);
        nbtData = nbtItem;

        setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
        setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));

        Set<Block> effectiveBlocks = ((IItemToolMixin) this).getEffectiveBlocks();
        effectiveBlocks.clear();
        effectiveBlocks.addAll(effectiveBlocksIn);
        attackDamage = attackDamageIn;
        attackSpeed = attackSpeedIn;

        if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) { setFull3D(); }
        if (nbtItem.getInteger("MaxStackDamage") > 1) { setMaxDamage(nbtItem.getInteger("MaxStackDamage")); }
        if (nbtItem.hasKey("CollectionMaterial", 10)) {
            collectionMaterial = CustomItem.getMaterial(nbtItem.getCompoundTag("collectionMaterial").getString("Material"));
            speedCollectionMaterial = nbtItem.getCompoundTag("collectionMaterial").getFloat("Speed");
        }
        if (nbtItem.hasKey("Efficiency", 5)) { efficiency = nbtItem.getFloat("Efficiency"); }
        if (nbtItem.hasKey("RepairItem", 10)) { repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem")); }
        else { repairItemStack = materialIn.getRepairItemStack(); }
        if (nbtItem.hasKey("Enchantability", 3)) { enchantability = nbtItem.getInteger("Enchantability"); }
        if (nbtItem.hasKey("HarvestLevel", 3)) { harvestLevel = nbtItem.getInteger("HarvestLevel"); }
        if (nbtItem.hasKey("ToolClass", 8)) { ((IItemToolMixin) this).setToolClass("axe"); }
        setCreativeTab(CustomTabs.ITEMS);
        defaultModifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
        defaultModifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
    }

    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
        if (state.getMaterial() == collectionMaterial) { return speedCollectionMaterial; }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public int getHarvestLevel(@Nonnull ItemStack stack, @Nonnull String toolClass, @Nullable net.minecraft.entity.player.EntityPlayer player, @Nullable IBlockState blockState) {
        if (harvestLevel > -1) { return harvestLevel; }
        return super.getHarvestLevel(stack, toolClass, player, blockState);
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        ItemStack mat = repairItemStack;
        if (repairItemStack.isEmpty()) { mat = toolMaterial.getRepairItemStack(); }
        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) { return true; }
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public @Nonnull Multimap<String, AttributeModifier> getItemAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot) {
        return equipmentSlot == EntityEquipmentSlot.MAINHAND ? defaultModifiers : HashMultimap.create();
    }

    @Override
    public int getItemEnchantability() {
        if (enchantability > 0) { return enchantability; }
        return super.getItemEnchantability();
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
            items.add(new ItemStack(this));
            if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() { return bFull3D; }

    @Override
    public String getCustomName() { return nbtData.getString("RegistryName"); }

    @Override
    public INbt getCustomNbt() { return new NBTWrapper(nbtData); }

    @Override
    public int getElementType() {
        if (nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
        return 2;
    }

    @Override
    public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
