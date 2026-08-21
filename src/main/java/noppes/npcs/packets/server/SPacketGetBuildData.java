package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
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
    private int id;
    private int type;

    public SPacketGetBuildData() { }

    public SPacketGetBuildData(int idIn, int typeIn) {
        id = idIn;
        type = typeIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(type);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        id = buf.readInt();
        type = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack stack = player.getHeldItemMainhand();
        BuilderData builder = null;
        if (stack.getItem() instanceof ISpecBuilder) { builder = ItemBuilder.getBuilder(stack, player); }
        else if (id >= 0) { builder = SyncController.dataBuilder.get(id); }
        if (builder != null) { Packets.send(player, new PacketSyncUpdate(id, 7, builder.getNbt())); }
        CustomNpcs.debugData.end("Packets");
    }

}