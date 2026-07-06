package noppes.npcs.mixin.block.material;

import net.minecraft.block.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MapColor.class, priority = 502)
public interface IMapColorMixin {

    @Invoker("<init>")
    static MapColor create(int index, int color) { throw new AssertionError(); }


}
