package noppes.npcs.packets.client;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class PacketNpcVisibleTrue extends PacketBasic {

   protected static int channelId;
   private static final Constructor<SpawnEntity> constr;
   private final SpawnEntity pkt;
   private final int id;

   public PacketNpcVisibleTrue(Entity entity) {
      id = entity.getId();
      SpawnEntity p = null;
      try { p = constr.newInstance(entity); } catch (Exception e) { LogWriter.error(e); }
      pkt = p;
   }

   public PacketNpcVisibleTrue(int idIn, SpawnEntity pktIn) {
      id = idIn;
      pkt = pktIn;
   }

   public static void encode(PacketNpcVisibleTrue msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      SpawnEntity.encode(msg.pkt, buf);
   }

   public static PacketNpcVisibleTrue decode(FriendlyByteBuf buf) { return new PacketNpcVisibleTrue(buf.readInt(), SpawnEntity.decode(buf)); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(id);
         if (entity == null) { SpawnEntity.handle(pkt, ctx); }
      }
      CustomNpcs.debugData.end("Packets");
   }

   static {
      Constructor<SpawnEntity> con = null;
      try {
         con = SpawnEntity.class.getDeclaredConstructor(Entity.class);
         con.setAccessible(true);
      } catch (NoSuchMethodException e) {
         LogWriter.error(e);
      }
      constr = con;
   }

}
