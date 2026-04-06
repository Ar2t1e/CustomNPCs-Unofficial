package noppes.npcs.packets.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.shared.common.PacketBasic;

import java.util.Objects;

public class PacketItemUpdate extends PacketBasic {

   protected static int channelId;
   private int id;
   private NBTTagCompound data;

   public PacketItemUpdate() { }

   public PacketItemUpdate(int idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ItemStack stack = player.inventory.getStackInSlot(id);
      if (!stack.isEmpty()) {
         ((ItemStackWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack)).setMCNbt(data);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
