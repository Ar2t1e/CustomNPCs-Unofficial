package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcInitData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcInitData extends PacketServerBasic {

    protected static int channelId;
    private int npcId;

    public SPacketNpcInitData() { }

    public SPacketNpcInitData(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(npcId); }

    @Override
    public void decode(FriendlyByteBuf buf) { npcId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity e = player.world.getEntityByID(npcId);
        if (e instanceof EntityNPCInterface) {
            NBTTagCompound compound = new NBTTagCompound();
            e.writeToNBT(compound);
            Packets.sendServer(new PacketNpcInitData(npcId, compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
