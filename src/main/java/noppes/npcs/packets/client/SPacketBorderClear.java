package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBorderClear extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketBorderClear ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketBorderClear decode(FriendlyByteBuf ignoredBuf) { return new SPacketBorderClear(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        BorderController.getInstance().regions.clear();
        CustomNpcs.debugData.end("Packets");
    }

}