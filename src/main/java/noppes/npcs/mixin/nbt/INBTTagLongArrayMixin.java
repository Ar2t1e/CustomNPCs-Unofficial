package noppes.npcs.mixin.nbt;

import net.minecraft.nbt.NBTTagLongArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NBTTagLongArray.class, priority = 502)
public interface INBTTagLongArrayMixin {

    @Accessor
    long[] getData();

}
