package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketMarketTime extends PacketServerBasic {

    protected static int channelId;
    private final int marcetId;

    public SPacketMarketTime(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketMarketTime msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetId); }

    public static SPacketMarketTime decode(FriendlyByteBuf buf) { return new SPacketMarketTime(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().sendTo(player, marcetId);
        CustomNpcs.debugData.end("Packets");
    }

}
