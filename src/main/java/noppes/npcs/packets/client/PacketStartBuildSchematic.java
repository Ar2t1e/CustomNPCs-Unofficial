package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsTileBuild;
import noppes.npcs.shared.common.PacketBasic;

public class PacketStartBuildSchematic extends PacketBasic {

    protected static int channelId;

    @Override
    public void encode(FriendlyByteBuf ignoredBuf) {  }

    @Override
    public void decode(FriendlyByteBuf buf) {  }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (ClientEventHandler.schemaPos != null && ClientEventHandler.schema != null) {
            Packets.sendServer(new SPacketSchematicsTileBuild(ClientEventHandler.schemaPos, ClientEventHandler.rotation, ClientEventHandler.schema.getNBT()));
        }
        CustomNpcs.debugData.end("Packets");
    }

}