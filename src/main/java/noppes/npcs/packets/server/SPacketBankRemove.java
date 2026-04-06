package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankRemove extends PacketServerBasic {

   protected static int channelId;
   private final int bankId;

   public SPacketBankRemove(int bankIdIn) { bankId = bankIdIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_BANK; }

   public static void encode(SPacketBankRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.bankId); }

   public static SPacketBankRemove decode(FriendlyByteBuf buf) { return new SPacketBankRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BankController.getInstance().removeBank(bankId);
      CustomNpcs.debugData.end("Packets");
   }

}
