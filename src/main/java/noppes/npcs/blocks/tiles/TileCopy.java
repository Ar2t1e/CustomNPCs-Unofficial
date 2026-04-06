package noppes.npcs.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TileCopy extends BlockEntity {

   public short length = 10;
   public short width = 10;
   public short height = 10;
   public String name = "";

   public TileCopy(BlockPos pos, BlockState state) {
      super(CustomBlocks.tile_copy, pos, state);
   }

   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      this.length = compound.getShort("Length");
      this.width = compound.getShort("Width");
      this.height = compound.getShort("Height");
      this.name = compound.getString("Name");
   }

   public void saveAdditional(CompoundTag compound) {
      compound.putShort("Length", this.length);
      compound.putShort("Width", this.width);
      compound.putShort("Height", this.height);
      compound.putString("Name", this.name);
      super.saveAdditional(compound);
   }

   public void handleUpdateTag(CompoundTag compound) {
      this.length = compound.getShort("Length");
      this.width = compound.getShort("Width");
      this.height = compound.getShort("Height");
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.handleUpdateTag(Objects.requireNonNull(pkt.getTag()));
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public @NotNull CompoundTag getUpdateTag() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("x", this.worldPosition.getX());
      compound.putInt("y", this.worldPosition.getY());
      compound.putInt("z", this.worldPosition.getZ());
      compound.putShort("Length", this.length);
      compound.putShort("Width", this.width);
      compound.putShort("Height", this.height);
      return compound;
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.worldPosition.getX() + this.width + 1, this.worldPosition.getY() + this.height + 1, this.worldPosition.getZ() + this.length + 1);
   }

}
