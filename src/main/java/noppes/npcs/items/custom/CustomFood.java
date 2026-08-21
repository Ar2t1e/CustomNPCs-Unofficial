package noppes.npcs.items.custom;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.mixin.item.IItemFoodMixin;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CustomFood extends ItemFood implements ICustomElement {

	protected final NBTTagCompound nbtData;

	public CustomFood(int amount, float saturation, boolean isWolfFood, NBTTagCompound nbtItem) {
		super(amount, saturation, isWolfFood);
		nbtData = nbtItem;
		setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
		setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));
		if (nbtItem.hasKey("UseDuration", 3)) { ((IItemFoodMixin) this).setItemUseDuration(ValueUtil.correctInt(nbtItem.getInteger("UseDuration"), 0, 1200)); }
		if (nbtItem.hasKey("PotionEffect", 10)) {
			NBTTagCompound potionEffect = nbtItem.getCompoundTag("PotionEffect");
			Potion potion = Potion.getPotionFromResourceLocation(potionEffect.getString("Potion"));
			if (potion != null) {
				PotionEffect effect = new PotionEffect(potion, potionEffect.getInteger("DurationTicks"),
						potionEffect.getInteger("Amplifier"), potionEffect.getBoolean("Ambient"),
						potionEffect.getBoolean("ShowParticles"));
				setPotionEffect(effect, potionEffect.getFloat("Probability"));
			}
		}
		if (nbtItem.hasKey("AlwaysEdible", 1) && nbtItem.getBoolean("AlwaysEdible")) { setAlwaysEdible(); }
		if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) { setFull3D(); }
		setCreativeTab(CustomTabs.ITEMS);
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack) { return itemUseDuration; }

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
		return 6;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
