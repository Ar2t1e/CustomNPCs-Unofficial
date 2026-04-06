package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;

public class SPacketMarcetDelete extends PacketServerBasic {

    protected static int channelId;
    private final int marcetId;

    public SPacketMarcetDelete(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_MARKETS; }

    public static void encode(SPacketMarcetDelete msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetId); }

    public static SPacketMarcetDelete decode(FriendlyByteBuf buf) { return new SPacketMarcetDelete(buf.readInt()); }

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