package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDebug extends PacketBasic {

    protected static int channelId;
    private final boolean isLogOrClear;

    public PacketDebug(boolean isLogOrClearOn) {
        isLogOrClear = isLogOrClearOn;
    }

    public static void encode(PacketDebug msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isLogOrClear); }

    public static PacketDebug decode(FriendlyByteBuf buf) { return new PacketDebug(buf.readBoolean()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        if (isLogOrClear) { CustomNpcs.debugData.logging(); }
        else { CustomNpcs.debugData.clear(); }
    }

}
