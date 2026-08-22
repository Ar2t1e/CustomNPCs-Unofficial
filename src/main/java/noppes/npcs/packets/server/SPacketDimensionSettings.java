package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.dimensions.CustomWorldInfo;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.dimensions.WorldCustom;
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
            if (dimension == 0) { DimensionController.getInstance().createNewDimension(player, wi, true); } // new
            else {
                CustomWorldInfo cwi = (CustomWorldInfo) DimensionController.getInstance().getMCWorldInfo(dimension);
                if (cwi != null) {
                    // Update stored world info
                    cwi.load(wi.cloneNBTCompound(wi.getPlayerNBTTagCompound()));

                    // If the world is currently loaded, update its live WorldInfo
                    // so new chunks use the updated generator settings
                    WorldServer world = DimensionManager.getWorld(dimension);
                    if (world instanceof WorldCustom) {
                        ((WorldCustom) world).updateWorldInfo(cwi);
                    }

                    // Mark handler dirty so changes are saved to world NBT
                    DimensionController.getInstance().markDirty();

                    player.sendMessage(Component.translatable("message.dimensions.updated", "" + dimension).getParent());
                }
            } // changed
        }
        CustomNpcs.debugData.end("Packets");
    }

}
