package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
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
   private final int id;
   private final int gender;

   public SPacketNpRandomNameSet(int idIn, int genderIn) {
      id = idIn;
      gender = genderIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_DISPLAY); }

   public static void encode(SPacketNpRandomNameSet msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeInt(msg.gender);
   }

   public static SPacketNpRandomNameSet decode(FriendlyByteBuf buf) { return new SPacketNpRandomNameSet(buf.readInt(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.display.setMarkovGeneratorId(id);
      npc.display.setMarkovGender(gender);
      npc.display.setName(npc.display.getRandomName());
      CompoundTag data = new CompoundTag();
      npc.display.save(data);
      Packets.send(player, new PacketGuiData(data));
      CustomNpcs.debugData.end("Packets");
   }

}
