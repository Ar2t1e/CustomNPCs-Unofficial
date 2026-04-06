package noppes.npcs.blocks.tiles;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomBlocks;
import noppes.npcs.NBTTags;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.schematics.SchematicWrapper;

import javax.annotation.Nonnull;

public class TileBuilder extends BlockEntity {

   protected final Stack<Integer> positions = new Stack<>();
   protected final Stack<Integer> positionsSecond = new Stack<>();
   protected SchematicWrapper schematic = null;
   protected boolean show = false;
   protected int ticks = 20;
   public Availability availability = new Availability();
   public int rotation = 0;
   public int yOffset = 0;
   public boolean enabled = false;
   public boolean started = false;
   public boolean finished = false;

   public TileBuilder(BlockPos pos, BlockState state) {
      super(CustomBlocks.tile_builder, pos, state);
   }

   @Override
   public void load(@Nonnull CompoundTag compound) {
      super.load(compound);
      positions.clear();
      positions.addAll(NBTTags.getIntegerList(compound.getList("Positions", 10)));
      positionsSecond.clear();
      positionsSecond.addAll(NBTTags.getIntegerList(compound.getList("PositionsSecond", 10)));
      loadPartNBT(compound);
   }

   public void loadPartNBT(CompoundTag compound) {
      if (compound.contains("SchematicName")) {
         schematic = SchematicController.Instance.load(compound.getString("SchematicName"));
      }
      rotation = compound.getInt("Rotation");
      yOffset = compound.getInt("YOffset");
      enabled = compound.getBoolean("Enabled");
      started = compound.getBoolean("Started");
      finished = compound.getBoolean("Finished");
      show = compound.getBoolean("IsShow");
      availability.load(compound.getCompound("Availability"));
      if (show && schematic != null) { ClientEventHandler.addShowThis(this); }
   }

   @Override
   public void saveAdditional(@Nonnull CompoundTag compound) {
      super.saveAdditional(compound);
      compound.put("Positions", NBTTags.nbtIntegerCollection(new ArrayList<>(positions)));
      compound.put("PositionsSecond", NBTTags.nbtIntegerCollection(new ArrayList<>(positionsSecond)));
      savePartNBT(compound);
   }

   public CompoundTag savePartNBT(CompoundTag compound) {
      if (schematic != null) { compound.putString("SchematicName", schematic.schema.getName()); }
      compound.putInt("Rotation", rotation);
      compound.putInt("YOffset", yOffset);
      compound.putBoolean("Enabled", enabled);
      compound.putBoolean("Started", started);
      compound.putBoolean("Finished", finished);
      compound.putBoolean("IsShow", show);
      compound.put("Availability", availability.save(new CompoundTag()));
      return compound;
   }

