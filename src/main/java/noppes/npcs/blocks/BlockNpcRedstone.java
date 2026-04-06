package noppes.npcs.blocks;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.NotNull;

public class BlockNpcRedstone extends BlockInterface {
   public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

   public BlockNpcRedstone() {
      super(Properties.copy(Blocks.STONE).lightLevel((state) -> 12).strength(50.0F, 2000.0F));
   }

   /** @deprecated */
   @Deprecated
   public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack currentItem = player.getInventory().getSelected();
         if (currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission((ServerPlayer) player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            SPacketGuiOpen.sendOpenGui((ServerPlayer) player, EnumGuiType.RedstoneBlock, null, pos);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void onPlace(@NotNull BlockState state, Level levelIn, @NotNull BlockPos pos, @NotNull BlockState stateNew, boolean bo) {
      levelIn.updateNeighborsAt(pos, this);
      levelIn.updateNeighborsAt(pos.below(), this);
      levelIn.updateNeighborsAt(pos.above(), this);
      levelIn.updateNeighborsAt(pos.west(), this);
      levelIn.updateNeighborsAt(pos.east(), this);
      levelIn.updateNeighborsAt(pos.south(), this);
      levelIn.updateNeighborsAt(pos.north(), this);
   }

   public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack item) {
      if (!level.isClientSide && entity instanceof ServerPlayer) {
         SPacketGuiOpen.sendOpenGui((ServerPlayer) entity, EnumGuiType.RedstoneBlock, null, pos);
      }

   }

   /** @deprecated */
   @Deprecated
   public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
      this.onPlace(state, level, pos, state, isMoving);
   }

   /** @deprecated */
   @Deprecated
   public int getSignal(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull Direction side) {
      return this.isActivated(state);
   }

   /** @deprecated */
   @Deprecated
   public int getDirectSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction side) {
      return this.isActivated(state);
   }

   /** @deprecated */
   @Deprecated
   public boolean isSignalSource(@NotNull BlockState state) {
      return true;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(ACTIVE);
   }

   public int isActivated(BlockState state) {
      return state.getValue(ACTIVE) ? 15 : 0;
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileRedstoneBlock(pos, state);
   }

   public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
      return createTickerHelper(type, CustomBlocks.tile_redstoneblock, TileRedstoneBlock::tick);
   }
}
