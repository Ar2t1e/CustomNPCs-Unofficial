package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankResetCeil extends PacketServerBasic {

    protected static int channelId;
    private int bankId;
    private int ceilPos;
    private int ceilsUpdate;

    public SPacketBankResetCeil() { }

    public SPacketBankResetCeil(int bankIdIn, int ceilPosIn, int ceilsUpdateIn) {
        bankId = bankIdIn;
        ceilPos = ceilPosIn;
        ceilsUpdate = ceilsUpdateIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(bankId);
        buf.writeInt(ceilPos);
        buf.writeInt(ceilsUpdate);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        bankId = buf.readInt();
        ceilPos = buf.readInt();
        ceilsUpdate = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerNPCBank &&
                ((ContainerNPCBank) player.openContainer).data.bank.ceilSettings.containsKey(((ContainerNPCBank) player.openContainer).ceil)) {
            ((ContainerNPCBank) player.openContainer).items.clear();
            ((ContainerNPCBank) player.openContainer).items.setNewSize(((ContainerNPCBank) player.openContainer).data.bank.ceilSettings
                    .get(((ContainerNPCBank) player.openContainer).ceil).startCells);
            ((ContainerNPCBank) player.openContainer).data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
