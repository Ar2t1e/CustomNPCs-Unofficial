package noppes.npcs.items.custom;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomPotions;
import noppes.npcs.CustomTabs;
import noppes.npcs.potions.PotionData;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CustomItemLingeringPotion extends ItemLingeringPotion {

	public CustomItemLingeringPotion() {
		super();
		this.setRegistryName(new ResourceLocation("lingering_potion"));
		this.setUnlocalizedName("lingering_potion");
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (getCreativeTab() != null) {
			if (tab != CustomTabs.ITEMS && tab != CreativeTabs.SEARCH) { return; }
			for (PotionData data : CustomPotions.CUSTOMS.values()) {
				if (data.showInCreative()) {
					ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(this), data.POTION_TYPE);
					if (tab == CustomTabs.ITEMS && data.nbtData.hasKey("MaxStackSize", 3)) {
						int count = ValueUtil.correctInt(data.nbtData.getInteger("MaxStackSize"), 1, 64);
						stack.getItem().setMaxStackSize(count);
						stack.setCount(count);
					}
					items.add(stack);
				}
			}
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

}
