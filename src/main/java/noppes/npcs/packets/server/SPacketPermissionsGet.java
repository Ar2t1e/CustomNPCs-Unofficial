package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;

public class SPacketPermissionsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.EDIT_PERMISSION; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_PERMISSION)) {
            permission(CustomNpcsPermissions.EDIT_PERMISSION, "SPacketPermissionsGet");
            Packets.send(player, new PacketGuiClose());
        }
        else { CustomNpcsPermissions.sendTo(player); }
        CustomNpcs.debugData.end("Packets");
    }

}