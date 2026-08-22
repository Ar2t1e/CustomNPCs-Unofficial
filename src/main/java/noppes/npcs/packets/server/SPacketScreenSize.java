package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketScreenSize extends PacketServerBasic {

    protected static int channelId;
    public int width;
    public int height;

    public SPacketScreenSize() { }

    @SuppressWarnings("unused")
    public SPacketScreenSize(int widthIn, int heightIn) {
        width = widthIn;
        height = heightIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(width);
        buf.writeInt(height);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        width = buf.readInt();
        height = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.screenSize.setSize(width, height);
        CustomNpcs.debugData.end("Packets");
    }

}
