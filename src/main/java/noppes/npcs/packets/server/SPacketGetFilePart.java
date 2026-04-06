package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSendFilePart;
import noppes.npcs.util.TempFile;

public class SPacketGetFilePart extends PacketServerBasic {

    protected static int channelId;
    private int partId;
    private String name;

    public SPacketGetFilePart() { }

    public SPacketGetFilePart(int partIdIn, String nameIn) {
        partId = partIdIn;
        name = nameIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(partId);
        buf.writeUtf(name);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        partId = buf.readInt();
        name = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!ScriptController.downloadableFiles.containsKey(name)) {
            Packets.send(player, new PacketSendFilePart(true, 0, name, ""));
        } else {
            TempFile file = ScriptController.downloadableFiles.get(name);
            Packets.send(player, new PacketSendFilePart(false, partId, name, String.valueOf(file.data.get(partId))));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
