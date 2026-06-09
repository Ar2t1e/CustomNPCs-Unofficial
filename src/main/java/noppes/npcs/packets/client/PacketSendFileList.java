package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.TempFile;

public class PacketSendFileList extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public PacketSendFileList() { }

    public PacketSendFileList(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        for (int i = 0; i < compound.getTagList("FileList", 10).tagCount(); i++) {
            NBTTagCompound tempFile = compound.getTagList("FileList", 10).getCompoundTagAt(i);
            String name = tempFile.getString("name");
            if (!ClientProxy.loadFiles.containsKey(name)) { ClientProxy.loadFiles.put(name, new TempFile()); }
            TempFile file = ClientProxy.loadFiles.get(name);
            file.setTitle(tempFile);
        }
        ClientTickHandler.loadFiles();
        CustomNpcs.debugData.end("Packets");
    }

}
