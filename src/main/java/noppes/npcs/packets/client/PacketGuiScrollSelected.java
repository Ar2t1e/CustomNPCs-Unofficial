package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollSelected extends PacketBasic {

    protected static int channelId;
    private String selected;

    public PacketGuiScrollSelected() { }

    public PacketGuiScrollSelected(String selectedIn) { selected = selectedIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { selected = buf.readUtf(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(selected); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof IScrollData) { ((IScrollData) gui).setSelected(selected); }
        CustomNpcs.debugData.end("Packets");
    }

}
