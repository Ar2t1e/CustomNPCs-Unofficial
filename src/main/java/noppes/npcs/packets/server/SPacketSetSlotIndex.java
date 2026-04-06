package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNpcAvailabilityItem;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSetSlotIndex extends PacketServerBasic {

    protected static int channelId;
    private int slotID;

    public SPacketSetSlotIndex() { }

    public SPacketSetSlotIndex(int slotIDIn) { slotID = slotIDIn; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(slotID); }

    @Override
    public void decode(FriendlyByteBuf buf) { slotID = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerNpcAvailabilityItem) {
            ((ContainerNpcAvailabilityItem) player.openContainer).slot.setSlotIndex(slotID, true);
        }
        CustomNpcs.debugData.end("Packets");
    }
}