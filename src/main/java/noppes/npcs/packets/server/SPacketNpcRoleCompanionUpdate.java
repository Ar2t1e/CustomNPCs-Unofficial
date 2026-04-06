package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketNpcRoleCompanionUpdate extends PacketServerBasic {

   protected static int channelId;
   private final EnumCompanionStage stage;

   public SPacketNpcRoleCompanionUpdate(EnumCompanionStage stageIn) { stage = stageIn; }

   @Override
   public boolean requiresNpc() { return true; }

   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketNpcRoleCompanionUpdate msg, FriendlyByteBuf buf) { buf.writeEnum(msg.stage); }

   public static SPacketNpcRoleCompanionUpdate decode(FriendlyByteBuf buf) {
      return new SPacketNpcRoleCompanionUpdate(buf.readEnum(EnumCompanionStage.class));
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role.getEnumType() == RoleType.COMPANION) {
         ((RoleCompanion) npc.role).matureTo(stage);
         npc.updateClient = true;
      }
      CustomNpcs.debugData.end("Packets");
   }

}
