package noppes.npcs.packets.server;

import java.util.Objects;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcJobSave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public SPacketNpcJobSave() { }

   public SPacketNpcJobSave(NBTTagCompound dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NBTTagCompound original = npc.job.save(new NBTTagCompound());
      Set<String> names = data.getKeySet();
      for (String name : names) { original.setTag(name, Objects.requireNonNull(data.getTag(name))); }
      npc.job.load(original);
      npc.updateClient = true;
      CustomNpcs.debugData.end("Packets");
   }

}
