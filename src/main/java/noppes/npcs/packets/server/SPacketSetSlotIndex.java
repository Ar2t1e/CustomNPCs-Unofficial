package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNpcAvailabilityItem;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSetSlotIndex extends PacketServerBasic {

    protected static int channelId;
    private final int slotID;

    public SPacketSetSlotIndex(int slotIDIn) { slotID = slotIDIn; }

    public boolean toolAllowed(ItemStack item){ return true; }

    public static void encode(SPacketSetSlotIndex msg, FriendlyByteBuf buf) { buf.writeInt(msg.slotID); }

    public static SPacketSetSlotIndex decode(FriendlyByteBuf buf) { return new SPacketSetSlotIndex(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerNpcAvailabilityItem container) {
            container.slot.setSlotIndex(slotID, true);
        }
        CustomNpcs.debugData.end("Packets");
    }
}