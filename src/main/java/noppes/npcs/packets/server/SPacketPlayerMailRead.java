package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerMailRead extends PacketServerBasic {

   protected static int channelId;
   private long time;
   private String username;

   public SPacketPlayerMailRead() { }

   public SPacketPlayerMailRead(long timeIn, String usernameIn) {
      time = timeIn;
      username = usernameIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeLong(time);
      buf.writeUtf(username);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      time = buf.readLong();
      username = buf.readUtf();
   }

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
