package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankClearCeil extends PacketServerBasic {

    protected static int channelId;
    private final int bankId;
    private final int ceil;
    private final int ceilPos;
    private final int ceilsUpdate;

    public SPacketBankClearCeil(int bankIdIn, int ceilIn, int ceilPosIn, int ceilsUpdateIn) {
        bankId = bankIdIn;
        ceil = ceilIn;
        ceilPos =ceilPosIn;
        ceilsUpdate = ceilsUpdateIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketBankClearCeil msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bankId);
        buf.writeInt(msg.ceil);
        buf.writeInt(msg.ceilPos);
        buf.writeInt(msg.ceilsUpdate);
    }

    public static SPacketBankClearCeil decode(FriendlyByteBuf buf) {
        return new SPacketBankClearCeil(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerNPCBank cont) { cont.items.clearContent(); }
        CustomNpcs.debugData.end("Packets");
    }

}
