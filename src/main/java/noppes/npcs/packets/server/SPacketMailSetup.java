package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMailSetup extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketMailSetup(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   public static void encode(SPacketMailSetup msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketMailSetup decode(FriendlyByteBuf buf) { return new SPacketMailSetup(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerMail mail = new PlayerMail();
      mail.load(data);
      ContainerMail.staticMail = mail;
      NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerMailOpen, buffer -> {
         buffer.writeBoolean(true);
         buffer.writeBoolean(false);
      });
      CustomNpcs.debugData.end("Packets");
   }

}
