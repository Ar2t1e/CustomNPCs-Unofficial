package noppes.npcs.api.wrapper.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.wrapper.BlockWrapper;

public class DataBlock {

    public final Level level;
    public final BlockPos pos;
    public final BlockState state;

    public DataBlock(Level levelIn, BlockPos posIn, BlockState stateIn) {
        level = levelIn;
        pos = posIn;
        state = stateIn;
    }

    @SuppressWarnings("all")
    public IBlock getIBlock() { return BlockWrapper.createNew(level, pos, state); }

}