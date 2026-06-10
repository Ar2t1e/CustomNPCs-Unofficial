package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketBankSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;
   private final int ceil;

   public SPacketBankSave(int ceilIn, CompoundTag dataIn) {
      ceil = ceilIn;
      data = dataIn;
   }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_BANK); }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return false; }

   public static void encode(SPacketBankSave msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.ceil);
      buf.writeNbt(msg.data);
   }

   public static SPacketBankSave decode(FriendlyByteBuf buf) { return new SPacketBankSave(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BankController.getInstance().loadBank(data);
      CustomNpcs.debugData.end("Packets");
   }

}
