package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPermissionMenu;

import java.util.List;

public class SPacketPermissionMenuGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketPermissionMenuGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketPermissionMenuGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketPermissionMenuGet(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Packets.send(player, new PacketPermissionMenu(
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_DISPLAY),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_STATS),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_AI),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_INVENTORY),
                CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_ADVANCED)
        ));
        CustomNpcs.debugData.end("Packets");
    }

}
