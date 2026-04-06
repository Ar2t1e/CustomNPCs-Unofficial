package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;

public class SPacketPermissionsDel extends PacketServerBasic {

    protected static int channelId;
    private String name;
    private String node;

    public SPacketPermissionsDel() { }

    public SPacketPermissionsDel(String nameIn, String nodeIn) {
        name = nameIn;
        node = nodeIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.EDIT_PERMISSION; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(node);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        name = buf.readUtf();
        node = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_PERMISSION)) {
            permission(CustomNpcsPermissions.EDIT_PERMISSION, "SPacketPermissionsDel");
            Packets.send(player, new PacketGuiClose());
        }
        else { CustomNpcsPermissions.remove(node, name, player); }
        CustomNpcs.debugData.end("Packets");
    }

}