package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;

public class SPacketMarcetDelete extends PacketServerBasic {

    protected static int channelId;
    private int marcetId;

    public SPacketMarcetDelete() { }

    public SPacketMarcetDelete(int marcetIDIn) { marcetId = marcetIDIn; }

    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_MARKETS; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(marcetId); }

    @Override
    public void decode(FriendlyByteBuf buf) { marcetId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().removeMarcet(marcetId);
        Packets.sendAll(new PacketSyncRemove(marcetId, 6));
        CustomNpcs.debugData.end("Packets");
    }

}