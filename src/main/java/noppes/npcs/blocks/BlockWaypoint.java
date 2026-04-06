package noppes.npcs.blocks;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.NotNull;

public class BlockWaypoint extends BlockInterface {

   public BlockWaypoint() {
      super(Properties.copy(Blocks.BARRIER).sound(SoundType.METAL));
   }

   /** @deprecated */
   @Deprecated
   public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
      if (level.isClientSide) {
         return InteractionResult.PASS;
      } else {
         ItemStack currentItem = player.getInventory().getSelected();
         if (currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission((ServerPlayer) player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            SPacketGuiOpen.sendOpenGui((ServerPlayer) player, EnumGuiType.Waypoint, null, pos);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack item) {
      if (!level.isClientSide && entity instanceof ServerPlayer sPlayer) {
         SPacketGuiOpen.sendOpenGui(sPlayer, EnumGuiType.Waypoint, null, pos);
      }
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileWaypoint(pos, state);
   }

   public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
      return createTickerHelper(type, CustomBlocks.tile_waypoint, TileWaypoint::tick);
   }

}
