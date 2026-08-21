package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSendFilePart extends PacketBasic {

    protected static int channelId;
    public boolean remove;
    public int partId;
    public String name;
    public String partText;

    public PacketSendFilePart() { }

    public PacketSendFilePart(boolean isRemove, int part, String nameIn, String partTextIn) {
        remove = isRemove;
        partId = part;
        name = nameIn;
        partText = partTextIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        remove = buf.readBoolean();
        partId = buf.readInt();
        name = buf.readUtf();
        partText = buf.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(remove);
        buf.writeInt(partId);
        buf.writeUtf(name);
        buf.writeUtf(partText);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
