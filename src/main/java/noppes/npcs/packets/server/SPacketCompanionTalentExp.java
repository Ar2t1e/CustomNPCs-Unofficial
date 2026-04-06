package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketCompanionTalentExp extends PacketServerBasic {

   protected static int channelId;
   private final EnumCompanionTalent talent;
   private final int exp;

   public SPacketCompanionTalentExp(EnumCompanionTalent talentIn, int expIn) {
      talent = talentIn;
      exp = expIn;
   }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketCompanionTalentExp msg, FriendlyByteBuf buf) {
      buf.writeEnum(msg.talent);
      buf.writeInt(msg.exp);
   }

   public static SPacketCompanionTalentExp decode(FriendlyByteBuf buf) {
      return new SPacketCompanionTalentExp(buf.readEnum(EnumCompanionTalent.class), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role.getType() == 6 && player == npc.getOwner()) {
         RoleCompanion role = (RoleCompanion)npc.role;
         if (exp > 0 && role.canAddExp(-exp)) {
            role.addExp(-exp);
            role.addTalentExp(talent, exp);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
