package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTraderLivePlayer extends PacketServerBasic {

    protected static int channelId;
    private final int marcetId;

    public SPacketTraderLivePlayer(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketTraderLivePlayer msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetId); }

    public static SPacketTraderLivePlayer decode(FriendlyByteBuf buf) { return new SPacketTraderLivePlayer(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Marcet marcet = MarcetController.getInstance().getMarcet(marcetId);
        if (marcet != null) { marcet.removeListener(player, true); }
        CustomNpcs.debugData.end("Packets");
    }

}