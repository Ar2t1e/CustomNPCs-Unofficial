package noppes.npcs.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CustomCreativeTabs extends CreativeTabs {

    public final ItemStack icon;

    public CustomCreativeTabs(String craftingCustomAnvilCategory, ItemStack iconIn) {
        super(craftingCustomAnvilCategory);
        CreativeTabs[] tmp = new CreativeTabs[CREATIVE_TAB_ARRAY.length - 1];
        boolean bo = false;
        for (int i = 0, j = 0; i < CREATIVE_TAB_ARRAY.length; i++) {
            if (CREATIVE_TAB_ARRAY[i] != this) {
                tmp[j++] = CREATIVE_TAB_ARRAY[i];
            }
            else { bo = true; }
        }
        if (bo) { CREATIVE_TAB_ARRAY = tmp; }
        icon = iconIn;
    }

    @SideOnly(Side.CLIENT)
    public @Nonnull ItemStack getTabIconItem() { return icon; }

}
