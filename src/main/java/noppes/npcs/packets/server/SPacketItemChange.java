package noppes.npcs.packets.server;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketItemChange extends PacketServerBasic {

    protected static int channelId;
    private String container;
    private int slot;
    private ItemStack stack;

    public SPacketItemChange() { }

    public SPacketItemChange(String containerIn, int slotId, ItemStack stackIn) {
        container = containerIn;
        slot = slotId;
        stack = stackIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(container);
        buf.writeInt(slot);
        buf.writeItemStack(stack, false);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        container = buf.readUtf();
        slot = buf.readInt();
        stack  = buf.readItem();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer.getClass().getSimpleName().equals(container)) {
            Slot slotIn = player.openContainer.getSlot(slot);
            if (slotIn != null) {
                slotIn.putStack(stack);
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}