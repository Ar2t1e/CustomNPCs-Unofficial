package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailRead extends PacketServerBasic {

   protected static int channelId;
   private final long time;
   private final String username;

   public SPacketPlayerMailRead(long timeIn, String usernameIn) {
      time = timeIn;
      username = usernameIn;
   }

   @Override
   public boolean requiresNpc() { return false; }


   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true;}

   public static void encode(SPacketPlayerMailRead msg, FriendlyByteBuf buf) {
      buf.writeLong(msg.time);
      buf.writeUtf(msg.username);
   }

   public static SPacketPlayerMailRead decode(FriendlyByteBuf buf) { return new SPacketPlayerMailRead(buf.readLong(), buf.readUtf()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerMailData mailData  = PlayerData.get(player).mailData;
      for (PlayerMail mail : mailData.playerMails) {
         if (!mail.beenRead && mail.timeWhenReceived == time && mail.sender.equals(username)) {
            if (mail.hasQuest()) {
               PlayerQuestController.addActiveQuest(mail.getQuest(), player, false);
            }
            mail.beenRead = true;
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
