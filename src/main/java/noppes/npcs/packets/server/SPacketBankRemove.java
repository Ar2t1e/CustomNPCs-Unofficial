package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketBankRemove extends PacketServerBasic {

   protected static int channelId;
   private int bankId;

   public SPacketBankRemove() { }

   public SPacketBankRemove(int bankIdIn) { bankId = bankIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_BANK); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(bankId); }

   @Override
   public void decode(FriendlyByteBuf buf) { bankId = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BankController.getInstance().removeBank(bankId);
      CustomNpcs.debugData.end("Packets");
   }

}
