package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankOpen;
import noppes.npcs.shared.common.PacketBasic;

public class PacketBankReOpen extends PacketBasic {

    protected static int channelId;

    public static void encode(PacketBankReOpen ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static PacketBankReOpen decode(FriendlyByteBuf ignoredBuf) { return new PacketBankReOpen(); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getInstance().screen instanceof GuiNPCBankChest gui) {
            gui.isWait = true;
            Packets.sendServer(new SPacketBankOpen(gui.getMenu().data.bank.id,
                    gui.getMenu().ceil, gui.ceilPos, gui.scrollY, gui.ceilsUpdate));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
