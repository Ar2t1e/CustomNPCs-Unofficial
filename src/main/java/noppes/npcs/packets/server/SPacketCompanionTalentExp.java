package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleCompanion;

public class SPacketCompanionTalentExp extends PacketServerBasic {

   protected static int channelId;
   private EnumCompanionTalent talent;
   private int exp;

   public SPacketCompanionTalentExp() { }

   public SPacketCompanionTalentExp(EnumCompanionTalent talentIn, int expIn) {
      talent = talentIn;
      exp = expIn;
   }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(talent);
      buf.writeInt(exp);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      talent = buf.readEnum(EnumCompanionTalent.class);
      exp = buf.readInt();
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
