package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankOpen;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBankReOpen extends PacketBasic {

    protected static int channelId;

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getMinecraft().currentScreen instanceof GuiNPCBankChest) {
            GuiNPCBankChest gui = (GuiNPCBankChest) Minecraft.getMinecraft().currentScreen;
            gui.isWait = true;
            Packets.sendServer(new SPacketBankOpen(gui.menu.data.bank.id,
                    gui.menu.ceil, gui.ceilPos, gui.scrollY, gui.ceilsUpdate));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
