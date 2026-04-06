package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;

public class SPacketDialogSelected extends PacketServerBasic {

   protected static int channelId;
   private final int dialogId;
   private final int optionId;

   public SPacketDialogSelected(int dialogIdIn, int optionIdIn) {
      dialogId = dialogIdIn;
      optionId = optionIdIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketDialogSelected msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.dialogId);
      buf.writeInt(msg.optionId);
   }

   public static SPacketDialogSelected decode(FriendlyByteBuf buf) { return new SPacketDialogSelected(buf.readInt(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerData data = PlayerData.get(player);
      if (data.dialogId == dialogId) {
         if (data.dialogId < 0 && npc.role.getEnumType() == RoleType.DIALOG) {
            String text = ((RoleDialog)npc.role).optionsTexts.get(optionId);
            if (text != null && !text.isEmpty()) {
               Dialog d = new Dialog(null);
               d.text = text;
               NoppesUtilServer.openDialog(player, npc, d);
            }
         } else {
            Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
            if (dialog != null) {
               if (!dialog.hasDialogs(player) && dialog.notHasOtherOptions()) {
                  closeDialog(player, npc, true);
               } else {
                  DialogOption option = dialog.options.get(optionId);
                  if (option != null &&
                          !EventHooks.onNPCDialogOption(npc, player, dialog, option) &&
                          (option.optionType != OptionType.DIALOG_OPTION || option.isAvailable(player) && option.hasDialogs()) &&
                          option.optionType != OptionType.DISABLED && option.optionType != OptionType.QUIT_OPTION) {
                     if (option.optionType == OptionType.ROLE_OPTION) {
                        closeDialog(player, npc, true);
                        if (npc.role.getEnumType() == RoleType.COMPANION) {
                           ((RoleCompanion)npc.role).interact(player, true);
                        } else {
                           npc.role.interact(player);
                        }
                     } else if (option.optionType == OptionType.DIALOG_OPTION) {
                        closeDialog(player, npc, false);
                        NoppesUtilServer.openDialog(player, npc, option.getDialog(player));
                     } else if (option.optionType == OptionType.COMMAND_BLOCK) {
                        closeDialog(player, npc, true);
                        NoppesUtilServer.runCommand(npc, npc.getName().getString(), option.command, player);
                     } else {
                        closeDialog(player, npc, true);
                     }
                  } else {
                     closeDialog(player, npc, true);
                  }
               }
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

   public void closeDialog(ServerPlayer player, EntityNPCInterface npc, boolean notifyClient) {
      PlayerData data = PlayerData.get(player);
      Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
      EventHooks.onNPCDialogClose(npc, player, dialog);
      if (notifyClient) {
         Packets.send(player, new PacketGuiClose(new CompoundTag()));
      }

      data.dialogId = -1;
   }
}
