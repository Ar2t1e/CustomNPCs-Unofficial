package noppes.npcs.mixin.inv;

import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Slot.class, priority = 502)
public interface ISlotMixin {

    @Mutable
    @Accessor("xPos")
    void setX(int newX);

    @Mutable
    @Accessor("yPos")
    void setY(int newX);

}
