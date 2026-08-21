package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcLookPos extends PacketBasic {

    protected static int channelId;
    public int dimensionId;
    public int npcId;
    public int lookId;

    public PacketNpcLookPos() { }

    public PacketNpcLookPos(int dimensionIdIn, int npcIdIn, int lookIdIn) {
        dimensionId = dimensionIdIn;
        npcId = npcIdIn;
        lookId = lookIdIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dimensionId = buf.readInt();
        npcId = buf.readInt();
        lookId = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(npcId);
        buf.writeInt(lookId);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}