package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailOpen extends PacketServerBasic {

   protected static int channelId;
   private final boolean canEdit;
   private final boolean canSend;
   private final long time;
   private final String username;

   public SPacketPlayerMailOpen(boolean canEditIn, boolean canSendIn, long timeIn, String usernameIn) {
      canEdit = canEditIn;
      canSend = canSendIn;
      time = timeIn;
      username = usernameIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketPlayerMailOpen msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.canEdit);
      buf.writeBoolean(msg.canSend);
      buf.writeLong(msg.time);
      buf.writeUtf(msg.username);
   }

   public static SPacketPlayerMailOpen decode(FriendlyByteBuf buf) {
      return new SPacketPlayerMailOpen(buf.readBoolean(), buf.readBoolean(), buf.readLong(), buf.readUtf());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      player.closeContainer();
      if (canEdit && canSend) {
         NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerMailOpen, (buf) -> {
            buf.writeBoolean(true);
            buf.writeBoolean(true);
         });
      }
      else {
         PlayerMailData mailData = PlayerData.get(player).mailData;
         for (PlayerMail mail : mailData.playerMails) {
            if (mail.timeWhenReceived == time && mail.sender.equals(username)) {
               ContainerMail.staticMail = mail;
               NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerMailOpen, (buf) -> {
                  buf.writeBoolean(false);
                  buf.writeBoolean(false);
               });
               break;
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
