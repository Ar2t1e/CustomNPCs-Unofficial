package noppes.npcs.api.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiParts;

import java.util.Objects;

public class ModelMenu extends MainMenuGui {

   public ModelMenu(EntityCustomNpc npc, IPlayer<?> player) {
      super(1, npc, player, false);
      this.gui.getScrollingPanel().init(180, 26, 230, this.gui.getHeight() - 32);
   }

   public static void open(Player player, EntityCustomNpc npc) {
      IPlayer<?> p = (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player);
      CustomGuiWrapper menu = (new ModelMenu(npc, p)).gui;
      p.showCustomGui(menu);
      if (player instanceof ServerPlayer) {
         Packets.send((ServerPlayer) p.getMCEntity(), new PacketGuiParts(npc.getId(), menu.toNBT()));
      }
   }

}
