package noppes.npcs.mixin.world.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemEntity.class, priority = 499)
public interface ItemEntityMixin {

   @Accessor("pickupDelay")
   int pickupDelay();

   @Accessor("age")
   void age(int newAge);

}
