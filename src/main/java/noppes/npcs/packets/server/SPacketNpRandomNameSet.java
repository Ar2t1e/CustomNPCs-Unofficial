package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketNpRandomNameSet extends PacketServerBasic {

   protected static int channelId;
   private int id;
   private int gender;

   public SPacketNpRandomNameSet() { }

   public SPacketNpRandomNameSet(int idIn, int genderIn) {
      id = idIn;
      gender = genderIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_DISPLAY); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeInt(gender);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      gender = buf.readInt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.display.setMarkovGeneratorId(id);
      npc.display.setMarkovGender(gender);
      npc.display.setName(npc.display.getRandomName());
      NBTTagCompound data = new NBTTagCompound();
      npc.display.save(data);
      Packets.send(player, new PacketGuiData(data));
      CustomNpcs.debugData.end("Packets");
   }

}
