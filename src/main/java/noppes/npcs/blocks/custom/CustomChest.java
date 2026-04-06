package noppes.npcs.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import noppes.npcs.CustomBlocks;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

// ChestBlock
public class CustomChest extends BlockInterface implements SimpleWaterloggedBlock, ICustomElement {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    protected static final int AABB_OFFSET = 1;
    protected static final int AABB_HEIGHT = 14;
    protected static final VoxelShape CHEST_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static VoxelShape SHAPE = Shapes.block();

    protected final @Nonnull CompoundTag nbtData;
    public final boolean isChest;

    public CustomChest(@Nonnull Properties properties, @Nonnull CompoundTag nbtBlock) {
        super(properties);
        nbtData = nbtBlock;
        isChest = nbtBlock.contains("IsChest", 1) && nbtBlock.getBoolean("IsChest");

        if (nbtBlock.get("AABB") instanceof ListTag tagList && tagList.getElementType() == (byte) 6 && tagList.size() > 5) {
            SHAPE = Shapes.create(new AABB(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2),
                    tagList.getDouble(3), tagList.getDouble(4), tagList.getDouble(5)));
        }
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return nbtData.contains("IsLadder", 1) ? nbtData.getBoolean("IsLadder") : super.isLadder(state, level, pos, entity);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, @javax.annotation.Nullable EntityType<?> entityType) {
        return nbtData.contains("IsValidSpawn", 1) ? nbtData.getBoolean("IsValidSpawn") : super.isValidSpawn(state, level, pos, type, entityType);
    }

    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        if (isChest) { return RenderShape.ENTITYBLOCK_ANIMATED; }
        return RenderShape.MODEL;
    }

    @Override
    public @Nonnull BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState nextStage, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos nextPos) {
        if (state.getValue(WATERLOGGED)) { level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level)); }
        return state;
    }

    @Override
    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        if (isChest) { return CHEST_SHAPE; }
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = context.isSecondaryUseActive();
        Direction fase = context.getClickedFace();
        if (fase.getAxis().isHorizontal() && flag) {
            Direction nextFase = candidatePartnerFacing(context, fase.getOpposite());
            if (nextFase != null && nextFase.getAxis() != fase.getAxis()) { direction = nextFase; }
        }
        return defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @Nonnull FluidState getFluidState(@Nonnull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    private @Nullable Direction candidatePartnerFacing(BlockPlaceContext context, Direction direction) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
        return blockstate.is(this) ? blockstate.getValue(FACING) : null;
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack item) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CustomTileEntityChest tile) {
            if (item.hasTag() && item.getTag() != null  && item.getTag().contains("BlockEntityTag")) { tile.load(item.getTag().getCompound("BlockEntityTag")); }
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState nextState, boolean isMoving) {
        if (!state.is(nextState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container container) {
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, nextState, isMoving);
        }
    }

    @Override
    public @Nonnull InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (level.isClientSide()) { return InteractionResult.SUCCESS; }
        MenuProvider menuprovider = getMenuProvider(state, level, pos);
        if (menuprovider != null) {
            player.openMenu(menuprovider);
            player.awardStat(getOpenChestStat());
            PiglinAi.angerNearbyPiglins(player, true);
        }
        return InteractionResult.CONSUME;
    }

    protected Stat<ResourceLocation> getOpenChestStat() { return Stats.CUSTOM.get(Stats.OPEN_CHEST); }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new CustomTileEntityChest(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return level.isClientSide() ? createTickerHelper(type, CustomBlocks.tile_custom_chest, CustomTileEntityChest::lidAnimateTick) : null;
    }

    @Override
    public @Nonnull BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @Nonnull BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
        stateDefinition.add(FACING, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull PathComputationType type) { return false; }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource rndSource) {
        if (level.getBlockEntity(pos) instanceof CustomTileEntityChest tile) { tile.recheckOpen(); }
    }

    @Override
    public String getCustomName() { return nbtData.getString("RegistryName"); }

    @Override
    public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

    @Override
    public int getElementType() {
        if (nbtData.contains("BlockType", 1)) { return nbtData.getByte("BlockType"); }
        return 2;
    }

    @Override
    public boolean showInCreative() { return !nbtData.contains("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
