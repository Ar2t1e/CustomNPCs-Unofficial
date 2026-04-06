package noppes.npcs.roles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketBankClearPos;

import javax.annotation.Nullable;

public class RoleBank extends RoleInterface {

   public int bankId = -1;

   public RoleBank(EntityNPCInterface npc) {
      super(npc);
      type = RoleType.BANK;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("RoleBankID", bankId);
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = RoleType.BANK;
      bankId = compound.getInt("RoleBankID");
   }

   @Override
   public void interact(Player player) {
      npc.say(player, npc.advanced.getInteractLine());
      Bank bank = BankController.getInstance().getBank(bankId);
      if (bank != null && player instanceof ServerPlayer sPlayer) {
         NoppesUtilServer.setEditingNpc(sPlayer, npc);
         Packets.send(sPlayer, new PacketBankClearPos());
         PlayerData.get(sPlayer).bankData.get(bankId).openToPlayer(sPlayer, 0, 0, 0, 1);
      }
   }

   public @Nullable Bank getBank() {
      Bank bank = BankController.getInstance().getBank(bankId);
      return bank != null ? bank : BankController.getInstance().getBanks().iterator().next();
   }

   public int getBankId() { return bankId; }

}
