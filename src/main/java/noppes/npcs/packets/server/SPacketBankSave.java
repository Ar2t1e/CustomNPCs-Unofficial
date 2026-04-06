package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.PacketServerBasic;

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
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_BANK; }

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
