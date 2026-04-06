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

public class PacketNpcDelete extends PacketBasic {

   protected static int channelId;
   private final int id;

   public PacketNpcDelete(int idIn) { id = idIn; }

   public static void encode(PacketNpcDelete msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static PacketNpcDelete decode(FriendlyByteBuf buf) { return new PacketNpcDelete(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).delete(); }
         else if (entity != null) { entity.remove(Entity.RemovalReason.DISCARDED); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
