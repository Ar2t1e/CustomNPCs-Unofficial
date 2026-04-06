package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPermissionGlobal;

public class SPacketPermissionGlobalGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Packets.send(player, new PacketPermissionGlobal(
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_BANK),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_FACTION),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_DIALOG),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_QUEST),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_TRANSPORT),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_PLAYERDATA),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_RECIPE),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_NATURALSPAWN),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_LINKED),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_MARKETS),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_AUCTIONS),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_MAIL)
        ));
        CustomNpcs.debugData.end("Packets");
    }

}