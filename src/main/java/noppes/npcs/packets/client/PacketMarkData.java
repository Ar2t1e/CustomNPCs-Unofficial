package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarkData extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final CompoundTag data;

   public PacketMarkData(int idIn, CompoundTag dataIn) {
      id = idIn;
      data = dataIn;
   }

   public static void encode(PacketMarkData msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeNbt(msg.data);
   }

   public static PacketMarkData decode(FriendlyByteBuf buf) { return new PacketMarkData(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity instanceof LivingEntity) {
            MarkData mark = MarkData.get((LivingEntity)entity);
            mark.setNBT(data);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
