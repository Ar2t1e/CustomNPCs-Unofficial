package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRole extends PacketBasic {

   protected static int channelId;
   private int id;
   private NBTTagCompound data;

   public PacketNpcRole() { }

   public PacketNpcRole(int idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = Minecraft.getMinecraft().world;
      if (world != null) {
         Entity entity = world.getEntityByID(id);
         if (entity instanceof EntityNPCInterface) {
            ((EntityNPCInterface) entity).advanced.setRole(data.getInteger("Type"));
            ((EntityNPCInterface) entity).role.load(data);
            NoppesUtilServer.setEditingNpc(player, (EntityNPCInterface) entity);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
