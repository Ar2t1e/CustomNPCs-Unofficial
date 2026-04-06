package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRotationUpdate extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final int orientation;

   public PacketNpcRotationUpdate(int idIn, int orientationIn) {
      id = idIn;
      orientation = orientationIn;
   }

   public static void encode(PacketNpcRotationUpdate msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeInt(msg.orientation);
   }

   public static PacketNpcRotationUpdate decode(FriendlyByteBuf buf) { return new PacketNpcRotationUpdate(buf.readInt(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity instanceof EntityNPCInterface npc) { npc.ais.orientation = orientation; }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
