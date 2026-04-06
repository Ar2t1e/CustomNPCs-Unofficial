package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNbtBookEntitySave extends PacketServerBasic {

   protected static int channelId;
   private final int id;
   private final CompoundTag data;

   public SPacketNbtBookEntitySave(int idIn, CompoundTag dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.nbt_book; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.TOOL_NBTBOOK; }

   public static void encode(SPacketNbtBookEntitySave msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeNbt(msg.data);
   }

   public static SPacketNbtBookEntitySave decode(FriendlyByteBuf buf) { return new SPacketNbtBookEntitySave(buf.readInt(), buf.readAnySizeNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.level().getEntity(id);
      if (entity != null) { entity.load(data); }
      CustomNpcs.debugData.end("Packets");
   }

}
