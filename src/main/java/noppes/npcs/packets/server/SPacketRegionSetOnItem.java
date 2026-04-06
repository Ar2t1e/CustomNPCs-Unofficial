package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.items.ItemBoundary;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRegionSetOnItem extends PacketServerBasic {

    protected static int channelId;
    private final int regionID;

    public SPacketRegionSetOnItem(int regionIDIn) { regionID = regionIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketRegionSetOnItem msg, FriendlyByteBuf buf) { buf.writeInt(msg.regionID); }

    public static SPacketRegionSetOnItem decode(FriendlyByteBuf buf) { return new SPacketRegionSetOnItem(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (BorderController.getInstance().regions.containsKey(regionID)) { BorderController.getInstance().sendTo(player); }
        else {
            if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof ItemBoundary) {
                CompoundTag compound = player.getMainHandItem().getOrCreateTag();
                compound.putInt("RegionID", regionID);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}