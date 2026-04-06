package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankLock extends PacketServerBasic {

    protected static int channelId;
    private int bankId;

    public SPacketBankLock() { }

    public SPacketBankLock(int bankIdIn) { bankId = bankIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(bankId); }

    @Override
    public void decode(FriendlyByteBuf buf) { bankId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerNPCBank) {
            ((ContainerNPCBank) player.openContainer).items.setNewSize(0);
            ((ContainerNPCBank) player.openContainer).data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
