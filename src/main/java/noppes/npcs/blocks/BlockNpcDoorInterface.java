package noppes.npcs.blocks;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import org.jetbrains.annotations.NotNull;

public abstract class BlockNpcDoorInterface extends DoorBlock implements EntityBlock {

   public BlockNpcDoorInterface(Properties properties) {
      super(properties, BlockSetType.STONE);
   }

   /** @deprecated */
   @Deprecated
   public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
      super.onRemove(state, level, pos, newState, isMoving);
      level.removeBlockEntity(pos);
   }

   /** @deprecated */
   @Deprecated
   public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull Builder builder) {
      return Collections.emptyList();
   }

   public void playerDestroy(@NotNull Level level, Player playerIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack stack) {
      playerIn.awardStat(Stats.BLOCK_MINED.get(this));
      playerIn.causeFoodExhaustion(0.005F);
      dropResources(state, level, pos, blockEntity, playerIn, stack);
   }

}
