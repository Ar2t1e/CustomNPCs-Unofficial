package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRolePostman;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class RolePostman extends RoleInterface implements IRolePostman {

	public NpcMiscInventory inventory = new NpcMiscInventory(1);
	private final List<EntityPlayer> recentlyChecked = new ArrayList<>();

    public RolePostman(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.MAILMAN;
	}

    @Override
	public boolean aiShouldExecute() {
		if (npc.ticksExisted % 20 == 0) {
			List<EntityPlayer> toCheck;
			List<EntityPlayer> list = new ArrayList<>();
			try { list = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(10.0, 10.0, 10.0)); }
			catch (Exception ignored) { }
			(toCheck = list).removeAll(recentlyChecked);
			List<EntityPlayer> listMax = new ArrayList<>();
			try { listMax = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(20.0, 20.0, 20.0)); }
			catch (Exception ignored) { }

			recentlyChecked.retainAll(listMax);
			recentlyChecked.addAll(toCheck);
			for (EntityPlayer player : toCheck) {
				if (PlayerData.get(player).mailData.hasMail()) { npc.say(player, new Line("mail.player.has.letter")); }
			}
		}
		return false;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.MAILMAN;
		inventory.load(compound.getCompoundTag("PostInv"));
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setTag("PostInv", inventory.save());
		return compound;
	}

	@Override
	public void interact(EntityPlayer player) {
		NoppesUtilServer.openContainerGui((EntityPlayerMP) player, EnumGuiType.PlayerMailOpen, (buf) -> {
			buf.writeBoolean(true);
			buf.writeBoolean(true);
		});
	}

}
