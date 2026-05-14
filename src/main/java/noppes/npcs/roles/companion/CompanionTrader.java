package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumGuiType;

public class CompanionTrader extends CompanionJobInterface {

   @Override
   public CompoundTag getNBT() { return new CompoundTag(); }

   @Override
   public void setNBT(CompoundTag compound) { }

   @Override
   public EnumCompanionJobs getType() { return EnumCompanionJobs.SHOP; }

   public void interact(Player playerIn) {
      if (playerIn instanceof ServerPlayer player) {
         NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionTrader, npc);
      }
   }

}
