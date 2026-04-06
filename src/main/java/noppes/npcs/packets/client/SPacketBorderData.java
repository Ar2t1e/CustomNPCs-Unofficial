package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBorderData extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag data;

    public SPacketBorderData(CompoundTag dataIn) { data = dataIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketBorderData msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

    public static SPacketBorderData decode(FriendlyByteBuf buf) { return new SPacketBorderData(buf.readAnySizeNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        BorderController.getInstance().loadRegion(data);
        CustomNpcs.debugData.end("Packets");
    }

}