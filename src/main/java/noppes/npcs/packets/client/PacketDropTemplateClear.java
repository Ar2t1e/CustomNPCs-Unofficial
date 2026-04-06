package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.DropController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDropTemplateClear extends PacketBasic {

    protected static int channelId;

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        DropController.getInstance().templates.clear();
        CustomNpcs.debugData.end("Packets");
    }

}
