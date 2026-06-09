package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketOpenParts extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_GUI; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        //ModelMenu.open(player, (EntityCustomNpc) npc);
        CustomNpcs.debugData.end("Packets");
    }

}
