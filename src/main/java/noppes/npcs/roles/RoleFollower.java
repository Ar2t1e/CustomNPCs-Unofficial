package noppes.npcs.roles;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.role.IRoleFollower;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerGameData.FollowerSet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

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
	public EntityPlayer owner = null;
	public HashMap<Integer, Integer> rates = new HashMap<>();
	public String dialogFarewell = Component.translatable("follower.farewellText").append(" {player}").getFormattedText();
	public String dialogFired = Component.translatable("follower.firedText").append(" {player}").getFormattedText();
	public String dialogHire = Component.translatable("follower.hireText")
			.append(" {days} ")
			.append(Component.translatable("follower.days")).getFormattedText();

	public RoleFollower(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.FOLLOWER;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.FOLLOWER;
		ownerUUID = compound.getString("MercenaryOwner");
		daysHired = compound.getInteger("MercenaryDaysHired");
		hiredTime = compound.getLong("MercenaryHiredTime");
		rates = NBTTags.getIntegerIntegerMap(compound.getTagList("MercenaryDayRates", 10));
		if (compound.hasKey("MercenaryInventory", 10)) {
			int size = compound.getCompoundTag("MercenaryInventory").getInteger("NpcMiscInvSize");
			inventory = new NpcMiscInventory(size);
			inventory.load(compound.getCompoundTag("MercenaryInventory"));
		}
		isFollowing = compound.getBoolean("MercenaryIsFollowing");
		disableGui = compound.getBoolean("MercenaryDisableGui");
		infiniteDays = compound.getBoolean("MercenaryInfiniteDays");
		refuseSoulStone = compound.getBoolean("MercenaryRefuseSoulstone");
		dialogHire = compound.getString("MercenaryDialogHired");
		dialogFarewell = compound.getString("MercenaryDialogFarewell");
		// New from Unofficial (BetaZavr)
		rentalItems.load(compound.getCompoundTag("MercenaryInv"));
		rentalMoney = compound.getInteger("MercenaryMoney");
		if (compound.hasKey("MercenaryDialogFired", 8)) {
			dialogFired = compound.getString("MercenaryDialogFired");
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setInteger("MercenaryDaysHired", daysHired);
		compound.setLong("MercenaryHiredTime", hiredTime);
		compound.setString("MercenaryDialogHired", dialogHire);
		compound.setString("MercenaryDialogFarewell", dialogFarewell);
		if (hasOwner()) { compound.setString("MercenaryOwner", ownerUUID); }
		compound.setTag("MercenaryDayRates", NBTTags.nbtIntegerIntegerMap(rates));
		compound.setTag("MercenaryInventory", inventory.save());
		compound.setBoolean("MercenaryIsFollowing", isFollowing);
		compound.setBoolean("MercenaryDisableGui", disableGui);
		compound.setBoolean("MercenaryInfiniteDays", infiniteDays);
		compound.setBoolean("MercenaryRefuseSoulstone", refuseSoulStone);
		// New from Unofficial (BetaZavr)
		compound.setTag("MercenaryInv", rentalItems.save());
		compound.setInteger("MercenaryMoney", rentalMoney);
		compound.setString("MercenaryDialogFired", dialogFired);
		return compound;
	}

	@Override
	public void addDays(int days) {
		if (hiredTime == 0L) {
			daysHired = days;
			hiredTime = System.currentTimeMillis();
		}
		else { daysHired += days; }
	}

	@Override
	public boolean aiShouldExecute() {
		// New from Unofficial (BetaZavr)
		if (npc.getHealth() <= 0.0f) { return false; }
		if ((ownerUUID == null || ownerUUID.isEmpty()) && npc.world.provider.getDimension() != npc.homeDimensionId) {
			npc = (EntityNPCInterface) Util.instance.teleportEntity(npc.world.getMinecraftServer(), npc,
						npc.homeDimensionId, npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
			return false;
		}
		PlayerData plData = getOwnerData();
		if (plData == null) {
			if (ownerUUID != null && !ownerUUID.isEmpty()) { killed(); }
			return false;
		}
		FollowerSet fs = plData.game.getFollower(npc);
        if (fs == null) { fs = plData.game.addFollower(npc); }
        fs.dimId = npc.world.provider.getDimension();
        fs.npc = npc;
        owner = getOwner();
		if (!infiniteDays && (System.currentTimeMillis() - hiredTime) > getDays() * 1440000L) {
			RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(owner, npc.wrappedNPC);
			EventHooks.onNPCRole(npc, event);
			if (owner != null && owner.openContainer instanceof ContainerNPCFollowerHire) { owner.closeScreen(); }
			npc.say(owner, new Line(NoppesStringUtils.formatText(dialogFarewell, owner, npc)));
            plData.game.removeFollower(npc);
            killed();
		}
		if (npc.getAttackTarget() != null) { return false; }
		if (!isFollowing) {
			if (!npc.getNavigator().noPath()) { npc.getNavigator().clearPath(); }
			return false;
		}
		if (owner == null) { return false; }
		double dist = npc.getDistance(owner);
		if (owner.world.provider.getDimension() != npc.world.provider.getDimension()) {
			npc = (EntityNPCInterface) Util.instance.teleportEntity(npc.world.getMinecraftServer(), npc, owner.world.provider.getDimension(), owner.posX, owner.posY, owner.posZ);
			fs.dimId = npc.world.provider.getDimension();
			fs.id = npc.getUniqueID();
			fs.npc = npc;
			npc.getNavigator().tryMoveToEntityLiving(owner, npc.ais.canSprint ? 1.3 : 1.0d);
		}
		else if (dist <= 2.5d) {
			if (!npc.getNavigator().noPath()) { npc.getNavigator().clearPath(); }
			return false;
		}
		else if (dist > getRange()) { npc.setPosition(owner.posX, owner.posY, owner.posZ); }
		else {
			boolean bo = npc.getNavigator().tryMoveToEntityLiving(owner, npc.ais.canSprint ? 1.3 : 1.0d);
			if (!bo && !npc.isMoving()) {
				if (waitTime == 0) {
					waitTime = 10;
					return false;
				}
				waitTime--;
				if (waitTime <= 0) { npc.setPosition(owner.posX, owner.posY, owner.posZ); }
			}
			else { waitTime = 0; }
		}
		return false;
	}

	@Override
	public boolean defendOwner() {
		return !isFollowing() || npc.job.getEnumType() != JobType.GUARD;
	}

    @Override
	public int getDays() {
		if (infiniteDays) { return 100; }
		if (daysHired <= 0) { return 0; }
		int daysPassed = (int) Math.floor((double) (System.currentTimeMillis() - hiredTime) / 480000.0d);
		return daysHired - daysPassed;
	}

	@Override
	public IPlayer<?> getFollowing() {
		EntityPlayer owner = getOwner();
		if (owner != null) {
			return (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(owner);
		}
		return null;
	}

	@Override
	public boolean getGuiDisabled() { return disableGui; }

	@Override
	public boolean getInfinite() { return infiniteDays; }

	public EntityPlayer getOwner() {
		if (ownerUUID == null || ownerUUID.isEmpty()) { return null; }
		try {
			UUID uuid = UUID.fromString(ownerUUID);
            MinecraftServer server = null;
            if (npc.world != null) { server = npc.world.getMinecraftServer(); }
            if (server == null && CustomNpcs.Server != null) { server = CustomNpcs.Server; }
            if (server != null) { return server.getPlayerList().getPlayerByUUID(uuid); }
        } catch (Exception e) { LogWriter.error(e); }
        assert npc.world != null;
        return npc.world.getPlayerEntityByName(ownerUUID);
	}

	private PlayerData getOwnerData() {
		if (ownerUUID == null || ownerUUID.isEmpty() || CustomNpcs.Server == null || npc.world == null || npc.world.getMinecraftServer() == null) {
			return null;
		}
		return PlayerDataController.instance.getDataFromUsername(
				CustomNpcs.Server == null ? npc.world.getMinecraftServer() : CustomNpcs.Server, ownerUUID);
	}

	public int getRange() {
		if (npc.stats.aggroRange > CustomNpcs.NpcNavRange) {
			return CustomNpcs.NpcNavRange;
		}
		return npc.stats.aggroRange;
	}

	@Override
	public boolean getRefuseSoulstone() { return refuseSoulStone; }

	public boolean hasOwner() {
		return (infiniteDays || daysHired > 0) && ownerUUID != null && !ownerUUID.isEmpty();
	}

	@Override
	public void interact(EntityPlayer playerIn) {
		if (playerIn instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) playerIn;
			if (ownerUUID != null && !ownerUUID.isEmpty()) {
				if (player == owner && !disableGui) {
					SPacketGuiOpen.sendOpenGui(player, EnumGuiType.PlayerFollower, npc, new BlockPos(1, 0, 0));
				}
			}
			else {
				if (npc != null) { npc.say(player, npc.advanced.getInteractLine()); }
				SPacketGuiOpen.sendOpenGui(player, EnumGuiType.PlayerFollowerHire, npc, new BlockPos(0, 0, 0));
			}
		}
	}

	@Override
	public boolean isFollowing() {
		return ownerUUID != null && !ownerUUID.isEmpty() && isFollowing && getDays() > 0;
	}

	@Override
	public void killed() {
		if (!inventory.isEmpty()) {
			if (owner == null) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (!NoppesUtilServer.isItemStackNull(stack)) { npc.entityDropItem(stack, 0.0f); }
				}
			}
			else if (owner.world.provider.getDimension() == npc.world.provider.getDimension()) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (!NoppesUtilServer.isItemStackNull(stack)) {
						EntityItem entityitem = new EntityItem(owner.world, owner.posX, owner.posY, owner.posZ, stack);
						entityitem.setPickupDelay(0);
						owner.world.spawnEntity(entityitem);
					}
				}
			}
			inventory.clear();
		}
		ownerUUID = null;
		daysHired = 0;
		hiredTime = 0L;
		isFollowing = true;
		PlayerData plData = getOwnerData();
		if (plData != null) {
			plData.game.removeFollower(npc);
			plData.save(true);
		}
	}

	@Override
	public void reset() {
		killed();
	}

	@Override
	public void setFollowing(IPlayer<?> player) {
		if (player == null) { ownerUUID = null; }
		else { setOwner(player.getMCEntity()); }
	}

	@Override
	public void setGuiDisabled(boolean disabled) { disableGui = disabled; }

	@Override
	public void setInfinite(boolean infinite) { infiniteDays = infinite; }

	public void setOwner(EntityPlayer player) {
		UUID id = player.getUniqueID();
		if (ownerUUID == null || !ownerUUID.equals(id.toString())) { killed(); }
		ownerUUID = id.toString();
	}

	@Override
	public void setRefuseSoulstone(boolean refuse) { refuseSoulStone = refuse; }

}
