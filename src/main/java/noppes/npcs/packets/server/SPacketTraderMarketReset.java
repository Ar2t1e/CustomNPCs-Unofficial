package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketTraderMarketReset extends PacketServerBasic {

    protected static int channelId;
    private final int marcetID;

    public SPacketTraderMarketReset(int marcetIDIn) { marcetID = marcetIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketTraderMarketReset msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetID); }

    public static SPacketTraderMarketReset decode(FriendlyByteBuf buf) { return new SPacketTraderMarketReset(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Marcet marcet = MarcetController.getInstance().getMarcet(marcetID);
        if (marcet != null) { marcet.updateNew(); }
        CustomNpcs.debugData.end("Packets");
    }

}