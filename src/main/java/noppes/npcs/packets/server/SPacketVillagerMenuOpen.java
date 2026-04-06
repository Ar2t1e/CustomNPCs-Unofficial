package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketVillagerMenuOpen extends PacketServerBasic {

    protected static int channelId;
    private final int entityId;

    public SPacketVillagerMenuOpen(int entityIdIn) { entityId = entityIdIn; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.EDIT_VILLAGER; }

    public static void encode(SPacketVillagerMenuOpen msg, FriendlyByteBuf buf) { buf.writeInt(msg.entityId); }

    public static SPacketVillagerMenuOpen decode(FriendlyByteBuf buf) { return new SPacketVillagerMenuOpen(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof Villager) {
            NoppesUtilServer.setEditingNpc(player, npc);
            NoppesUtilServer.openContainerGui(player, EnumGuiType.MerchantAdd, (buffer) -> buffer.writeInt(entityId));
        }
        CustomNpcs.debugData.end("Packets");
    }

}