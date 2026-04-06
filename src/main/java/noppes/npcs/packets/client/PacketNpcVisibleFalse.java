package noppes.npcs.packets.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PacketNpcVisibleFalse extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final UUID uuid;

   private PacketNpcVisibleFalse(int idIn, UUID uuidIn) {
      id = idIn;
      uuid = uuidIn;
   }

   public PacketNpcVisibleFalse(@Nonnull Entity entityIn) {
      id = entityIn.getId();
      uuid = entityIn.getUUID();
   }

   public static void encode(PacketNpcVisibleFalse msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeUUID(msg.uuid);
   }

   public static PacketNpcVisibleFalse decode(FriendlyByteBuf buf) { return new PacketNpcVisibleFalse(buf.readInt(), buf.readUUID()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ClientLevel level = (ClientLevel) player.level();
      Entity entity = level.getEntity(this.id);
      if (entity instanceof EntityNPCInterface) { level.removeEntity(id, RemovalReason.DISCARDED); }
      CustomNpcs.debugData.end("Packets");
   }

}
