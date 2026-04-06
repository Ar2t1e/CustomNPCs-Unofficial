package noppes.npcs.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = ItemTool.class, priority = 502)
public interface IItemToolMixin {

    @Mutable
    @Accessor(remap = false)
    void setToolClass(String toolClass);

    @Accessor
    Set<Block> getEffectiveBlocks();

}
