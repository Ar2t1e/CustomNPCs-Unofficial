package noppes.npcs.roles;

import java.util.Objects;
import java.util.Stack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobBuilder;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

public class JobBuilder extends JobInterface implements IJobBuilder {

   public TileBuilder build = null;
   protected BlockPos possibleBuildPos = null;
   protected Stack<BlockData> placingList = null;
   protected BlockData placing = null;
   protected int tryTicks = 0;
   protected int ticks = 0;

   public JobBuilder(EntityNPCInterface npc) {
      super(npc);
      overrideMainHand = true;
      type = JobType.BUILDER;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.BUILDER;
      if (compound.contains("BuildX")) {
         possibleBuildPos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
      }
      if (npc != null) {
         if (possibleBuildPos != null && compound.contains("Placing")) {
            Stack<BlockData> placing = new Stack<>();
            ListTag list = compound.getList("Placing", 10);
            for(int i = 0; i < list.size(); ++i) {
               placing.add(BlockData.getData(npc.level(), list.getCompound(i)));
            }
            placingList = placing;
         }
         npc.ais.doorInteract = 1;
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      if (build != null) {
         compound.putInt("BuildX", build.getBlockPos().getX());
         compound.putInt("BuildY", build.getBlockPos().getY());
         compound.putInt("BuildZ", build.getBlockPos().getZ());
         if (placingList != null && !placingList.isEmpty()) {
            ListTag list = new ListTag();
            for (BlockData data : placingList) { list.add(data.getNBT()); }
            if (placing != null) { list.add(placing.getNBT()); }
            compound.put("Placing", list);
         }
      }
      return compound;
   }

   @Override
   public IItemStack getMainhand() {
      if (npc != null) {
         String name = npc.getJobData();
         ItemStack item = stringToItem(name);
         return item.isEmpty() ? npc.inventory.weapons.get(0) : Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(item);
      }
      return ItemStackWrapper.AIR;
   }

   @Override
   public boolean aiShouldExecute() {
      if (possibleBuildPos != null && npc != null) {
         BlockEntity tile = npc.level().getBlockEntity(possibleBuildPos);
         if (tile instanceof TileBuilder) { build = (TileBuilder)tile; }
         else { placingList.clear(); }
         possibleBuildPos = null;
      }
      return build != null;
   }

   @Override
   public void aiUpdateTask() {
      if (npc != null) {
         if ((!build.finished || placingList != null) && build.enabled && !build.isRemoved()) {
            if (ticks++ >= 10) {
               ticks = 0;
               if ((placingList == null || placingList.isEmpty()) && placing == null) {
                  placingList = build.getBlock();
                  npc.setJobData("");
               }
               else {
                  if (placing == null) {
                     placing = placingList.pop();
                     if (placing.state.getBlock() == Blocks.STRUCTURE_VOID) {
                        placing = null;
                        return;
                     }
                     tryTicks = 0;
                     npc.setJobData(blockToString(placing));
                  }
                  npc.getNavigation().moveTo(placing.pos.getX(), placing.pos.getY() + 1, placing.pos.getZ(), 1.0D);
                  if (tryTicks++ > 40 || npc.nearPosition(placing.pos)) {
                     BlockPos blockPos = placing.pos;
                     placeBlock();
                     if (tryTicks > 40) {
                        blockPos = NoppesUtilServer.getClosePos(blockPos, npc.level());
                        npc.teleportTo((double)blockPos.getX() + 0.5D, blockPos.getY(), (double)blockPos.getZ() + 0.5D);
                     }
                  }
               }
            }
         }
         else {
            build = null;
            npc.getNavigation().moveTo(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), 1.0D);
         }
      }
   }

   @Override
   public void stop() { reset(); }

   @Override
   public void reset() {
      build = null;
      if (npc != null) { npc.setJobData(""); }
   }

   @Override
   public boolean isBuilding() { return build != null && build.enabled && !build.finished && build.started; }

   private String blockToString(BlockData data) {
      return data.state.getBlock() == Blocks.AIR ? Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.IRON_PICKAXE)).toString() : itemToString(data.getStack());
   }

   public void placeBlock() {
      if (placing != null && npc != null) {
         npc.getNavigation().stop();
         npc.swing(InteractionHand.MAIN_HAND);
         npc.level().setBlock(placing.pos, placing.state, 2);
         if (placing.state.getBlock() instanceof EntityBlock && placing.tile != null) {
            BlockEntity tile = npc.level().getBlockEntity(placing.pos);
            if (tile != null) {
               try { tile.load(placing.tile); } catch (Exception e) { LogWriter.error(e); }
            }
         }
         placing = null;
      }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isWorking() { return build != null && !build.finished && placingList != null && build.enabled && !build.isRemoved(); }

}
