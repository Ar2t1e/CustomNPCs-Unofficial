package noppes.npcs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabNpcs extends CreativeTabs {

	public Item item;
	public int meta;

	public CreativeTabNpcs(String label) {
		super(label);
		item = Items.BOWL;
		meta = 0;
	}

	@Override
	public @Nonnull ItemStack getTabIconItem() { return new ItemStack(this.item, 1, this.meta); }

}
