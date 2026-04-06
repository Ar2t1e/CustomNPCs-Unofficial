package noppes.npcs.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Block.class, priority = 502)
public interface IBlockMixin {

    @Mutable
    @Accessor
    void setBlockState(BlockStateContainer newBlockStateContainer);

}
