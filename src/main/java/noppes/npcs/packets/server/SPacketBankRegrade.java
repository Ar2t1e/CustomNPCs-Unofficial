package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankRegrade extends PacketServerBasic {

    protected static int channelId;
    private int bankId;
    private int scrollY;
    private int ceilPos;
    private int size;

    public SPacketBankRegrade() { }

    public SPacketBankRegrade(int bankIdIn, int scrollYIn, int ceilPosIn, int sizeIn) {
        bankId = bankIdIn;
        scrollY = scrollYIn;
        ceilPos = ceilPosIn;
        size = sizeIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(bankId);
        buf.writeInt(scrollY);
        buf.writeInt(ceilPos);
        buf.writeInt(size);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        bankId = buf.readInt();
        scrollY = buf.readInt();
        ceilPos = buf.readInt();
        size = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerNPCBank && ((ContainerNPCBank) player.openContainer).items.getSizeInventory() > 0) {
            ContainerNPCBank cont = (ContainerNPCBank) player.openContainer;
            cont.items.setNewSize(Math.max(cont.data.bank.ceilSettings.get(cont.ceil).startCells, cont.items.getSizeInventory() - size));
            cont.data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
