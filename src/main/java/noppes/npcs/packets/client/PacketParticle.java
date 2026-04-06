package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketParticle extends PacketBasic {

   protected static int channelId;
   private final double posX;
   private final double posY;
   private final double posZ;
   private final float height;
   private final float width;
   private final String name;

   public PacketParticle(double x, double y, double z, float heightIn, float widthIn, String nameIn) {
      posX = x;
      posY = y;
      posZ = z;
      height = heightIn;
      width = widthIn;
      name = nameIn;
   }

   public static void encode(PacketParticle msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.posX);
      buf.writeDouble(msg.posY);
      buf.writeDouble(msg.posZ);
      buf.writeFloat(msg.height);
      buf.writeFloat(msg.width);
      buf.writeUtf(msg.name);
   }

   public static PacketParticle decode(FriendlyByteBuf buf) {
      return new PacketParticle(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readUtf());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         RandomSource rand = level.random;
         if (name.equals("heal")) {
            for(int k = 0; k < 6; ++k) {
               level.addParticle(ParticleTypes.INSTANT_EFFECT, posX + (rand.nextDouble() - 0.5D) * (double)width, posY + rand.nextDouble() * (double)height, posZ + (rand.nextDouble() - 0.5D) * (double)width, 0.0D, 0.0D, 0.0D);
               level.addParticle(ParticleTypes.EFFECT, posX + (rand.nextDouble() - 0.5D) * (double)width, posY + rand.nextDouble() * (double)height, posZ + (rand.nextDouble() - 0.5D) * (double)width, 0.0D, 0.0D, 0.0D);
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
