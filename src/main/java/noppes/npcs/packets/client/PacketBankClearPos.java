package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBankClearPos extends PacketBasic {

    protected static int channelId;

    public static void encode(PacketBankClearPos ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static PacketBankClearPos decode(FriendlyByteBuf ignoredBuf) { return new PacketBankClearPos(); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        GuiNPCBankChest.startXMouse = 0;
        GuiNPCBankChest.startYMouse = 0;
        CustomNpcs.debugData.end("Packets");
    }

}