package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketRemoveLoadFile extends PacketServerBasic {

    protected static int channelId;
    private final String name;

    public SPacketRemoveLoadFile(String nameIn) { name = nameIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketRemoveLoadFile msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

    public static SPacketRemoveLoadFile decode(FriendlyByteBuf buf) { return new SPacketRemoveLoadFile(buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).clientScriptFiles.remove(name);
        CustomNpcs.debugData.end("Packets");
    }

}