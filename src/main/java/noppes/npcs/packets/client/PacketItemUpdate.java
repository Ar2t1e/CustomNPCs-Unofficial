package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.shared.common.PacketBasic;

import java.util.Objects;

public class PacketItemUpdate extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final CompoundTag data;

   public PacketItemUpdate(int idIn, CompoundTag dataIn) {
      id = idIn;
      data = dataIn;
   }

   public static void encode(PacketItemUpdate msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeNbt(msg.data);
   }

   public static PacketItemUpdate decode(FriendlyByteBuf buf) { return new PacketItemUpdate(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ItemStack stack = player.getInventory().getItem(id);
      if (!stack.isEmpty()) {
         ((ItemStackWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack)).setMCNbt(data);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
