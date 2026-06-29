package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiCustomChest;
import noppes.npcs.shared.common.PacketBasic;

public class PacketCustomChestName extends PacketBasic {

    protected static int channelId;
    private String name;

    public PacketCustomChestName() { }

    public PacketCustomChestName(String nameIn) { name = nameIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiCustomChest) {
            ((GuiCustomChest) screen).title = Component.translatable(name).getFormattedText();
        }
        CustomNpcs.debugData.end("Packets");
    }

}