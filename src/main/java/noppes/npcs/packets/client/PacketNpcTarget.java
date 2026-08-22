package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcTarget extends PacketBasic {

    protected static int channelId;
    public int entityId;
    public int targetId;

    public PacketNpcTarget() { }

    public PacketNpcTarget(int entityIdIn, int targetIdIn) {
        entityId = entityIdIn;
        targetId = targetIdIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        targetId = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(targetId);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}