package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankLock extends PacketServerBasic {

    protected static int channelId;
    private final int bankId;

    public SPacketBankLock(int bankIdIn) { bankId = bankIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketBankLock msg, FriendlyByteBuf buf) { buf.writeInt(msg.bankId); }

    public static SPacketBankLock decode(FriendlyByteBuf buf) { return new SPacketBankLock(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.containerMenu instanceof ContainerNPCBank cont) {
            cont.items.setNewSize(0);
            cont.data.setChanged();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
