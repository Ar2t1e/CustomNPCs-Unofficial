package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.items.ItemBoundary;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRegionRemove extends PacketServerBasic {

    protected static int channelId;
    private int regionID;

    public SPacketRegionRemove() { }

    public SPacketRegionRemove(int regionIDIn) { regionID = regionIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(regionID); }

    @Override
    public void decode(FriendlyByteBuf buf) { regionID = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (BorderController.getInstance().regions.containsKey(regionID)) {
            if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemBoundary) {
                NBTTagCompound compound = player.getHeldItemMainhand().getTagCompound();
                if (compound != null) { compound.removeTag("RegionID"); }
            }
            if (BorderController.getInstance().removeRegion(regionID)) {
                for (EntityPlayerMP p : CustomNpcs.Server.getPlayerList().getPlayers()) { BorderController.getInstance().sendTo(p); }
            }
        }
        else { BorderController.getInstance().sendTo(player); }
        CustomNpcs.debugData.end("Packets");
    }

}