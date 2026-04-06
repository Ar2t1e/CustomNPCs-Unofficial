package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketMiniMapDataSet extends PacketServerBasic {

    private NBTTagCompound data;

    public SPacketMiniMapDataSet() { }

    public SPacketMiniMapDataSet(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) {
        data = buf.readNbt();
    }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        NoppesUtilServer.openContainerGui(player, gui, b -> b.writeBytes(buffer.nioBuffer()));
        CustomNpcs.debugData.end("Packets");
    }

}