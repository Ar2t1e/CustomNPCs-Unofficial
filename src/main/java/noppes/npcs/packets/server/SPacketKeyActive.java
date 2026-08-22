package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketKeyActive extends PacketServerBasic {

    protected static int channelId;
    private int id;

    public SPacketKeyActive() { }

    public SPacketKeyActive(int idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        EventHooks.onPlayerKeyActive(player, id);
        CustomNpcs.debugData.end("Packets");
    }

}
