package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.NBTTags;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMenuSave extends PacketBasic {

    protected static int channelId;
    public int npcId;
    public EnumMenuType type;
    public NBTTagCompound data = new NBTTagCompound();

    public PacketMenuSave() { }

    public PacketMenuSave(EntityNPCInterface npc, EnumMenuType typeIn) {
        npcId = npc.getEntityId();
        type = typeIn;
        switch (type) {
            case DISPLAY: npc.display.save(data); break;
            case STATS: npc.stats.save(data); break;
            case INVENTORY: npc.inventory.save(data); break;
            case AI: npc.ais.save(data); break;
            case ADVANCED: npc.advanced.save(data);  break;
            case MODEL: data = ((EntityCustomNpc) npc).modelData.save(); break;
            case TRANSFORM: npc.transform.saveOptions(data); break;
            case MOVING_PATH: data.setTag("MovingPathNew", NBTTags.nbtIntegerArraySet(npc.ais.getMovingPath())); break;
            case MARK: data = MarkData.get(npc).getNBT(); break;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
        buf.writeEnum(type);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        npcId = buf.readInt();
        type = buf.readEnum(EnumMenuType.class);
        data = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
