package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMarcetsGet extends PacketServerBasic {

    protected static int channelId;
    private final int marcetId;

    public SPacketMarcetsGet(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MARKETS); }

    public static void encode(SPacketMarcetsGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetId); }

    public static SPacketMarcetsGet decode(FriendlyByteBuf buf) { return new SPacketMarcetsGet(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().sendTo(player, marcetId);
        CustomNpcs.debugData.end("Packets");
    }

}