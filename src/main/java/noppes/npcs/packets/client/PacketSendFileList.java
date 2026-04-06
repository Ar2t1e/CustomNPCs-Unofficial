package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.TempFile;

public class PacketSendFileList extends PacketBasic {

    protected static int channelId;
    private final CompoundTag compound;

    public PacketSendFileList(CompoundTag compoundIn) { compound = compoundIn; }

    public static void encode(PacketSendFileList msg, FriendlyByteBuf buf) { buf.writeNbt(msg.compound); }

    public static PacketSendFileList decode(FriendlyByteBuf buf) { return new PacketSendFileList(buf.readNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        for (int i = 0; i < compound.getList("FileList", 10).size(); i++) {
            CompoundTag tempFile = compound.getList("FileList", 10).getCompound(i);
            String name = tempFile.getString("name");
            if (!ClientProxy.loadFiles.containsKey(name)) { ClientProxy.loadFiles.put(name, new TempFile()); }
            TempFile file = ClientProxy.loadFiles.get(name);
            file.setTitle(tempFile);
        }
        ClientTickHandler.loadFiles();
        CustomNpcs.debugData.end("Packets");
    }

}
