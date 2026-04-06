package noppes.npcs.blocks;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.NotNull;

public class BlockBorder extends BlockInterface {

   public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

   public BlockBorder() {
      super(Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(ROTATION);
   }

   /** @deprecated */
   @Deprecated
   public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
      ItemStack currentItem = player.getInventory().getSelected();
      if (!level.isClientSide && currentItem.getItem() == CustomItems.wand) {
         SPacketGuiOpen.sendOpenGui((ServerPlayer) player, EnumGuiType.Border, null, pos);
         return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      if (context.getPlayer() != null) {
         int l = Mth.floor((double)(context.getPlayer().getYRot() * 4.0F / 360.0F) + 0.5D) & 3;
         return this.defaultBlockState().setValue(ROTATION, l);
      } else {
         return super.getStateForPlacement(context);
      }
   }

   public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack item) {
      TileBorder tile = (TileBorder)level.getBlockEntity(pos);
      if (tile == null) { return; }
      TileBorder adjacent = this.getTile(level, pos.west());
      if (adjacent == null) { adjacent = this.getTile(level, pos.south()); }
      if (adjacent == null) { adjacent = this.getTile(level, pos.north()); }
      if (adjacent == null) { adjacent = this.getTile(level, pos.east()); }
      if (adjacent != null) {
         CompoundTag compound = new CompoundTag();
         adjacent.writeExtraNBT(compound);
         tile.readExtraNBT(compound);
      }
      tile.rotation = state.getValue(ROTATION);
      if (!level.isClientSide && entity instanceof ServerPlayer player) {
         SPacketGuiOpen.sendOpenGui(player, EnumGuiType.Border, null, pos);
      }

   }

   private TileBorder getTile(Level level, BlockPos pos) {
      BlockEntity tile = level.getBlockEntity(pos);
      return tile instanceof TileBorder ? (TileBorder)tile : null;
   }

   public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileBorder(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
      return createTickerHelper(type, CustomBlocks.tile_border, TileBorder::tick);
   }
}
