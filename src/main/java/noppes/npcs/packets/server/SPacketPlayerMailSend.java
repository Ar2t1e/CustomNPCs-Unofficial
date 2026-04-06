package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerMailSend extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag compound;
   private final String username;
   private final long cost;

   public SPacketPlayerMailSend(String usernameIn, long costIn, CompoundTag compoundIn) {
      username = usernameIn;
      compound = compoundIn;
      cost = costIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketPlayerMailSend msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.username);
      buf.writeLong(msg.cost);
      buf.writeNbt(msg.compound);
   }

   public static SPacketPlayerMailSend decode(FriendlyByteBuf buf) { return new SPacketPlayerMailSend(buf.readUtf(), buf.readLong(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (username.equalsIgnoreCase(player.getName().getString()) && !(CustomNpcs.MailSendToYourself || player.isCreative())) {
         NoppesUtilServer.sendGuiError(player, 2);
      }
      else if (PlayerDataController.instance.hasPlayer(username).isEmpty()) {
         NoppesUtilServer.sendGuiError(player, 0);
      }
      else if (!player.isCreative() && cost > PlayerData.get(player).game.getMoney()) {
         NoppesUtilServer.sendGuiError(player, 3);
      }
      else {
         PlayerMail mail = new PlayerMail();
         String s = player.getDisplayName().getString();
         if (!s.equals(player.getName().getString())) { s = s + "(" + player.getName() + ")"; }
         mail.load(compound);
         if (!mail.title.isEmpty()) {
            mail.sender = s;
            for (int i = 0; i < 4; i++) { mail.items.set(i, ((ContainerMail) player.containerMenu).mail.items.get(i)); }
            CompoundTag comp = new CompoundTag();
            comp.putString("username", username);
            NoppesUtilServer.sendGuiClose(player, comp);
            EntityNPCInterface npc2 = NoppesUtilServer.getEditingNpc(player);
            if (npc2 == null || !EventHooks.onNPCRole(npc2, new RoleEvent.MailmanEvent(player, npc2.wrappedNPC, mail))) {
               PlayerDataController.instance.addPlayerMessage(player.getServer(), username, mail);
            }
         }
         else { NoppesUtilServer.sendGuiError(player, 1); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
