package noppes.npcs.mixin.entity.item;

import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityItem.class, priority = 502)
public interface IEntityItemMixin {

    @Accessor int getAge();

    @Accessor void setAge(int newAge);

}
