package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketStopSound extends PacketBasic {

    protected static int channelId;
    public int category;
    public ResourceLocation sound;

    public PacketStopSound() { }

    public PacketStopSound(ResourceLocation soundIn, int categoryIn) {
        sound = soundIn;
        category = categoryIn;
        if (category < 0) { category *= -1; }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        sound = buf.readResourceLocation();
        category = buf.readInt();
        if (category < 0) { category *= -1; }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(sound);
        buf.writeInt(category);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}