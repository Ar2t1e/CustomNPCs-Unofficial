package noppes.npcs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("all")
public class BlockMailbox extends BlockInterface {

   public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
   public final int type;

   public BlockMailbox(int type) {
      super(Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).strength(5.0F, 10.0F));
      this.type = type;
   }

   public @NotNull String getDescriptionId() {
      return "block." + CustomNpcs.MODID + ".npcmailbox";
   }

   /** @deprecated */
   @Deprecated
   public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos) {
      return Shapes.empty();
   }

   /** @deprecated */
   @Deprecated
   public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull PathComputationType pathType) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
      if (!level.isClientSide) {
         Packets.send((ServerPlayer)player, new PacketGuiOpen(EnumGuiType.PlayerMailbox, pos));
      }
      return InteractionResult.SUCCESS;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(ROTATION);
   }

   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      int l = Mth.floor((double)(Objects.requireNonNull(context.getPlayer()).getYRot() * 4.0F / 360.0F) + 0.5D) & 3;
      return this.defaultBlockState().setValue(ROTATION, l % 4);
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return (new TileMailbox(pos, state)).setModel(type);
   }

}
