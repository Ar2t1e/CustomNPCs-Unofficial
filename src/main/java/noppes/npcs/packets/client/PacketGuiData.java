package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiData extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketGuiData() { }

    public PacketGuiData(NBTTagCompound compound) { data = compound; }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        while (gui instanceof IGuiInterface && ((IGuiInterface) gui).hasSubGui()) { gui = ((IGuiInterface) gui).getSubGui(); }
        if (gui instanceof IGuiData) { ((IGuiData) gui).setGuiData(data); }
        CustomNpcs.debugData.end("Packets");
    }

}