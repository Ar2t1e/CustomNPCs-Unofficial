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
import noppes.npcs.controllers.PhysicsHelper;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class PacketUpdatePhysics extends PacketBasic {

   protected static final Constructor<SpawnEntity> constr;
   protected static int channelId;

   private final SpawnEntity pkt;
   private final int id;

   public PacketUpdatePhysics(Entity entity) {
      id = entity.getId();
      SpawnEntity p = null;
      try { p = constr.newInstance(entity); }
      catch (Exception e) { LogWriter.error(e); }
      pkt = p;
   }

   public PacketUpdatePhysics(int idIn, SpawnEntity pktIn) {
      id = idIn;
      pkt = pktIn;
   }

   public static void encode(PacketUpdatePhysics msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      SpawnEntity.encode(msg.pkt, buf);
   }

   public static PacketUpdatePhysics decode(FriendlyByteBuf buf) {
      return new PacketUpdatePhysics(buf.readInt(), SpawnEntity.decode(buf));
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel w = Minecraft.getInstance().level;
      if (w != null) {
         Entity entity = w.getEntity(id);
         if (entity == null) { SpawnEntity.handle(pkt, ctx); }
         if (PhysicsHelper.Enabled) { PhysicsHelper.resetEntityPhysics(w, id); }
      }
      CustomNpcs.debugData.end("Packets");
   }

   static {
      Constructor<SpawnEntity> con = null;
      try {
         con = SpawnEntity.class.getDeclaredConstructor(Entity.class);
         con.setAccessible(true);
      }
      catch (NoSuchMethodException e) { LogWriter.error(e); }
      constr = con;
   }
}
