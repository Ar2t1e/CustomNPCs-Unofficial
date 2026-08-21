package noppes.npcs.mixin.item;

import net.minecraft.item.ItemArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemArmor.class, priority = 502)
public interface IItemArmorMixin {

    @Mutable @Accessor("damageReduceAmount") void setDefense(int maxStDam);

    @Mutable @Accessor void setToughness(float newToughness);

}
