package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.handler.data.IWorldInfo;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;

import java.util.Collections;
import java.util.List;

public class SPacketDimensionsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.teleporter; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_TELEPORTER); }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        sendDimensionIDs(player);
        CustomNpcs.debugData.end("Packets");
    }

    public static void sendDimensionIDs(EntityPlayerMP player) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        DimensionController dData = DimensionController.getInstance();
        DimensionController.clearDimensionsData();
        DimensionType dimensionType;
        for (int id : dData.getAllIDs()) {
            DimensionData oldDD = DimensionController.getDimensionData(id);
            DimensionData dd = new DimensionData();
            dd.dimensionId = id;
            dd.isRemoved = DimensionManager.isWorldQueuedToUnload(id) || dData.isDelete(id);
            IWorldInfo worldInfo = dData.getMCWorldInfo(id);
            if (worldInfo != null) { dd.worldName = worldInfo.getDisplayName(); }
            WorldServer world = DimensionManager.getWorld(id);
            boolean unload = world == null;
            dd.isLoad = !unload && !dd.isRemoved;
            if (unload) {
                DimensionManager.initDimension(id);
                world = DimensionManager.getWorld(id);
            }
            if (world != null) {
                if (dd.worldName.isEmpty()) { dd.worldName = world.getWorldInfo().getWorldName(); }
                dd.spawnPos = world.getSpawnPoint();
                dimensionType = world.provider.getDimensionType();
                dd.name = dimensionType.getName();
                dd.suffix = dimensionType.getSuffix();
                if (unload) { DimensionManager.unloadWorld(id); }
            }
            if (dd.name.isEmpty()) {
                try {
                    dimensionType = DimensionManager.getProviderType(id);
                    if (dimensionType != null) {
                        dd.name = dimensionType.getName();
                        dd.suffix = dimensionType.getSuffix();
                    }
                }
                catch (Exception ignored) { }
            }
            if (dd.name.isEmpty() && oldDD != null) {
                dd.name = oldDD.name;
                dd.suffix = oldDD.suffix;
            }
            list.appendTag(dd.save());
            DimensionController.addDimensionData(dd);
        }
        compound.setTag("Data", list);
        Packets.send(player, new PacketSync(9, compound, false));
    }

}
