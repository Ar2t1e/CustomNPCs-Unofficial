package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;

public class PacketBankSetPlayer extends PacketBasic {

    protected static int channelId;
    public String name;

    public PacketBankSetPlayer() { }

    public PacketBankSetPlayer(@Nonnull String nameIn) { name = nameIn; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
