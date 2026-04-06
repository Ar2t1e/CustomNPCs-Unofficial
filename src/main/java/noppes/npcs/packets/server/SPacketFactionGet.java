package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionGet extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketFactionGet(int idIn) { id = idIn; }

   public static void encode(SPacketFactionGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketFactionGet decode(FriendlyByteBuf buf) { return new SPacketFactionGet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CompoundTag compound = new CompoundTag();
      Faction faction = FactionController.instance.getFaction(id);
      faction.save(compound);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
