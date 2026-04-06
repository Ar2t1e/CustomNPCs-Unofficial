package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleBank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketBankClearPos;

public class RoleBank extends RoleInterface implements IRoleBank {

	public int bankId = -1;

	public RoleBank(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.BANK;
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setInteger("RoleBankID", bankId);
		return compound;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.BANK;
		bankId = compound.getInteger("RoleBankID");
	}

	@Override
	public void interact(EntityPlayer player) {
		npc.say(player, npc.advanced.getInteractLine());
		Bank bank = BankController.getInstance().getBank(bankId);
		if (bank != null && player instanceof EntityPlayerMP) {
			NoppesUtilServer.setEditingNpc(player, npc);
			Packets.send((EntityPlayerMP) player, new PacketBankClearPos());
			PlayerData.get(player).bankData.get(bankId).openToPlayer((EntityPlayerMP) player, 0, 0, 0, 1);
		}
	}

	public Bank getBank() {
		Bank bank = BankController.getInstance().banks.get(bankId);
		return bank != null ? bank : BankController.getInstance().banks.values().iterator().next();
	}

	public int getBankId() { return bankId; }

}
