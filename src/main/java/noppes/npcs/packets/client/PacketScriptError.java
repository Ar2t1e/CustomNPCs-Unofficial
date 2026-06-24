package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketScriptError extends PacketBasic {

    protected static int channelId;
    private ITextComponent component;

    public PacketScriptError() { }

    public PacketScriptError(Component dataIn) { component = dataIn.getParent(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeComponent(component); }

    @Override
    public void decode(FriendlyByteBuf buf) { component = buf.readComponent(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (CustomNpcs.DisplayErrorInChat && component != null && !component.getFormattedText().isEmpty()) { player.sendMessage(component); }
        CustomNpcs.debugData.end("Packets");
    }

}