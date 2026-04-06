package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketLinkedSet extends PacketServerBasic {

   protected static int channelId;
   private String name;

   public SPacketLinkedSet() { }

   public SPacketLinkedSet(String nameIn) { name = nameIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

   @Override
   public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.linkedName = name;
      LinkedNpcController.Instance.loadNpcData(npc);
      CustomNpcs.debugData.end("Packets");
   }

}
