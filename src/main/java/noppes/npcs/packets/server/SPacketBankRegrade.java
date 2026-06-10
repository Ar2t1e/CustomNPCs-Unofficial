package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankRegrade extends PacketServerBasic {

    protected static int channelId;
    private final int bankId;
    private final int scrollY;
    private final int ceilPos;
    private final int size;

    public SPacketBankRegrade(int bankIdIn, int scrollYIn, int ceilPosIn, int sizeIn) {
        bankId = bankIdIn;
        scrollY = scrollYIn;
        ceilPos = ceilPosIn;
        size = sizeIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketBankRegrade msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bankId);
        buf.writeInt(msg.scrollY);
        buf.writeInt(msg.ceilPos);
        buf.writeInt(msg.size);
    }

    public static SPacketBankRegrade decode(FriendlyByteBuf buf) {
        return new SPacketBankRegrade(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerNPCBank cont && cont.items.getContainerSize() > 0) {
            cont.items.setNewSize(Math.max(cont.data.bank.ceilSettings.get(cont.ceil).startCells, cont.items.getContainerSize() - size));
            cont.data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
