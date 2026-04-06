package noppes.npcs.mixin.core;

import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = NonNullList.class, priority = 502)
public interface INonNullListMixin<E> {

    @Accessor List<E> getList();

    @Mutable @Accessor void setList(List<E> newList);

}
