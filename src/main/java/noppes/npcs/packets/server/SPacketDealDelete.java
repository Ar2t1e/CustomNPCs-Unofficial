package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
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
    private final int dealId;

    public SPacketDealDelete(int dealIDIn) { dealId = dealIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MARKETS); }

    public static void encode(SPacketDealDelete msg, FriendlyByteBuf buf) { buf.writeInt(msg.dealId); }

    public static SPacketDealDelete decode(FriendlyByteBuf buf) { return new SPacketDealDelete(buf.readInt()); }

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