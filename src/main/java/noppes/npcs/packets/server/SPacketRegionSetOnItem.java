package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.items.ItemBoundary;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRegionSetOnItem extends PacketServerBasic {

    protected static int channelId;
    private int regionID;

    public SPacketRegionSetOnItem() { }

    public SPacketRegionSetOnItem(int regionIDIn) { regionID = regionIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_BUILDERS); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(regionID); }

    @Override
    public void decode(FriendlyByteBuf buf) { regionID = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (BorderController.getInstance().regions.containsKey(regionID)) { BorderController.getInstance().sendTo(player); }
        else {
            if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemBoundary) {
                NBTTagCompound compound = player.getHeldItemMainhand().getTagCompound();
                if (compound == null) { player.getHeldItemMainhand().setTagCompound(compound = new NBTTagCompound()); }
                compound.setInteger("RegionID", regionID);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}