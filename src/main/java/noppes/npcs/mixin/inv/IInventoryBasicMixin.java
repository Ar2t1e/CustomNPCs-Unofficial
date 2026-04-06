package noppes.npcs.mixin.inv;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InventoryBasic.class, priority = 502)
public interface IInventoryBasicMixin {

    @Accessor("inventoryContents")
    NonNullList<ItemStack> getItems();

    @Accessor("slotsCount")
    int getSize();

    @Mutable
    @Accessor("slotsCount")
    void setSize(int newSize);

}
