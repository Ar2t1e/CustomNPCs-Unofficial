package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeRemoveGroup extends PacketServerBasic {

    protected static int channelId;
    private final int size;
    private final String group;

    public SPacketRecipeRemoveGroup(int sizeIn, String groupIn) {
        size = sizeIn;
        group = groupIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

    public static void encode(SPacketRecipeRemoveGroup msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.size);
        buf.writeUtf(msg.group);
    }

    public static SPacketRecipeRemoveGroup decode(FriendlyByteBuf buf) { return new SPacketRecipeRemoveGroup(buf.readInt(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");

        CustomNpcs.debugData.end("Packets");
    }

}