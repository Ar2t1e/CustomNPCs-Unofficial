package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketScriptError extends PacketBasic {

    protected static int channelId;
    public ITextComponent component;

    public PacketScriptError() { }

    public PacketScriptError(Component dataIn) { component = dataIn.getParent(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeComponent(component); }

    @Override
    public void decode(FriendlyByteBuf buf) { component = buf.readComponent(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}