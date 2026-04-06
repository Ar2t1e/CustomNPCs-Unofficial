package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.dimensions.CustomWorldInfo;
import noppes.npcs.dimensions.DimensionHandler;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;

public class SPacketDimensionsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.teleporter; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.TOOL_TELEPORTER; }

    @Override
    public void  encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        DimensionHandler dData = DimensionHandler.getInstance();
        for (int id : DimensionManager.getStaticDimensionIDs()) {
            WorldProvider provider = DimensionManager.createProviderFor(id);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("deleted", DimensionManager.getWorld(id) == null);
            nbt.setBoolean("loaded", !DimensionManager.isWorldQueuedToUnload(id));
            String name = provider.getDimensionType().getName();
            if (dData.getMCWorldInfo(id) instanceof CustomWorldInfo) { name = ((CustomWorldInfo) dData.getMCWorldInfo(id)).getWorldName(); }
            name += provider.getDimensionType().getSuffix();
            nbt.setString("name", name);
            nbt.setInteger("id", id);
            list.appendTag(nbt);
        }
        compound.setTag("Data", list);
        Packets.send(player, new PacketSync(9, compound, false));
        CustomNpcs.debugData.end("Packets");
    }

}
