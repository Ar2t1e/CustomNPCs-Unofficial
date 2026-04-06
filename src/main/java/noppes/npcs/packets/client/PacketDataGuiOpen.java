package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDataGuiOpen extends PacketBasic {

    protected static int channelId;
    private EnumGuiType gui;
    private NBTTagCompound data;

    public PacketDataGuiOpen() { }

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
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        try {
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.displayGuiScreen(ClientProxy.getGui(NoppesUtilServer.getEditingNpc(player), gui, null, data));
        }
        catch (Exception e) { LogWriter.error("Error in gui: " + gui, e); }
        CustomNpcs.debugData.end("Packets");
    }

}
