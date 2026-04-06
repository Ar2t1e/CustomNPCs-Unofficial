package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRole extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final CompoundTag data;

   public PacketNpcRole(int idIn, CompoundTag dataIn) {
      id = idIn;
      data = dataIn;
   }

   public static void encode(PacketNpcRole msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeNbt(msg.data);
   }

   public static PacketNpcRole decode(FriendlyByteBuf buf) { return new PacketNpcRole(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity instanceof EntityNPCInterface npc) {
            npc.advanced.setRole(data.getInt("Type"));
            npc.role.load(data);
            NoppesUtilServer.setEditingNpc(player, npc);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
