package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerGameData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleFollower;

public class SPacketFollowerState extends PacketServerBasic {

   protected static int channelId;
   private final boolean isWaiting;

   public SPacketFollowerState(boolean isWaitingIn) { isWaiting = isWaitingIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketFollowerState msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isWaiting);  }

   public static SPacketFollowerState decode(FriendlyByteBuf buf) { return new SPacketFollowerState(buf.readBoolean()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role instanceof RoleFollower role && role.owner != null) {
         if (isWaiting) {
            if (role.owner.getName().equals(player.getName())) { role.isFollowing = !role.isFollowing; }
            Packets.send(player, new PacketGuiData(npc.role.save(new CompoundTag())));
         }
         else {
            RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(role.owner, npc.wrappedNPC);
            EventHooks.onNPCRole(npc, event);
            role.owner.sendSystemMessage(Component.translatable(NoppesStringUtils.formatText(role.dialogFired, role.owner, npc)));
            PlayerData data = PlayerData.get(player);
            if (data != null) {
               PlayerGameData.FollowerSet fs = data.game.getFollower(role.npc);
               if (fs != null) { data.game.removeFollower(role.npc); }
            }
            role.killed();
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
