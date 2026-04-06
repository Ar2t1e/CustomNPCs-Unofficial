package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcDialogRemove extends PacketServerBasic {

   protected static int channelId;
   private int slot;

   public SPacketNpcDialogRemove() { }

   public SPacketNpcDialogRemove(int slotIn) { slot = slotIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(slot); }

   @Override
   public void decode(FriendlyByteBuf buf) { slot = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (slot >= 0 && slot < npc.dialogs.length) {
         int[] newIDs = new int[npc.dialogs.length - 1];
         for (int i = 0, j = 0; i < npc.dialogs.length; i++) {
            if (i == slot) { continue; }
            newIDs[j] = npc.dialogs[i];
            j++;
         }
         npc.dialogs = newIDs;
         NoppesUtilServer.sendNpcDialogs(player);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
