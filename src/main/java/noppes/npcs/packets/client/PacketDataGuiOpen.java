package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDataGuiOpen extends PacketBasic {

    protected static int channelId;
    public EnumGuiType gui;
    public NBTTagCompound data;

    public PacketDataGuiOpen() { }

    @SuppressWarnings("unused")
    public PacketDataGuiOpen(EnumGuiType guiIn, NBTTagCompound dataIn) {
        gui = guiIn;
        data = dataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        gui = buf.readEnum(EnumGuiType.class);
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(gui);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
