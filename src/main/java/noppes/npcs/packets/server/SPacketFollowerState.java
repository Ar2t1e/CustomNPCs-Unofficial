package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
   private boolean isWaiting;

   public SPacketFollowerState() { }

   public SPacketFollowerState(boolean isWaitingIn) { isWaiting = isWaitingIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeBoolean(isWaiting);}

   @Override
   public void decode(FriendlyByteBuf buf) { isWaiting = buf.readBoolean(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role instanceof RoleFollower && ((RoleFollower) npc.role).owner != null) {
         RoleFollower role = (RoleFollower) npc.role;
         if (isWaiting) {
            if (role.owner.getName().equals(player.getName())) { role.isFollowing = !role.isFollowing; }
            Packets.send(player, new PacketGuiData(npc.role.save(new NBTTagCompound())));
         }
         else {
            RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(role.owner, npc.wrappedNPC);
            EventHooks.onNPCRole(npc, event);
            role.owner.sendMessage(Component.translatable(NoppesStringUtils.formatText(role.dialogFired, role.owner, npc)));
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
