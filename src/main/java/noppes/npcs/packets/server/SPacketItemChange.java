package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketItemChange extends PacketServerBasic {

    protected static int channelId;
    private final String container;
    private final int slot;
    private final ItemStack stack;

    public SPacketItemChange(String containerIn, int slotId, ItemStack stackIn) {
        container = containerIn;
        slot = slotId;
        stack = stackIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketItemChange msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.container);
        buf.writeInt(msg.slot);
        buf.writeItemStack(msg.stack, false);
    }

    public static SPacketItemChange decode(FriendlyByteBuf buf) { return new SPacketItemChange(buf.readUtf(), buf.readInt(), buf.readItem()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    @SuppressWarnings("all")
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu.getClass().getSimpleName().equals(container)) {
            Slot slotIn = player.containerMenu.getSlot(slot);
            if (slotIn != null) { slotIn.set(stack); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}