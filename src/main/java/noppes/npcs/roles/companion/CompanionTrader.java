package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumGuiType;

public class CompanionTrader extends CompanionJobInterface {

   public CompoundTag getNBT() {
       return new CompoundTag();
   }

   public void setNBT(CompoundTag compound) {
   }

   public void interact(Player player) {
      NoppesUtilServer.sendOpenGui((ServerPlayer) player, EnumGuiType.CompanionTrader, this.npc);
   }

   public EnumCompanionJobs getType() {
      return EnumCompanionJobs.SHOP;
   }

}
