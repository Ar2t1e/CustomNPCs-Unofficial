package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketWindowSize extends PacketServerBasic {

    protected static int channelId;
    private double width;
    private double height;

    public SPacketWindowSize() { }

    public SPacketWindowSize(double widthIn, double heightIn) { width = widthIn; height = heightIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(width);
        buf.writeDouble(height);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        width = buf.readDouble();
        height = buf.readDouble();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.getWindowSize().setSize(width, height);
        CustomNpcs.debugData.end("Packets");
    }

}
