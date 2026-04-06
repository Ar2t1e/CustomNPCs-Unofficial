package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketScreenSize extends PacketServerBasic {

    protected static int channelId;
    public int width;
    public int height;

    public SPacketScreenSize(int widthIn, int heightIn) {
        width = widthIn;
        height = heightIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketScreenSize msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.width);
        buf.writeInt(msg.height);
    }

    public static SPacketScreenSize decode(FriendlyByteBuf buf) { return new SPacketScreenSize(buf.readInt(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.getWindowSize().setSize(width, height);
        CustomNpcs.debugData.end("Packets");
    }

}
