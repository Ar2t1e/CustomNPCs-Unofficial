package noppes.npcs.mixin.world.inventory;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Slot.class, priority = 502)
public interface ISlotMixin {

    @Mutable
    @Accessor("x")
    void setX(int newX);

    @Mutable
    @Accessor("y")
    void setY(int newX);

}
