package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankClearCeil extends PacketServerBasic {

    protected static int channelId;
    private int bankId;
    private int ceil;
    private int ceilPos;
    private int ceilsUpdate;

    public SPacketBankClearCeil() { }

    public SPacketBankClearCeil(int bankIdIn, int ceilIn, int ceilPosIn, int ceilsUpdateIn) {
        bankId = bankIdIn;
        ceil = ceilIn;
        ceilPos =ceilPosIn;
        ceilsUpdate = ceilsUpdateIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(bankId);
        buf.writeInt(ceil);
        buf.writeInt(ceilPos);
        buf.writeInt(ceilsUpdate);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        bankId = buf.readInt();
        ceil = buf.readInt();
        ceilPos = buf.readInt();
        ceilsUpdate = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.openContainer instanceof ContainerNPCBank) { ((ContainerNPCBank) player.openContainer).items.clear(); }
        CustomNpcs.debugData.end("Packets");
    }

}
