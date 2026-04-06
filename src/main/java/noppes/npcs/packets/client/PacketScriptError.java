package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketScriptError extends PacketBasic {

    protected static int channelId;
    private final Component component;

    public PacketScriptError(Component dataIn) { component = dataIn; }

    public static void encode(PacketScriptError msg, FriendlyByteBuf buf) { buf.writeComponent(msg.component); }

    public static PacketScriptError decode(FriendlyByteBuf buf) { return new PacketScriptError(buf.readComponent()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (CustomNpcs.DisplayErrorInChat && component != null && !component.getString().isEmpty()) { player.sendSystemMessage(component); }
        CustomNpcs.debugData.end("Packets");
    }

}