package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketUpdateMarcetGui extends PacketBasic {

    protected static int channelId;

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getMinecraft().currentScreen instanceof IGuiData) { ((IGuiData) Minecraft.getMinecraft().currentScreen).setGuiData(new NBTTagCompound()); }
        CustomNpcs.debugData.end("Packets");
    }

}