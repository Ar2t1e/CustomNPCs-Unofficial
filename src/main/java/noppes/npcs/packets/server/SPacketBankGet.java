package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.List;

public class SPacketBankGet extends PacketServerBasic {

   protected static int channelId;
   private final int bank;
   private final int ceil;

   public SPacketBankGet(int bankIn, int ceilIn) {
      bank = bankIn;
      ceil = ceilIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   public static void encode(SPacketBankGet msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.bank);
      buf.writeInt(msg.ceil);
   }

   public static SPacketBankGet decode(FriendlyByteBuf buf) { return new SPacketBankGet(buf.readInt(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendBank(player, BankController.getInstance().getBank(bank), ceil);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendBank(ServerPlayer player, Bank bank, int ceil) {
      Packets.send(player, new PacketGuiData(bank.save()));
      if (player.containerMenu instanceof ContainerManageBanks container) { container.setBank(bank, ceil); }
      player.containerMenu.sendAllDataToRemote();
   }

}
