package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketKeyActive extends PacketServerBasic {

    protected static int channelId;
    private final int id;

    public SPacketKeyActive(int idIn) { id = idIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketKeyActive msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

    public static SPacketKeyActive decode(FriendlyByteBuf buf) { return new SPacketKeyActive(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        EventHooks.onPlayerKeyActive(player, id);
        CustomNpcs.debugData.end("Packets");
    }

}
