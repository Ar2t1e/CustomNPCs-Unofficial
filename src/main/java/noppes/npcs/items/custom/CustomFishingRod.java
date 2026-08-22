package noppes.npcs.items.custom;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class CustomFishingRod extends ItemFishingRod implements ICustomElement {

	protected final NBTTagCompound nbtData;
	protected final int enchantability;
	protected final ItemStack repairItemStack;

	protected final int fishingLineColor;
	protected final ResourceLocation fishingLineTexture;

	public CustomFishingRod(NBTTagCompound nbtItem) {
		super();
		nbtData = nbtItem;
		setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
		setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));

		maxStackSize = nbtItem.hasKey("MaxStackSize", 3) ? nbtItem.getInteger("MaxStackSize") : 1;
		if (nbtItem.hasKey("RepairItem", 10)) { repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem")); }
		else { repairItemStack = ItemStack.EMPTY; }
		if (nbtItem.hasKey("Enchantability", 3)) { enchantability = nbtItem.getInteger("Enchantability"); }
		else { enchantability = 1; }
		if (nbtItem.getInteger("MaxStackDamage") > 1) { setMaxDamage(nbtItem.getInteger("MaxStackDamage")); }
		setCreativeTab(CustomTabs.ITEMS);

		fishingLineColor = nbtData.hasKey("FishingLineColor", 3) ? nbtData.getInteger("FishingLineColor") : 0;
		fishingLineTexture = nbtData.hasKey("FishingHookTexture", 8) ?
				new ResourceLocation(CustomNpcs.MODID, "textures/entity/" + nbtData.getString("FishingHookTexture") + ".png") : null;
	}

	@Override
	public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
		if (repairItemStack.isEmpty()) {
			return super.getIsRepairable(toRepair, repair);
		}
        if (net.minecraftforge.oredict.OreDictionary.itemMatches(repairItemStack, repair, false)) {
			return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}

	@Override
	public int getItemEnchantability() { return enchantability; }

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return new NBTWrapper(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 8;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

	public int getFishingLineColor() { return fishingLineColor; }

	public ResourceLocation getFishingHookTexture() { return fishingLineTexture; }

}
