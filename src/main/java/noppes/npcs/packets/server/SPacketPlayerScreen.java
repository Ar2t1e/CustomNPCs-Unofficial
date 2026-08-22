package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerScreen extends PacketServerBasic {

    protected static int channelId;
    private String newScreen;
    private String oldScreen;

    public SPacketPlayerScreen() { }

    public SPacketPlayerScreen(String newScreenName, String oldScreenName) {
        newScreen = newScreenName;
        oldScreen = oldScreenName;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(newScreen);
        buf.writeUtf(oldScreen);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        newScreen = buf.readUtf();
        oldScreen = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        EventHooks.onPlayerScreen(player, newScreen, oldScreen);
        CustomNpcs.debugData.end("Packets");
    }

}
