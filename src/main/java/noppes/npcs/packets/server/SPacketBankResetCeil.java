package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankResetCeil extends PacketServerBasic {

    protected static int channelId;
    private final int bankId;
    private final int ceilPos;
    private final int ceilsUpdate;

    public SPacketBankResetCeil(int bankIdIn, int ceilPosIn, int ceilsUpdateIn) {
        bankId = bankIdIn;
        ceilPos = ceilPosIn;
        ceilsUpdate = ceilsUpdateIn;
    }

    public static void encode(SPacketBankResetCeil msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bankId);
        buf.writeInt(msg.ceilPos);
        buf.writeInt(msg.ceilsUpdate);
    }

    public static SPacketBankResetCeil decode(FriendlyByteBuf buf) { return new SPacketBankResetCeil(buf.readInt(), buf.readInt(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerNPCBank cont && cont.data.bank.ceilSettings.containsKey(cont.ceil)) {
            cont.items.clearContent();
            cont.items.setNewSize(cont.data.bank.ceilSettings.get(cont.ceil).startCells);
            cont.data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
