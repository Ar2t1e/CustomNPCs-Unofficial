package noppes.npcs.packets.server;

import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcJobSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketNpcJobSave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketNpcJobSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketNpcJobSave decode(FriendlyByteBuf buf) { return new SPacketNpcJobSave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CompoundTag original = npc.job.save(new CompoundTag());
      Set<String> names = data.getAllKeys();
      for (String name : names) { original.put(name, Objects.requireNonNull(data.get(name))); }
      npc.job.load(original);
      npc.updateClient = true;
      CustomNpcs.debugData.end("Packets");
   }

}
