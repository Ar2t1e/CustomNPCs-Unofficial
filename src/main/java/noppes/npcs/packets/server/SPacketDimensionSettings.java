package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.storage.WorldInfo;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.dimensions.CustomWorldInfo;
import noppes.npcs.dimensions.DimensionHandler;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDimensionSettings extends PacketServerBasic {

    protected static int channelId;
    private int dimension;
    private WorldInfo worldInfo;

    public SPacketDimensionSettings() { }

    public SPacketDimensionSettings(int dimensionIn, WorldInfo worldInfoIn) {
        dimension = dimensionIn;
        worldInfo = worldInfoIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dimension);
        buf.writeWorldInfo(worldInfo);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dimension = buf.readInt();
        worldInfo = buf.readWorldInfo();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        if (worldInfo instanceof CustomWorldInfo) {
            CustomWorldInfo wi = (CustomWorldInfo) worldInfo;
            if (dimension == 0) { DimensionHandler.getInstance().createDimension(player, wi); }
            else {
                CustomWorldInfo cwi = (CustomWorldInfo) DimensionHandler.getInstance().getMCWorldInfo(dimension);
                if (cwi != null) { cwi.load(wi.read()); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}
