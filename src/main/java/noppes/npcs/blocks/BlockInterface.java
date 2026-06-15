package noppes.npcs.blocks;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;

public abstract class BlockInterface extends BaseEntityBlock implements EntityBlock {

   protected BlockInterface(Properties properties) { super(properties); }

}
