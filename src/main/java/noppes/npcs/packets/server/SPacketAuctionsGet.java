package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketAuctionsGet extends PacketServerBasic {

    protected static int channelId;

    public SPacketAuctionsGet() { }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return new ArrayList<>(Collections.singletonList(CustomNpcsPermissions.GLOBAL_AUCTIONS)); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf ignoreBuf) { }

    @Override
    public void decode(FriendlyByteBuf ignoreBuf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        AuctionController.getInstance().sendAllTo(player);
        CustomNpcs.debugData.end("Packets");
    }

}
