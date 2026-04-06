package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.DropController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDropTemplateClear extends PacketBasic {

    protected static int channelId;
    public static void encode(PacketDropTemplateClear ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static PacketDropTemplateClear decode(FriendlyByteBuf ignoredBuf) { return new PacketDropTemplateClear(); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        DropController.getInstance().templates.clear();
        CustomNpcs.debugData.end("Packets");
    }

}
