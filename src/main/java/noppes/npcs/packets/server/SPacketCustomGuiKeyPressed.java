package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketCustomGuiKeyPressed extends PacketServerBasic {

    protected static int channelId;
    private int keyId;

    public SPacketCustomGuiKeyPressed() { }

    public SPacketCustomGuiKeyPressed(int keyIdIn) { keyId = keyIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(keyId); }

    @Override
    public void decode(FriendlyByteBuf buf) { keyId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerCustomGui) {
            EventHooks.onCustomGuiKeyPressed(iPlayer, ((ContainerCustomGui) player.openContainer).activeGui, keyId);
        }
        CustomNpcs.debugData.end("Packets");
    }

}