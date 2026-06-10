package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.mixin.world.ISimpleContainerMixin;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPacketFollowerHire extends PacketServerBasic {

   protected static int channelId;
   private final int pos;

   public SPacketFollowerHire(int posIn) { pos = posIn; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketFollowerHire msg, FriendlyByteBuf buf) { buf.writeInt(msg.pos); }

   public static SPacketFollowerHire decode(FriendlyByteBuf buf) { return new SPacketFollowerHire(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role instanceof RoleFollower role && player.containerMenu instanceof ContainerNPCFollowerHire) {
         if (pos >= 0 && pos < 4 && role.rates.containsKey(pos)) {
            if (pos == 3) {
               if (!player.isCreative()) {
                  if (PlayerData.get(player).game.getMoney() < role.rentalMoney) {
                     CustomNpcs.debugData.end("Packets");
                     return;
                  }
                  PlayerData.get(player).game.addMoney(role.rentalMoney * -1);
               }
            } else {
               ItemStack currency = role.rentalItems.getItem(0);
               if (currency.isEmpty()) {
                  CustomNpcs.debugData.end("Packets");
                  return;
               }
               if (!player.isCreative()) {
                  Map<ItemStack, Integer> map = new HashMap<>();
                  map.put(currency, currency.getCount());
                  if (!Util.instance.canRemoveItems(((ISimpleContainerMixin) role.rentalItems).getItems(), map, false, false)) {
                     CustomNpcs.debugData.end("Packets");
                     return;
                  }
                  Util.instance.removeItem(player, currency, false, false);
               }
            }
            int days = role.rates.get(pos);
            RoleEvent.FollowerHireEvent event = new RoleEvent.FollowerHireEvent(player, npc.wrappedNPC, days);
            if (!EventHooks.onNPCRole(npc, event) && event.days > 0) {
               npc.say(player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", days + ""), player, npc)));
               role.setOwner(player);
               role.addDays(days);
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
