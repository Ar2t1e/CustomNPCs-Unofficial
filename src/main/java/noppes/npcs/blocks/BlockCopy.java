package noppes.npcs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.NotNull;

public class BlockCopy extends BlockInterface {

   public BlockCopy() {
      super(Properties.copy(Blocks.BARRIER).sound(SoundType.STONE));
   }

   /** @deprecated */
   @Deprecated
   public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
      if (level.isClientSide) {
         return InteractionResult.PASS;
      } else {
         ItemStack currentItem = player.getInventory().getSelected();
         if (currentItem.getItem() == CustomItems.wand) {
            SPacketGuiOpen.sendOpenGui((ServerPlayer) player, EnumGuiType.CopyBlock, null, pos);
         }
         return InteractionResult.SUCCESS;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      if (!context.getLevel().isClientSide) {
         SPacketGuiOpen.sendOpenGui((ServerPlayer) context.getPlayer(), EnumGuiType.CopyBlock, null, context.getClickedPos());
      }

      return this.defaultBlockState();
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileCopy(pos, state);
   }

}
