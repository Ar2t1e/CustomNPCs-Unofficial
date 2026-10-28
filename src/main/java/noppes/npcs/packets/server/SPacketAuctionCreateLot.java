package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.controllers.data.AuctionLot;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketAuctionCreateLot extends PacketServerBasic {

    protected static int channelId;
    private int auctionId;
    private int count;
    private AuctionLot lot;

    public SPacketAuctionCreateLot() { }

    public SPacketAuctionCreateLot(int auctionIdIn, AuctionLot lotIn, int countIn) {
        auctionId = auctionIdIn;
        count = countIn;
        lot = lotIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(auctionId);
        buf.writeInt(count);
        buf.writeNbt(lot.save());
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        auctionId = buf.readInt();
        count = buf.readInt();
        lot = new AuctionLot(0L);
        lot.load(buf.readNbt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        AuctionController.getInstance().createLots(player, lot, auctionId, count);
        CustomNpcs.debugData.end("Packets");
    }

}
