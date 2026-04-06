package noppes.npcs.mixin.item;

import net.minecraft.item.ItemFood;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemFood.class, priority = 502)
public interface IItemFoodMixin {

    @Mutable
    @Accessor
    void setItemUseDuration(int newItemUseDuration);

}
