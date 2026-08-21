package noppes.npcs.mixin.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntity.class, priority = 502)
public interface ITileEntityMixin {

    @Accessor void setBlockType(Block newBlockType);

}
