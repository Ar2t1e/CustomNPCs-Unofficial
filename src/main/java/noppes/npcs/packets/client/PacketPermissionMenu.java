package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPermissionMenu extends PacketBasic {

    protected static int channelId;
    public boolean display;
    public boolean stats;
    public boolean ai;
    public boolean inventory;
    public boolean advanced;

    public PacketPermissionMenu() { }

    public PacketPermissionMenu(boolean displayIn, boolean statsIn, boolean aiIn, boolean inventoryIn, boolean advancedIn) {
        display = displayIn;
        stats = statsIn;
        ai = aiIn;
        inventory = inventoryIn;
        advanced = advancedIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        display = buf.readBoolean();
        stats = buf.readBoolean();
        ai = buf.readBoolean();
        inventory = buf.readBoolean();
        advanced = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(display);
        buf.writeBoolean(stats);
        buf.writeBoolean(ai);
        buf.writeBoolean(inventory);
        buf.writeBoolean(advanced);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
