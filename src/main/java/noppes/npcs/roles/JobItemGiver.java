package noppes.npcs.roles;

import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.role.IJobItemGiver;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.entity.EntityNPCInterface;

public class JobItemGiver extends JobInterface implements IJobItemGiver {

   protected final List<Player> recentlyChecked = new ArrayList<>();
   protected List<Player> toCheck;
   protected int ticks = 10;

   public List<String> lines = new ArrayList<>();
   public Availability availability = new Availability();
   public NpcMiscInventory inventory = new NpcMiscInventory(9);
   public int cooldownType = 0; // 0:timer, 1:one, 2:rldaily
   public int givingMethod = 0; // 0:rnd, 1:all, 2:owned, 3:doesn't own, 4:chained
   public int cooldown = 10;
   public int itemGiverId = 0;

   public JobItemGiver(EntityNPCInterface npc) {
      super(npc);
      lines.add("Have these items {player}");
      type = JobType.ITEM_GIVER;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("igCooldownType", cooldownType);
      compound.putInt("igGivingMethod", givingMethod);
      compound.putInt("igCooldown", cooldown);
      compound.putInt("ItemGiverId", itemGiverId);
      compound.put("igLines", NBTTags.nbtStringList(lines));
      compound.put("igJobInventory", inventory.save());
      compound.put("igAvailability", availability.save(new CompoundTag()));
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.ITEM_GIVER;
      itemGiverId = compound.getInt("ItemGiverId");
      cooldownType = compound.getInt("igCooldownType");
      givingMethod = compound.getInt("igGivingMethod");
      cooldown = compound.getInt("igCooldown");
      lines = NBTTags.getStringList(compound.getList("igLines", 10));
      inventory.load(compound.getCompound("igJobInventory"));
      if (itemGiverId == 0 && GlobalDataController.instance != null) {
         itemGiverId = GlobalDataController.instance.incrementItemGiverId();
      }
      availability.load(compound.getCompound("igAvailability"));
   }

