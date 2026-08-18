package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketDimensionRecreate extends PacketServerBasic {

    protected static int channelId;
    private int dimension;

    public SPacketDimensionRecreate() { }

    public SPacketDimensionRecreate(int dimensionIn) { dimension = dimensionIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(dimension); }

    @Override
    public void decode(FriendlyByteBuf buf) { dimension = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        DimensionController.getInstance().recreateDimension(player, dimension);
        SPacketDimensionsGet.sendDimensionIDs(player);
        CustomNpcs.debugData.end("Packets");
    }

}