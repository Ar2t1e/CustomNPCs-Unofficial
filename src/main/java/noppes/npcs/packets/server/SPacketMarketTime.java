package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketMarketTime extends PacketServerBasic {

    protected static int channelId;
    private int marcetId;

    public SPacketMarketTime() { }

    public SPacketMarketTime(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(marcetId); }

    @Override
    public void decode(FriendlyByteBuf buf) { marcetId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().sendTo(player, marcetId);
        CustomNpcs.debugData.end("Packets");
    }

}
