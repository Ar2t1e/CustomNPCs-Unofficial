package noppes.npcs.roles;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobFarmer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class JobFarmer extends JobInterface implements MassBlockController.IMassBlock, IJobFarmer {

   public int chestMode = 1;
   protected final List<BlockPos> trackedBlocks = new ArrayList<>();
   protected ItemStack holding = ItemStack.EMPTY;
   protected BlockPos chest = null;
   protected BlockPos ripe = null;
   protected boolean waitingForBlocks = false;
   protected int blockTicks = 800;
   protected int walkTicks = 0;
   protected int ticks = 0;
   protected int range = 16;

   public JobFarmer(EntityNPCInterface npc) {
      super(npc);
      overrideMainHand = true;
      type = JobType.FARMER;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.FARMER;
      chestMode = compound.getInt("JobChestMode");
      holding = ItemStack.of(compound.getCompound("JobHolding"));
      blockTicks = 1100;
      // New from Unofficial (BetaZavr)
      if (compound.contains("Range", 3)) { setRange(compound.getInt("Range")); }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("JobChestMode", chestMode);
      if (!holding.isEmpty()) { compound.put("JobHolding", holding.save(new CompoundTag())); }
      // New from Unofficial (BetaZavr)
      compound.putInt("Range", range);
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
      if (!holding.isEmpty()) {
         if (chestMode == 0) { setHolding(ItemStack.EMPTY); }
         else if (chestMode == 1) {
            if (chest == null) {
               dropItem(holding);
               setHolding(ItemStack.EMPTY);
            }
            else { chest(); }
         }
         else if (chestMode == 2) {
            dropItem(holding);
            setHolding(ItemStack.EMPTY);
         }
         return false;
      }
      if (ripe != null) {
         pluck();
         return false;
      }
      if (!waitingForBlocks && blockTicks++ > 1200) {
         blockTicks = 0;
         waitingForBlocks = true;
         MassBlockController.Queue(this);
      }
      if (ticks++ < 100) { return false; }
      ticks = 0;
      return true;
   }

   @Override
   public boolean aiContinueExecute() { return false; }

   @Override
   public void aiUpdateTask() {
      if (npc != null) {
         Iterator<BlockPos> ite = trackedBlocks.iterator();
         while(ite.hasNext() && ripe == null) {
            BlockPos pos = ite.next();
            BlockState state = npc.level().getBlockState(pos);
            Block b = state.getBlock();
            if ((b instanceof CropBlock && ((CropBlock)b).isMaxAge(state) || b instanceof StemGrownBlock) &&
                    b.getLootTable() != BuiltInLootTables.EMPTY) { ripe = pos; }
            else { ite.remove(); }
         }
         npc.ais.returnToStart = ripe == null;
         if (ripe != null) {
            npc.getNavigation().stop();
            npc.getLookControl().setLookAt(ripe.getX(), ripe.getY(), ripe.getZ(), 10.0F, (float)npc.getMaxHeadXRot());
         }
      }
   }

   @Override
   public boolean isPlucking() { return ripe != null || !holding.isEmpty(); }

   @Override
   public EntityNPCInterface getNpc() { return npc; }

   @Override
   public void processed(List<BlockData> list) {
      trackedBlocks.clear();
      chest = null;
      for (BlockData data : list) {
         BlockEntity tile = npc != null ? npc.level().getBlockEntity(data.pos) : null;
         Block b = data.state.getBlock();
         if (!(tile instanceof RandomizableContainerBlockEntity)) {
            if ((b instanceof CropBlock || b instanceof StemBlock) && !trackedBlocks.contains(data.pos)) { trackedBlocks.add(data.pos); }
            if (b instanceof ChestBlock) {
               if (chest != null && npc != null && npc.distanceToSqr(chest.getCenter()) > npc.distanceToSqr(data.pos.getCenter())) { chest = data.pos; }
            }
         }
      }
      waitingForBlocks = false;
   }

   @Override
   public EnumSet<Flag> getFlags() { return EnumSet.of(Flag.MOVE); }

   public void setHolding(ItemStack item) {
      holding = item;
      if (npc != null) { npc.setJobData(itemToString(holding)); }
   }

   private void dropItem(ItemStack item) {
      if (npc != null) {
         ItemEntity entityItem = new ItemEntity(npc.level(), npc.getX(), npc.getY(), npc.getZ(), item);
         entityItem.setDefaultPickUpDelay();
         npc.level().addFreshEntity(entityItem);
      }
   }

   private void chest() {
      if (npc != null) {
         BlockPos pos = chest;
         npc.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
         npc.getLookControl().setLookAt(pos.getX(), pos.getY(), pos.getZ(), 10.0F, (float)npc.getMaxHeadXRot());
         if (npc.nearPosition(pos) || walkTicks++ > 400) {
            if (walkTicks < 400) { npc.swing(InteractionHand.MAIN_HAND); }
            npc.getNavigation().stop();
            ticks = 100;
            walkTicks = 0;
            BlockState state = npc.level().getBlockState(pos);
            BlockEntity tile = npc.level().getBlockEntity(pos);
            Container inventory = tile instanceof Container ? (Container)tile : null;
            if (state.getBlock() instanceof ChestBlock) {
               inventory = ChestBlock.getContainer((ChestBlock)state.getBlock(), state, npc.level(), pos, true);
            }
            if (inventory == null) { chest = null; }
            else {
               int i;
               for(i = 0; !holding.isEmpty() && i < inventory.getContainerSize(); ++i) { holding = mergeStack(inventory, i, holding); }
               for(i = 0; !holding.isEmpty() && i < inventory.getContainerSize(); ++i) {
                  ItemStack item = inventory.getItem(i);
                  if (item.isEmpty()) {
                     inventory.setItem(i, holding);
                     holding = ItemStack.EMPTY;
                  }
               }
               if (!holding.isEmpty()) {
                  dropItem(holding);
                  holding = ItemStack.EMPTY;
               }
            }
            setHolding(holding);
         }
      }
   }

   private ItemStack mergeStack(Container inventory, int slot, ItemStack item) {
      ItemStack item2 = inventory.getItem(slot);
      if (!NoppesUtilPlayer.compareItems(item, item2, false, false)) { return item; }
      int size = item2.getMaxStackSize() - item2.getCount();
      if (size >= item.getCount()) {
         item2.setCount(item2.getCount() + item.getCount());
         return ItemStack.EMPTY;
      }
      item2.setCount(item2.getMaxStackSize());
      item.setCount(item.getCount() - size);
      return item.isEmpty() ? ItemStack.EMPTY : item;
   }

   private void pluck() {
      if (npc != null) {
         BlockPos pos = ripe;
         npc.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
         npc.getLookControl().setLookAt(pos.getX(), pos.getY(), pos.getZ(), 10.0F, (float)npc.getMaxHeadXRot());
         if (npc.nearPosition(pos) || walkTicks++ > 400) {
            if (walkTicks > 400) {
               pos = NoppesUtilServer.getClosePos(pos, npc.level());
               npc.teleportTo((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D);
            }
            ripe = null;
            npc.getNavigation().stop();
            ticks = 90;
            walkTicks = 0;
            npc.swing(InteractionHand.MAIN_HAND);
            BlockState state = npc.level().getBlockState(pos);
            Block b = state.getBlock();
            if (b instanceof CropBlock crop && crop.isMaxAge(state)) {
               Item item = crop.getCloneItemStack(npc.level(), pos, state).getItem();
               Builder builder = (new Builder((ServerLevel)npc.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, npc.getMainHandItem()).withParameter(LootContextParams.BLOCK_STATE, state).withOptionalParameter(LootContextParams.BLOCK_ENTITY, npc.level().getBlockEntity(pos));
               LootTable loottable = Objects.requireNonNull(npc.getServer()).getLootData().getLootTable(b.getLootTable());
               List<ItemStack> l = loottable.getRandomItems(builder.create(LootContextParamSets.BLOCK));
               npc.level().setBlock(pos, crop.getStateForAge(0), 2);
               if (l.isEmpty()) { holding = ItemStack.EMPTY; }
               else if (l.size() == 1) { holding = l.get(0); }
               else {
                  List<ItemStack> fl = l.stream().filter((t) -> t.getItem() != item).collect(Collectors.toList());
                  if ((fl).isEmpty()) { fl = l; }
                  holding = (ItemStack)((List<?>)fl).get(npc.getRandom().nextInt(fl.size()));
               }
               holding.setCount(1);
            }
            if (b instanceof StemGrownBlock) {
               b = npc.level().getBlockState(pos).getBlock();
               npc.level().removeBlock(pos, false);
               holding = new ItemStack(b);
            }
            setHolding(holding);
         }
      }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public int getRange() { return range; }

   @Override
   public void setRange(int dist) { range = ValueUtil.correctInt(dist, 2, 32); }

   @Override
   public boolean isWorking() { return !trackedBlocks.isEmpty(); }

}
