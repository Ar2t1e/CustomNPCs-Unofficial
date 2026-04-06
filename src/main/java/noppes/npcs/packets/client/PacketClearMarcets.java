package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketClearMarcets extends PacketBasic {

    protected static int channelId;

    public static void encode(PacketClearMarcets ignoredMsg, FriendlyByteBuf ignoredBuf) {}

    public static PacketClearMarcets decode(FriendlyByteBuf ignoredBuf) { return new PacketClearMarcets(); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().markets.clear();
        MarcetController.getInstance().deals.clear();
        CustomNpcs.debugData.end("Packets");
    }

}
