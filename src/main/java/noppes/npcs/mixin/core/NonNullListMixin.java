package noppes.npcs.mixin.core;

import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = NonNullList.class, priority = 502)
public interface NonNullListMixin<E> {

    @Accessor("delegate")
    List<E> getList();

    @Mutable
    @Accessor("delegate")
    void setList(List<E> newList);

}
