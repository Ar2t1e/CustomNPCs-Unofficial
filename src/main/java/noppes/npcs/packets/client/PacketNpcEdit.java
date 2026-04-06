package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcEdit extends PacketBasic {

   protected static int channelId;
   private final int id;

   public PacketNpcEdit(int idIn) { id = idIn; }

   public static void encode(PacketNpcEdit msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static PacketNpcEdit decode(FriendlyByteBuf buf) { return new PacketNpcEdit(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity instanceof EntityNPCInterface npc) { NoppesUtilServer.setEditingNpc(player, npc); }
         else { NoppesUtilServer.setEditingNpc(player, null); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
