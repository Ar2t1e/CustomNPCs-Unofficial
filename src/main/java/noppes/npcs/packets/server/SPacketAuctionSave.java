package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.controllers.data.AuctionHouse;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketAuctionSave extends PacketServerBasic {

    protected static int channelId;
    private int houseId;
    private NBTTagCompound compound;

    public SPacketAuctionSave() { }

    public SPacketAuctionSave(int houseIdIn, NBTTagCompound compoundIn) {
        houseId = houseIdIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return new ArrayList<>(Collections.singletonList(CustomNpcsPermissions.GLOBAL_AUCTIONS)); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(houseId);
        buf.writeNbt(compound);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        houseId = buf.readInt();
        compound = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        AuctionController controller = AuctionController.getInstance();
        if (compound != null && compound.hasKey("HouseID", 3)) {
            int id = compound.getInteger("HouseID");
            AuctionHouse auction = controller.getAuction(id);
            if (auction == null) { auction = controller.addAuction(); } // new
            if (auction != null) {
                auction.load(compound, false);
                controller.save(auction.getId());
            } // reset
        }
        CustomNpcs.debugData.end("Packets");
    }

}
