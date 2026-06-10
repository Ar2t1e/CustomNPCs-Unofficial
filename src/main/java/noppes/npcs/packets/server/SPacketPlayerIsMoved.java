package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerIsMoved extends PacketServerBasic {

    protected static int channelId;
    private final boolean isMoved;

    public SPacketPlayerIsMoved(boolean isMovedIn) { isMoved = isMovedIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketPlayerIsMoved msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isMoved); }

    public static SPacketPlayerIsMoved decode(FriendlyByteBuf buf) { return new SPacketPlayerIsMoved(buf.readBoolean()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.isMoved = isMoved;
        CustomNpcs.debugData.end("Packets");
    }

}