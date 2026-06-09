package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketVillagerMenuOpen extends PacketServerBasic {

    protected static int channelId;
    private int entityId;

    public SPacketVillagerMenuOpen() { }

    public SPacketVillagerMenuOpen(int entityIdIn) { entityId = entityIdIn; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.EDIT_VILLAGER; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(entityId); }

    @Override
    public void decode(FriendlyByteBuf buf) { entityId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.world.getEntityByID(entityId);
        if (entity instanceof EntityVillager) {
            NoppesUtilServer.openContainerGui(player, EnumGuiType.MerchantAdd, (buffer) -> buffer.writeInt(entityId));
        }
        CustomNpcs.debugData.end("Packets");
    }

}