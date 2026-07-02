package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBorderClear extends PacketBasic {

    protected static int channelId;

    public static void encode(PacketBorderClear ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static PacketBorderClear decode(FriendlyByteBuf ignoredBuf) { return new PacketBorderClear(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        BorderController.getInstance().regions.clear();
        CustomNpcs.debugData.end("Packets");
    }

}