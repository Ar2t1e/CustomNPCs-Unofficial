package noppes.npcs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlockBuilder extends BlockInterface {

   public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

   public BlockBuilder() {
      super(Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(ROTATION);
   }

   public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

    @SuppressWarnings("all")
    @Deprecated
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
        if (!level.isClientSide) {
            ItemStack currentItem = player.getInventory().getSelected();
            if (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomBlocks.builder_item) {
                SPacketGuiOpen.sendOpenGui((ServerPlayer) player, EnumGuiType.BuilderBlock, null, pos);
            }

        }
        return InteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int var6 = Mth.floor((double)(Objects.requireNonNull(context.getPlayer()).getYRot() / 90.0F) + 0.5D) & 3;
        if (!context.getLevel().isClientSide) {
            SPacketGuiOpen.sendOpenGui((ServerPlayer) context.getPlayer(), EnumGuiType.BuilderBlock, null, context.getClickedPos());
        }
        return this.defaultBlockState().setValue(ROTATION, var6);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileBuilder(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, CustomBlocks.tile_builder, TileBuilder::tick);
    }
}
