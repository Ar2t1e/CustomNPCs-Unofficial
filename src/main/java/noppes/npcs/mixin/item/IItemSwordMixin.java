package noppes.npcs.mixin.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemSword.class, priority = 502)
public interface IItemSwordMixin {

    @Accessor("attackDamage") float getEntityDamage();

    @Mutable @Accessor("attackDamage") void setEntityDamage(float newAttackDamage);

    @Accessor Item.ToolMaterial getMaterial();

}
