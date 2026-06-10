package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;

import java.util.Collections;
import java.util.List;

public class SPacketOpenEditClientScript extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.EDIT_CLIENT_SCRIPT); }

    public static void encode(SPacketOpenEditClientScript ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketOpenEditClientScript decode(FriendlyByteBuf ignoredBuf) { return new SPacketOpenEditClientScript(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Packets.send(player, new PacketGuiOpen(EnumGuiType.EditClientScript, BlockPos.ZERO));
        CustomNpcs.debugData.end("Packets");
    }

}