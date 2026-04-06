package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerScreen extends PacketServerBasic {

    protected static int channelId;
    private final String newScreen;
    private final String oldScreen;

    public SPacketPlayerScreen(String newScreenName, String oldScreenName) {
        newScreen = newScreenName;
        oldScreen = oldScreenName;
    }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    public static void encode(SPacketPlayerScreen msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.newScreen);
        buf.writeUtf(msg.oldScreen);
    }

    public static SPacketPlayerScreen decode(FriendlyByteBuf buf) { return new SPacketPlayerScreen(buf.readUtf(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        EventHooks.onPlayerScreen(player, newScreen, oldScreen);
        CustomNpcs.debugData.end("Packets");
    }

}
