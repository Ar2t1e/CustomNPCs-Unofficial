package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiNPCTrader;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarcetClose extends PacketBasic {

    protected static int channelId;
    private final int marcetID;

    public PacketMarcetClose(int marcetIDIn) { marcetID = marcetIDIn; }

    public static void encode(PacketMarcetClose msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetID); }

    public static PacketMarcetClose decode(FriendlyByteBuf buf) { return new PacketMarcetClose(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Marcet m = MarcetController.getInstance().getMarcet(marcetID);
        if (m != null) {
            m.removeListener(player, false);
            if (Minecraft.getInstance().screen instanceof GuiNPCTrader gui &&
                    GuiNPCTrader.marcet != null &&
                    GuiNPCTrader.marcet.getId() == marcetID) { gui.onClose(); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}