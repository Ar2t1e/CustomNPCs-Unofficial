package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNpcAvailabilityItem;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSetSlotIndex extends PacketServerBasic {

    protected static int channelId;
    private final int slotID;

    public SPacketSetSlotIndex(int slotIDIn) { slotID = slotIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
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