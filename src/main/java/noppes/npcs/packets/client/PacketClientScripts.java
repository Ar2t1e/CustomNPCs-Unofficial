package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketClientScripts extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public PacketClientScripts() { }

    public PacketClientScripts(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ScriptController.HasStart = true;
        ScriptController.Instance.setClientScripts(compound);
        CustomNpcs.debugData.end("Packets");
    }

}
