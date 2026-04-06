package noppes.npcs.roles;

import java.util.EnumSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nullable;

public abstract class JobInterface implements INPCJob {

   public static final JobInterface NONE = new JobInterface(null) {

      @Override
      public CompoundTag save(CompoundTag compound) { return compound; }

      @Override
      public void load(CompoundTag compound) { }

      @Override
      public int getType() { return JobType.NONE.get(); }

      @Override
      public JobType getEnumType() { return JobType.NONE; }

   };

   public @Nullable EntityNPCInterface npc;
   public boolean overrideMainHand = false;
   public boolean overrideOffHand = false;

   // New from Unofficial (BetaZavr)
   public JobType type = JobType.NONE;

   public JobInterface(@Nullable EntityNPCInterface npcIn) { npc = npcIn; }

   public void killed() { }

   public void delete() { }

   public void aiDeathExecute(Entity attackingEntity) { }

   public boolean aiShouldExecute() { return false; }

   public boolean aiContinueExecute() { return aiShouldExecute(); }

   public void aiStartExecuting() { }

   public void aiUpdateTask() { }

   public void reset() { }

   public void stop() { }

   public IItemStack getMainhand() {
      return null;
   }

   public IItemStack getOffhand() {
      return null;
   }

   public boolean isFollowing() {
      return false;
   }

   public EnumSet<Flag> getFlags() {
      return EnumSet.noneOf(Flag.class);
   }

   public ItemStack stringToItem(String s) {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(s));
      return s.isEmpty() || item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   public String itemToString(ItemStack item) {
      ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item.getItem());
      if (registryName == null) { registryName = new ResourceLocation("minecraft", "air"); }
      return !item.isEmpty() ? registryName.toString() : "";
   }

   // New from Unofficial (BetaZavr)
   public void load(CompoundTag compound) { type = JobType.get(compound.getInt("Type")); }

   public CompoundTag save(CompoundTag compound) {
      compound.putInt("Type", type.get());
      return compound;
   }

   @Override
   public boolean isWorking() { return false; }

   @Override
   public int getType() { return type.get(); }

   public JobType getEnumType() { return type; }

   public void interact(Player player) { }

}
