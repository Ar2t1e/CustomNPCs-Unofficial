package noppes.npcs.schematics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.util.CustomNPCsScheduler;

// New from Unofficial (BetaZavr)
public class SchematicBlockData {

    public BlockPos pos;
    public BlockState state;
    public CompoundTag nbtTile;
    public Level level;
    public int id = 0;

    public SchematicBlockData(Level levelIn, BlockState stateIn, BlockPos posIn) {
        level = levelIn;
        pos = posIn;
        state = stateIn;
        nbtTile = null;
        if (stateIn.getBlock() instanceof EntityBlock && level != null) {
            BlockEntity tile = level.getBlockEntity(posIn);
            if (tile != null) { nbtTile = tile.saveWithFullMetadata(); }
        }
    }

    public SchematicBlockData(Level levelIn, ItemStack stack) {
        level = levelIn;
        pos = null;
        Block b = Block.byItem(stack.getItem());
        state = b.defaultBlockState();
        if (stack.getDamageValue() < b.getStateDefinition().getPossibleStates().size()) {
            state = b.getStateForPlacement(new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, stack,
                    new BlockHitResult(new Vec3(0.0f, 0.0f, 0.0f), Direction.DOWN, BlockPos.ZERO, false)));
        }
        nbtTile = null;
        if (stack.hasTag() && stack.getTag() != null) { nbtTile = stack.getTag().copy(); }
    }

    public void set(BlockPos pos) {
        if (level == null || pos == null || state == null) { return; }
        level.setBlock(pos, state, 2);
        if (nbtTile != null) {
            nbtTile.putInt("x", pos.getX());
            nbtTile.putInt("y", pos.getY());
            nbtTile.putInt("z", pos.getZ());
            CustomNPCsScheduler.runTack(() -> {
                BlockEntity tile = level.getBlockEntity(pos);
                if (tile == null && state.getBlock() instanceof EntityBlock entityBlock) { tile = entityBlock.newBlockEntity(pos, state); }
                if (tile != null) {
                    tile.load(nbtTile);
                    nbtTile.putInt("x", pos.getX());
                    nbtTile.putInt("y", pos.getY());
                    nbtTile.putInt("z", pos.getZ());
                }
            }, 200);
        }
    }

    @Override
    public String toString() {
        return "SchematicBlockData [ ID:" + id + "; state:" + state + "," + "; pos:" + pos
                + "; hasNbt:" + (nbtTile != null) + " ]";
    }

}
