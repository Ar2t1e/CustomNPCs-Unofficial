package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.*;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailSend extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound compound;
   private String username;
   private long cost;

   public SPacketPlayerMailSend() { }

   public SPacketPlayerMailSend(String usernameIn, long costIn, NBTTagCompound compoundIn) {
      username = usernameIn;
      compound = compoundIn;
      cost = costIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(username);
      buf.writeLong(cost);
      buf.writeNbt(compound);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      username = buf.readUtf();
      cost = buf.readLong();
      compound = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (username.equalsIgnoreCase(player.getName()) && !(CustomNpcs.MailSendToYourself || player.isCreative())) {
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
         String s = player.getDisplayNameString();
         if (!s.equals(player.getName())) { s = s + "(" + player.getName() + ")"; }
         mail.load(compound);
         if (!mail.title.isEmpty()) {
            mail.sender = s;
            for (int i = 0; i < 4; i++) { mail.items.set(i, ((ContainerMail) player.openContainer).mail.items.get(i)); }
            NBTTagCompound comp = new NBTTagCompound();
            comp.setString("username", username);
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
