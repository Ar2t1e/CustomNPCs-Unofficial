package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRarityTitleSet extends PacketBasic {

    protected static int channelId;
    public int npcId;
    public NBTTagCompound compound;

    public PacketNpcRarityTitleSet() { }

    public PacketNpcRarityTitleSet(int npcIdIn, NBTTagCompound compoundIn) {
        npcId = npcIdIn;
        compound = compoundIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        npcId = buf.readInt();
        compound = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
        buf.writeNbt(compound);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}