package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketSceneReset extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.SCENES); }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketSceneReset ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketSceneReset decode(FriendlyByteBuf ignoredBuf) { return new SPacketSceneReset(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (CustomNpcs.SceneButtonsEnabled) { DataScenes.Reset(null, npc); }
      CustomNpcs.debugData.end("Packets");
   }

}
