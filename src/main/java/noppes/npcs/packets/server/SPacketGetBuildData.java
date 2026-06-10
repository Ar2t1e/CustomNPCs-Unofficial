package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.items.ItemBuilder;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.BuilderData;

import java.util.List;

public class SPacketGetBuildData extends PacketServerBasic {

    protected static int channelId;
    private final int id;
    private final int type;

    public SPacketGetBuildData(int idIn, int typeIn) {
        id = idIn;
        type = typeIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketGetBuildData msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.type);
    }

    public static SPacketGetBuildData decode(FriendlyByteBuf buf) { return new SPacketGetBuildData(buf.readInt(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack stack = player.getMainHandItem();
        BuilderData builder = null;
        if (stack.getItem() instanceof ISpecBuilder) { builder = ItemBuilder.getBuilder(stack, player); }
        else if (id >= 0) { builder = SyncController.dataBuilder.get(id); }
        if (builder != null) { Packets.send(player, new PacketSyncUpdate(id, 7, builder.getNbt())); }
        CustomNpcs.debugData.end("Packets");
    }

}