package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;

import java.util.Collections;
import java.util.List;

public class SPacketDealDelete extends PacketServerBasic {

    protected static int channelId;
    private int dealId;

    public SPacketDealDelete() { }

    public SPacketDealDelete(int dealIDIn) { dealId = dealIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MARKETS); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(dealId); }

    @Override
    public void decode(FriendlyByteBuf buf) { dealId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().removeDeal(dealId);
        Packets.sendAll(new PacketSyncRemove(dealId, 7));
        CustomNpcs.debugData.end("Packets");
    }

}