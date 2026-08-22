package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketBankSave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;
   private int ceil;

   public SPacketBankSave() { }

   public SPacketBankSave(int ceilIn, NBTTagCompound dataIn) {
      ceil = ceilIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_BANK); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(ceil);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      ceil = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BankController.getInstance().loadBank(data);
      CustomNpcs.debugData.end("Packets");
   }

}
