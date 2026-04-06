package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketWindowSize extends PacketServerBasic {

    protected static int channelId;
    private final double width;
    private final double height;

    public SPacketWindowSize(double widthIn, double heightIn) { width = widthIn; height = heightIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketWindowSize msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.width);
        buf.writeDouble(msg.height);
    }

    public static SPacketWindowSize decode(FriendlyByteBuf buf) { return new SPacketWindowSize(buf.readDouble(), buf.readDouble()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.getWindowSize().setSize(width, height);
        CustomNpcs.debugData.end("Packets");
    }

}
