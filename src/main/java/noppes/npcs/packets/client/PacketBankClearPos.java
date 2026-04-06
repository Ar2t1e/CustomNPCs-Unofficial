package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBankClearPos extends PacketBasic {

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
        GuiNPCBankChest.startXMouse = 0;
        GuiNPCBankChest.startYMouse = 0;
        CustomNpcs.debugData.end("Packets");
    }

}