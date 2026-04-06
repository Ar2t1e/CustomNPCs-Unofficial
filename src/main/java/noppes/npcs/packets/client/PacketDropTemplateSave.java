package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.DropController;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDropTemplateSave extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketDropTemplateSave() { }

    public PacketDropTemplateSave(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (data.hasKey("Name", 8)) {
            DropsTemplate template = new DropsTemplate(data.getCompoundTag("Groups"));
            DropController.getInstance().templates.put(data.getString("Name"), template);
        }
        CustomNpcs.debugData.end("Packets");
    }

}