   @Override
   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) { handleUpdateTag(Objects.requireNonNull(pkt.getTag())); }

   @Override
   public void handleUpdateTag(CompoundTag compound) { loadPartNBT(compound); }

   @Override
   public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

   public @Nonnull CompoundTag getUpdateTag() { return savePartNBT(new CompoundTag()); }

   @OnlyIn(Dist.CLIENT)
   public void setDrawSchematic(SchematicWrapper schematics, boolean showIn) {
      schematic = schematics;
      show = showIn;
      if (show && schematic != null) { ClientEventHandler.addShowThis(this); }
   }

   public void setSchematic(SchematicWrapper schematics) {
      schematic = schematics;
      if (schematics == null) {
         positions.clear();
         positionsSecond.clear();
      }
      else {
         positions.clear();
         for(int y = 0; y < schematics.schema.getHeight(); ++y) {
            int z;
            int x;
            for(z = 0; z < schematics.schema.getLength() / 2; ++z) {
               for(x = 0; x < schematics.schema.getWidth() / 2; ++x) { positions.add(0, xyzToIndex(x, y, z)); }
            }
            for(z = 0; z < schematics.schema.getLength() / 2; ++z) {
               for(x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) { positions.add(0, xyzToIndex(x, y, z)); }
            }
            for(z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
               for(x = 0; x < schematics.schema.getWidth() / 2; ++x) { positions.add(0, xyzToIndex(x, y, z)); }
            }
            for(z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
               for(x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) { positions.add(0, xyzToIndex(x, y, z)); }
            }
         }
         positionsSecond.clear();
      }
   }

   public int xyzToIndex(int x, int y, int z) {
      return (y * schematic.schema.getLength() + z) * schematic.schema.getWidth() + x;
   }

   public SchematicWrapper getSchematic() { return schematic; }

   public boolean hasSchematic() { return schematic != null; }

   public static void tick(Level level, BlockPos pos, BlockState ignoredState, TileBuilder tile) {
      if (!level.isClientSide && tile.hasSchematic() && !tile.finished) {
         --tile.ticks;
         if (tile.ticks <= 0) {
            tile.ticks = 200;
            if (tile.positions.isEmpty() && tile.positionsSecond.isEmpty()) { tile.finished = true; }
            else {
               if (!tile.started) {
                  for (Player player : tile.getPlayerList()) {
                     if (tile.availability.isAvailable(player)) {
                        tile.started = true;
                        break;
                     }
                  }
                  if (!tile.started) { return; }
               }
               List<EntityNPCInterface> list = level.getEntitiesOfClass(EntityNPCInterface.class, (new AABB(pos, pos)).inflate(32.0D, 32.0D, 32.0D));
               for (EntityNPCInterface npc : list) {
                  if (npc.job.getType() == 10) {
                     JobBuilder job = (JobBuilder) npc.job;
                     if (job.build == null) { job.build = tile; }
                  }
               }
            }
         }
      }
   }

   private List<Player> getPlayerList() {
      if (level == null) { return Collections.emptyList(); }
      return level.getEntitiesOfClass(Player.class, (new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1)).inflate(10.0D, 10.0D, 10.0D));
   }

   public Stack<BlockData> getBlock() {
      if (enabled && !finished && hasSchematic()) {
         boolean bo = positions.isEmpty();
         Stack<BlockData> list = new Stack<>();
         int size = schematic.schema.getWidth() * schematic.schema.getLength() / 4;
         if (size > 30) { size = 30; }
         for(int i = 0; i < size; ++i) {
            if (positions.isEmpty() && !bo || positionsSecond.isEmpty() && bo) { return list; }
            int pos = bo ? positionsSecond.pop() : positions.pop();
            if (pos < schematic.size) {
               int x = pos % schematic.schema.getWidth();
               int z = (pos - x) / schematic.schema.getWidth() % schematic.schema.getLength();
               int y = ((pos - x) / schematic.schema.getWidth() - z) / schematic.schema.getLength();
               BlockState state = schematic.schema.getBlockState(x, y, z);
               if (!state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) && !bo && state.getBlock() != Blocks.AIR) {
                  positionsSecond.add(0, pos);
               }
               else {
                  BlockPos blockPos = getBlockPos().offset(1, yOffset, 1).offset(schematic.rotatePos(x, y, z, rotation));
                  if (level != null) {
                     BlockState original = level.getBlockState(blockPos);
                     if (Block.getId(state) != Block.getId(original)) {
                        state = SchematicWrapper.rotationState(state, rotation);
                        CompoundTag tile = null;
                        if (state.getBlock() instanceof EntityBlock) { tile = schematic.getBlockEntity(x, y, z, blockPos); }
                        list.add(0, new BlockData(blockPos, state, tile));
                     }
                  }
               }
            }
         }
         return list;
      }
      return null;
   }

   @Override
   public @Nonnull AABB getRenderBoundingBox() {
      return schematic == null ? super.getRenderBoundingBox() : new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + schematic.schema.getWidth() + 1, worldPosition.getY() + schematic.schema.getHeight() + 1, worldPosition.getZ() + schematic.schema.getLength() + 1);
   }

   public boolean getShow() { return show; }

}
