package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneNameCheck extends PacketServerBasic {

   protected static int channelId;
   private final String name;
   private final int tab;

   public SPacketCloneNameCheck(String nameIn, int tabIn) {
      name = nameIn;
      tab = tabIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.cloner; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_CLONE; }

   public static void encode(SPacketCloneNameCheck msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.name);
      buf.writeInt(msg.tab);
   }

   public static SPacketCloneNameCheck decode(FriendlyByteBuf buf) { return new SPacketCloneNameCheck(buf.readUtf(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("NameExists", ServerCloneController.Instance.getCloneData(null, name, tab) != null);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
