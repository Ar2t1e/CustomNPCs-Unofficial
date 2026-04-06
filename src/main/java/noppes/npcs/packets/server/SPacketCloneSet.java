package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCloneSet extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag data;

    public SPacketCloneSet(CompoundTag dataIn) { data = dataIn; }

    @Override
    public boolean requiresNpc() { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_CLONE; }

    public static void encode(SPacketCloneSet msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

    public static SPacketCloneSet decode(FriendlyByteBuf buf) { return new SPacketCloneSet(buf.readAnySizeNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).cloned = data;
        CustomNpcs.debugData.end("Packets");
    }

}