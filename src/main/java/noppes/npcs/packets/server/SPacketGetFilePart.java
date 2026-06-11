package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSendFilePart;
import noppes.npcs.util.TempFile;

import java.util.List;

public class SPacketGetFilePart extends PacketServerBasic {

    protected static int channelId;
    private final int partId;
    private final String name;

    public SPacketGetFilePart(int partIdIn, String nameIn) {
        partId = partIdIn;
        name = nameIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketGetFilePart msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.partId);
        buf.writeUtf(msg.name);
    }

    public static SPacketGetFilePart decode(FriendlyByteBuf buf) { return new SPacketGetFilePart(buf.readInt(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        if (!data.clientScriptFiles.containsKey(name)) {
            Packets.send(player, new PacketSendFilePart(true, 0, name, ""));
        } else {
            TempFile file = data.clientScriptFiles.get(name);
            Packets.send(player, new PacketSendFilePart(false, partId, name, String.valueOf(file.data.get(partId))));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
