package noppes.npcs.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import org.jetbrains.annotations.NotNull;

public class TileDoor extends TileNpcEntity {

   public int tickCount = 0;
   public Block blockModel;
   public boolean needsClientUpdate;

   public TileDoor(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
      super(p_i48289_1_, pos, state);
      this.blockModel = CustomBlocks.scripted_door;
      this.needsClientUpdate = false;
   }

   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      this.setDoorNBT(compound);
   }

   public void setDoorNBT(CompoundTag compound) {
      this.blockModel = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(compound.getString("ScriptDoorBlockModel")));
      if (this.blockModel == null || !(this.blockModel instanceof DoorBlock)) { blockModel = CustomBlocks.scripted_door; }
   }

   public void saveAdditional(@NotNull CompoundTag compound) {
      this.getDoorNBT(compound);
      super.saveAdditional(compound);
   }

   public void getDoorNBT(CompoundTag compound) {
      ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(blockModel);
      if (registryName == null) { registryName = new ResourceLocation(CustomNpcs.MODID, "npcscripteddoortool"); }
      compound.putString("ScriptDoorBlockModel", registryName.toString());
   }

   public void setItemModel(Block block) {
      if (!(block instanceof DoorBlock)) {
         block = CustomBlocks.scripted_door;
      }
      if (this.blockModel != block) {
         this.blockModel = block;
         this.needsClientUpdate = true;
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, TileDoor tile) {
      ++tile.tickCount;
      if (tile.tickCount >= 10) {
         tile.tickCount = 0;
         if (tile.needsClientUpdate) {
            tile.setChanged();
            level.setBlockAndUpdate(pos, state);
            tile.needsClientUpdate = false;
         }
      }

   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.handleUpdateTag(pkt.getTag());
   }

   public void handleUpdateTag(CompoundTag compound) {
      this.setDoorNBT(compound);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public @NotNull CompoundTag getUpdateTag() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("x", this.worldPosition.getX());
      compound.putInt("y", this.worldPosition.getY());
      compound.putInt("z", this.worldPosition.getZ());
      this.getDoorNBT(compound);
      return compound;
   }

}
