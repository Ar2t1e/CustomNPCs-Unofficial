package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.parts.ModelEyeData;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketBasic;

public class PacketEyeBlink extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final ResourceKey<Level> dimension;

   public PacketEyeBlink(int idIn, ResourceKey<Level> dimensionIn) {
      id = idIn;
      dimension = dimensionIn;
   }

   public static void encode(PacketEyeBlink msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeResourceKey(msg.dimension);
   }

   public static PacketEyeBlink decode(FriendlyByteBuf buf) { return new PacketEyeBlink(buf.readInt(), buf.readResourceKey(Registries.DIMENSION)); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.level().dimension().equals(dimension)) {
         Entity entity = player.level().getEntity(id);
         if (entity instanceof EntityCustomNpc npcIn && npcIn.modelData != null) {
            for (MpmPartData pd : npcIn.modelData.mpmParts) {
               if (pd instanceof ModelEyeData med) { med.blinkStart = System.currentTimeMillis(); }
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
