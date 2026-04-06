package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketNpcRoleCompanionUpdate extends PacketServerBasic {

   protected static int channelId;
   private EnumCompanionStage stage;

   public SPacketNpcRoleCompanionUpdate() { }

   public SPacketNpcRoleCompanionUpdate(EnumCompanionStage stageIn) { stage = stageIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeEnum(stage); }

   @Override
   public void decode(FriendlyByteBuf buf) { stage = buf.readEnum(EnumCompanionStage.class); }

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
