package noppes.npcs.roles;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.role.IRoleFollower;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerGameData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class RoleFollower extends RoleInterface implements IRoleFollower {

   public boolean disableGui = false;
   public boolean infiniteDays = false;
   public boolean isFollowing = true;
   public boolean refuseSoulStone = false;
   public int daysHired;
   public String ownerUUID;
   public long hiredTime;
   public long waitTime = 0;
   public int rentalMoney = 0;
   public NpcMiscInventory rentalItems = new NpcMiscInventory(3);
   public NpcMiscInventory inventory = new NpcMiscInventory(0);
   public Player owner = null;
   public HashMap<Integer, Integer> rates = new HashMap<>();
   public String dialogFarewell = Util.instance.getOldFormattedText(Component.translatable("follower.farewellText").append(" {player}"));
   public String dialogFired = Util.instance.getOldFormattedText(Component.translatable("follower.firedText").append(" {player}"));
   public String dialogHire = Util.instance.getOldFormattedText(Component.translatable("follower.hireText")
           .append(" {days} ")
           .append(Component.translatable("follower.days")));

   public RoleFollower(EntityNPCInterface npc) {
      super(npc);
      type = RoleType.FOLLOWER;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = RoleType.FOLLOWER;
      ownerUUID = compound.getString("MercenaryOwner");
      daysHired = compound.getInt("MercenaryDaysHired");
      hiredTime = compound.getLong("MercenaryHiredTime");
      rates = NBTTags.getIntegerIntegerMap(compound.getList("MercenaryDayRates", 10));
      if (compound.contains("getCompound", 10)) {
         int size = compound.getCompound("MercenaryInventory").getInt("NpcMiscInvSize");
         inventory = new NpcMiscInventory(size);
         inventory.load(compound.getCompound("MercenaryInventory"));
      }
      isFollowing = compound.getBoolean("MercenaryIsFollowing");
      disableGui = compound.getBoolean("MercenaryDisableGui");
      infiniteDays = compound.getBoolean("MercenaryInfiniteDays");
      refuseSoulStone = compound.getBoolean("MercenaryRefuseSoulstone");
      dialogHire = compound.getString("MercenaryDialogHired");
      dialogFarewell = compound.getString("MercenaryDialogFarewell");
      // New from Unofficial (BetaZavr)
      rentalItems.load(compound.getCompound("MercenaryInv"));
      rentalMoney = compound.getInt("MercenaryMoney");
      if (compound.contains("MercenaryDialogFired", 8)) {
         dialogFired = compound.getString("MercenaryDialogFired");
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("MercenaryDaysHired", daysHired);
      compound.putLong("MercenaryHiredTime", hiredTime);
      compound.putString("MercenaryDialogHired", dialogHire);
      compound.putString("MercenaryDialogFarewell", dialogFarewell);
      if (hasOwner()) { compound.putString("MercenaryOwner", ownerUUID); }
      compound.put("MercenaryDayRates", NBTTags.nbtIntegerIntegerMap(rates));
      compound.put("MercenaryInventory", inventory.save());
      compound.putBoolean("MercenaryIsFollowing", isFollowing);
      compound.putBoolean("MercenaryDisableGui", disableGui);
      compound.putBoolean("MercenaryInfiniteDays", infiniteDays);
      compound.putBoolean("MercenaryRefuseSoulstone", refuseSoulStone);
      // New from Unofficial (BetaZavr)
      compound.put("MercenaryInv", rentalItems.save());
      compound.putInt("MercenaryMoney", rentalMoney);
      compound.putString("MercenaryDialogFired", dialogFired);
      return compound;
   }

   public boolean aiShouldExecute() {
      // New from Unofficial (BetaZavr)
      if (npc.getHealth() <= 0.0f) { return false; }
      if ((ownerUUID == null || ownerUUID.isEmpty()) && !npc.level().dimension().location().equals(npc.homeDimensionId.location())) {
         npc = (EntityNPCInterface) Util.instance.teleportEntity(npc.level().getServer(), npc,
                 npc.homeDimensionId, npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
         return false;
      }
      PlayerData plData = getOwnerData();
      if (plData == null) {
         if (ownerUUID != null && !ownerUUID.isEmpty()) { killed(); }
         return false;
      }
      PlayerGameData.FollowerSet fs = plData.game.getFollower(npc);
      if (fs == null) { fs = plData.game.addFollower(npc); }
      fs.dimId = npc.level().dimension().location();
      fs.npc = npc;
      owner = getOwner();
      if (!infiniteDays && (System.currentTimeMillis() - hiredTime) > getDays() * 1440000L) {
         RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(owner, npc.wrappedNPC);
         EventHooks.onNPCRole(npc, event);
         if (owner != null && owner.containerMenu instanceof ContainerNPCFollowerHire) { owner.closeContainer(); }
         npc.say(owner, new Line(NoppesStringUtils.formatText(dialogFarewell, owner, npc)));
         plData.game.removeFollower(npc);
         killed();
      }
      if (npc.getTarget() != null) { return false; }
      if (!isFollowing) {
         if (!npc.getNavigation().isDone()) { npc.getNavigation().stop(); }
         return false;
      }
      if (owner == null) { return false; }
      double dist = npc.distanceTo(owner);
      if (!owner.level().dimension().location().equals(npc.level().dimension().location())) {
         npc = (EntityNPCInterface) Util.instance.teleportEntity(npc.level().getServer(), npc, owner.level().dimension(), owner.getX(), owner.getY(), owner.getZ());
         fs.dimId = npc.level().dimension().location();
         fs.id = npc.getUUID();
         fs.npc = npc;
         npc.getNavigation().moveTo(owner, npc.ais.canSprint ? 1.3 : 1.0d);
      }
      else if (dist <= 2.5d) {
         if (!npc.getNavigation().isDone()) { npc.getNavigation().stop(); }
         return false;
      }
      else if (dist > getRange()) { npc.setPos(owner.getX(), owner.getY(), owner.getZ()); }
      else {
         boolean bo = npc.getNavigation().moveTo(owner, npc.ais.canSprint ? 1.3 : 1.0d);
         if (!bo && !npc.isMoving()) {
            if (waitTime == 0) {
               waitTime = 10;
               return false;
            }
            waitTime--;
            if (waitTime <= 0) { npc.setPos(owner.getX(), owner.getY(), owner.getZ()); }
         }
         else { waitTime = 0; }
      }
      return false;
   }

   public Player getOwner() {
      if (npc == null || npc.level().isClientSide) { return null; }
      if (ownerUUID != null && !ownerUUID.isEmpty()) {
         try {
            UUID uuid = UUID.fromString(ownerUUID);
             return npc.level().getPlayerByUUID(uuid);
         }
         catch (IllegalArgumentException ignored) { }
         return ((ServerLevel)npc.level()).players()
                 .stream()
                 .filter((t) -> t.getName().getString().equals(ownerUUID))
                 .findFirst().orElse(null);
      }
      return null;
   }

   public boolean hasOwner() {
      if (!infiniteDays && daysHired <= 0) { return false; }
      return ownerUUID != null && !ownerUUID.isEmpty();
   }

   public void killed() {
      ownerUUID = null;
      daysHired = 0;
      hiredTime = 0L;
      isFollowing = true;
   }

   public void reset() { killed(); }

   public void interact(Player playerIn) {
      if (playerIn instanceof ServerPlayer player) {
         if (ownerUUID != null && !ownerUUID.isEmpty()) {
            if (player == owner && !disableGui) { NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollower, npc); }
         }
         else {
            if (npc != null) { npc.say(player, npc.advanced.getInteractLine()); }
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollowerHire, npc);
         }
      }
   }

   public boolean defendOwner() { return isFollowing() && npc != null && npc.job.getType() == 3; }

   public boolean isFollowing() { return owner != null && isFollowing && getDays() > 0; }

   public void setOwner(@Nullable Player player) {
      if (player == null) {
         killed();
         return;
      }
      UUID id = player.getUUID();
      if (ownerUUID == null || !ownerUUID.equals(id.toString())) { killed(); }
      ownerUUID = id.toString();
   }

   public int getDays() {
      if (infiniteDays) { return 100; }
      else if (daysHired <= 0) { return 0; }
      int days = (int)(((npc == null ? 0 : npc.level().getGameTime()) - hiredTime) / 24000L);
      return daysHired - days;
   }

   public void addDays(int days) {
      daysHired = days + getDays();
      hiredTime = npc == null ? 0 : npc.level().getGameTime();
   }

   public boolean getInfinite() { return infiniteDays; }

   public void setInfinite(boolean infinite) { infiniteDays = infinite; }

   public boolean getGuiDisabled() { return disableGui; }

   public void setGuiDisabled(boolean disabled) { disableGui = disabled; }

   public boolean getRefuseSoulstone() { return refuseSoulStone; }

   public void setRefuseSoulstone(boolean refuse) { refuseSoulStone = refuse; }

   public IPlayer<?> getFollowing() {
      Player owner = getOwner();
      return owner != null ? (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(owner) : null;
   }

   public void setFollowing(IPlayer<?> player) {
      setOwner(player == null ? null : player.getMCEntity());
   }

   // New from Unofficial (BetaZavr)
   public int getRange() {
      if (npc.stats.aggroRange > CustomNpcs.NpcNavRange) { return CustomNpcs.NpcNavRange; }
      return npc.stats.aggroRange;
   }

   private PlayerData getOwnerData() {
      if (ownerUUID == null || ownerUUID.isEmpty() || CustomNpcs.Server == null || npc.level().getServer() == null) { return null; }
      return PlayerDataController.instance.getDataFromUsername(CustomNpcs.Server == null ? npc.level().getServer() : CustomNpcs.Server, ownerUUID);
   }

}
