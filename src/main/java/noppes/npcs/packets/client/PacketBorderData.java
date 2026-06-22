package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBorderData extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketBorderData() { }

    public PacketBorderData(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        BorderController.getInstance().loadRegion(data);
        CustomNpcs.debugData.end("Packets");
    }

}