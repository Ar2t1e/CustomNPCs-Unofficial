package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSceneStart extends PacketServerBasic {

   protected static int channelId;
   private final int scene;

   public SPacketSceneStart(int sceneIn) { scene = sceneIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.SCENES; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketSceneStart msg, FriendlyByteBuf buf) { buf.writeInt(msg.scene); }

   public static SPacketSceneStart decode(FriendlyByteBuf buf) { return new SPacketSceneStart(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (CustomNpcs.SceneButtonsEnabled) { DataScenes.Toggle(scene + "btn"); }
      CustomNpcs.debugData.end("Packets");
   }

}
