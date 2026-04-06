package noppes.npcs.roles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleTrader;
import noppes.npcs.api.handler.data.IMarcet;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class RoleTrader extends RoleInterface implements IRoleTrader {

   private int marcetId = -1;

   public RoleTrader(EntityNPCInterface npc) {
      super(npc);
      type = RoleType.TRADER;
   }

   @Override
   public IMarcet getMarket() { return MarcetController.getInstance().getMarcet(marcetId); }

   @Override
   public int getMarketID() { return marcetId; }

   @Override
   public void interact(Player player) {
      npc.say(player, npc.advanced.getInteractLine());
      Marcet marcet = (Marcet) getMarket();
      if (marcet != null && marcet.isValid() && player instanceof ServerPlayer sPlayer) {
         marcet.addListener(player, true);
         PlayerData.get(player).game.getMarkupData(marcetId);
         NoppesUtilServer.sendExtraData(sPlayer, npc, EnumGuiType.PlayerTrader);
         NoppesUtilServer.openContainerGui(sPlayer, EnumGuiType.PlayerTrader, (buffer) -> buffer.writeInt(marcetId));
      }
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = RoleType.TRADER;
      if (!compound.contains("MarketID", 3)) { marcetId = MarcetController.getInstance().loadOld(compound); }
      else { marcetId = compound.getInt("MarketID"); }
   }

   @Override
   public void setMarket(IMarcet marcet) {
      IMarcet m = getMarket();
      if (m != null) { ((Marcet) m).closeForAllPlayers(); }
      marcetId = marcet.getId();
   }

   @Override
   public void setMarket(int id) {
      IMarcet m = getMarket();
      if (m != null) { ((Marcet) m).closeForAllPlayers(); }
      marcetId = id;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("MarketID", marcetId);
      return compound;
   }

}
