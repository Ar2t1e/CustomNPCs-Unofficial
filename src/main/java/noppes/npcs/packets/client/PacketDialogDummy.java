package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialogDummy extends PacketBasic {

   protected static int channelId;
   private final String name;
   private final CompoundTag data;

   public PacketDialogDummy(String nameIn, CompoundTag dataIn) {
      name = nameIn;
      data = dataIn;
   }

   public static void encode(PacketDialogDummy msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.name);
      buf.writeNbt(msg.data);
   }

   public static PacketDialogDummy decode(FriendlyByteBuf buf) {
      return new PacketDialogDummy(buf.readUtf(32767), buf.readNbt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      EntityDialogNpc npc = new EntityDialogNpc(player.level());
      npc.display.setName(Component.translatable(name).toString());
      EntityUtil.Copy(player, npc);
      Dialog dialog = new Dialog(null);
      dialog.load(data);
      PacketDialog.openDialog(dialog, npc, player);
      CustomNpcs.debugData.end("Packets");
   }

}