   private boolean giveItems(Player player) {
      PlayerItemGiverData data = PlayerData.get(player).itemgiverData;
      if (!canPlayerInteract(data)) { return false; }
      Vector<ItemStack> items = new Vector<>();
      Vector<ItemStack> toGive = new Vector<>();
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!stack.isEmpty()) { items.add(stack.copy()); }
      }
      if (!items.isEmpty()) {
         if (isAllGiver()) { toGive = items; }
         else if (isRemainingGiver()) {
            for (ItemStack stack : items) {
               if (!playerHasItem(player, stack.getItem())) { toGive.add(stack); }
            }
         }
         else if (isRandomGiver()) {
            int index = npc != null ? npc.level().random.nextInt(items.size()) : new Random().nextInt(items.size());
            toGive.add((items.get(index)).copy());
         }
         else if (isGiverWhenNotOwnedAny()) {
            boolean ownsItems = false;
            for (ItemStack iStack : items) {
               if (playerHasItem(player, iStack.getItem())) {
                  ownsItems = true;
                  break;
               }
            }
            if (ownsItems) { return false; }
            toGive = items;
         }
         else if (isChainedGiver()) {
            int itemIndex = data.getItemIndex(this);
            if (itemIndex > 0 && itemIndex < inventory.getContainerSize()) { toGive.add(inventory.getItem(itemIndex)); }
         }
         if (toGive.isEmpty()) { return false; }
         if (givePlayerItems(player, toGive)) {
            if (npc != null && !lines.isEmpty()) { npc.say(player, new Line(lines.get(npc.getRandom().nextInt(lines.size())))); }
            if (isDaily()) { data.setTime(this, getDay()); }
            else { data.setTime(this, System.currentTimeMillis()); }
            if (isChainedGiver()) {
               data.setItemIndex(this, (data.getItemIndex(this) + 1) % inventory.getContainerSize());
            }
            return true;
         }
      }
      return false;
   }

   private long getDay() { return npc == null ? 0L : (npc.level().getGameTime() / 24000L); }

   private boolean canPlayerInteract(PlayerItemGiverData data) {
      if (inventory.getContainerSize() == 0) { return false; }
      if (isOnTimer()) {
         return data.notInteractedBefore(this) || data.getTime(this) + cooldown * 1000L < System.currentTimeMillis();
      }
      if (isGiveOnce()) { return data.notInteractedBefore(this); }
      if (isDaily()) {
         return data.notInteractedBefore(this) || getDay() > data.getTime(this);
      }
      return false;
   }

   private boolean givePlayerItems(Player player, Vector<ItemStack> toGive) {
      if (toGive.isEmpty() || freeInventorySlots(player) < toGive.size()) { return false; }
      if (npc != null) {
         for (ItemStack is : toGive) { npc.givePlayerItem(player, is); }
      }
      return true;
   }

   private boolean playerHasItem(Player player, Item item) {
      for (ItemStack is : player.getInventory().items) {
         if (!is.isEmpty() && is.getItem() == item) { return true; }
      }
      for (ItemStack is : player.getInventory().armor) {
         if (!is.isEmpty() && is.getItem() == item) { return true; }
      }
      return false;
   }

   private int freeInventorySlots(Player player) {
      int i = 0;
      for (ItemStack is : player.getInventory().items) {
         if (NoppesUtilServer.isItemStackNull(is)) { ++i; }
      }
      return i;
   }

   private boolean isRandomGiver() {
      return givingMethod == 0;
   }

   private boolean isAllGiver() { return givingMethod == 1; }

   private boolean isRemainingGiver() { return givingMethod == 2; }

   private boolean isGiverWhenNotOwnedAny() { return givingMethod == 3; }

   private boolean isChainedGiver() { return givingMethod == 4; }

   public boolean isOnTimer() { return cooldownType == 0; }

   private boolean isGiveOnce() { return cooldownType == 1; }

   private boolean isDaily() { return cooldownType == 2; }

   @Override
   public boolean aiShouldExecute() {
      if (npc == null || npc.isAttacking()) { return false; }
      --ticks;
      if (ticks > 0) { return false; }
      ticks = 10;
      toCheck = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(3.0D, 3.0D, 3.0D));
      toCheck.removeAll(recentlyChecked);
      List<Player> listMax = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(10.0D, 10.0D, 10.0D));
      recentlyChecked.retainAll(listMax);
      recentlyChecked.addAll(toCheck);
      return !toCheck.isEmpty();
   }

   @Override
   public boolean aiContinueExecute() { return false; }

   @Override
   public void aiStartExecuting() {
      if (npc != null) {
         for (Player player : toCheck) {
            if (npc.canSee(player) && availability.isAvailable(player)) {
               recentlyChecked.add(player);
               interact(player);
            }
         }
      }
   }

   @Override
   public void interact(Player player) {
      if (npc != null && !giveItems(player)) { npc.say(player, npc.advanced.getInteractLine()); }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public IItemStack[] getItemStacks() {
      IItemStack[] items = new IItemStack[inventory.getContainerSize()];
      NpcAPI api = NpcAPI.Instance();
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         if (api != null) { items[i] = api.getIItemStack(inventory.getItem(i)); }
         else { items[i] = ItemStackWrapper.AIR; }
      }
      return items;
   }

   @Override
   public void setItemStacks(IItemStack[] stacks) {
      inventory.clearContent();
      if (stacks == null) { return; }
      for (int i = 0; i < inventory.getContainerSize() && i < stacks.length; i++) {
         inventory.setItem(i, stacks[i].getMCItemStack());
      }
   }

   @Override
   public String[] getLines() {
      String[] ls = new String[3];
      for (int i = 0; i < 3; i++) {
         if (lines.get(i) != null) { ls[i] = lines.get(i); }
         else { ls[i] = ""; }
      }
      return ls;
   }

   @Override
   public void setLines(String[] linesIn) {
      lines.clear();
      if (linesIn == null) { return; }
      for (int i = 0; i < 3; i++) {
         if (i < linesIn.length) { lines.add(linesIn[i]); }
         else { lines.add(""); }
      }
   }

   @Override
   public int getCooldownType() { return cooldownType; }

   @Override
   public void setCooldownType(int type) {
      if (type < 0 || type > 2) {
         throw new CustomNPCsException("Cooldown type must be between 0 and 2");
      }
      cooldownType = type;
   }

   @Override
   public int getGivingType() { return givingMethod; }

   @Override
   public void setGivingType(int type) {
      if (type < 0 || type > 4) {
         throw new CustomNPCsException("Giving type must be between 0 and 4");
      }
      givingMethod = type;
   }

}
