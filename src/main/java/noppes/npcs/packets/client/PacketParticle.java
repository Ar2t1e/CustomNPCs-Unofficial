package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.EnumParticleTypes;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

import java.util.Random;

public class PacketParticle extends PacketBasic {

   protected static int channelId;
   private double posX;
   private double posY;
   private double posZ;
   private float height;
   private float width;
   private String name;

   public PacketParticle() { }

   public PacketParticle(double x, double y, double z, float heightIn, float widthIn, String nameIn) {
      posX = x;
      posY = y;
      posZ = z;
      height = heightIn;
      width = widthIn;
      name = nameIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      posX = buf.readDouble();
      posY = buf.readDouble();
      posZ = buf.readDouble();
      height = buf.readFloat();
      width = buf.readFloat();
      name = buf.readUtf();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeDouble(posX);
      buf.writeDouble(posY);
      buf.writeDouble(posZ);
      buf.writeFloat(height);
      buf.writeFloat(width);
      buf.writeUtf(name);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = Minecraft.getMinecraft().world;
      if (world != null) {
         Random rand = world.rand;
         if (name.equals("heal")) {
            for (int k = 0; k < 6; ++k) {
               world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, 0.0, 0.0, 0.0);
               world.spawnParticle(EnumParticleTypes.SPELL, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, 0.0, 0.0, 0.0);
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
