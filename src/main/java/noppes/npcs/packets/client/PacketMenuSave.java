package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMenuSave extends PacketBasic {

    protected static int channelId;
    private final int npcId;
    private final EnumMenuType type;
    private CompoundTag data = new CompoundTag();

    public PacketMenuSave(int npcIdIn, EnumMenuType typeIn, CompoundTag dataIn) {
        npcId = npcIdIn;
        type = typeIn;
        data = dataIn;
    }

    public PacketMenuSave(EntityNPCInterface npc, EnumMenuType typeIn) {
        npcId = npc.getId();
        type = typeIn;
        switch (type) {
            case DISPLAY: npc.display.save(data); break;
            case STATS: npc.stats.save(data); break;
            case INVENTORY: npc.inventory.save(data); break;
            case AI: npc.ais.save(data); break;
            case ADVANCED: npc.advanced.save(data);  break;
            case MODEL: data = ((EntityCustomNpc) npc).modelData.save(); break;
            case TRANSFORM: npc.transform.saveOptions(data); break;
            case MOVING_PATH: data.put("MovingPathNew", NBTTags.nbtIntegerArraySet(npc.ais.getMovingPath())); break;
            case MARK: data = MarkData.get(npc).getNBT(); break;
        }
    }

    public static void encode(PacketMenuSave msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.npcId);
        buf.writeEnum(msg.type);
        buf.writeNbt(msg.data);
    }

    public static PacketMenuSave decode(FriendlyByteBuf buf) { return new PacketMenuSave(buf.readInt(), buf.readEnum(EnumMenuType.class), buf.readNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(npcId);
        if (entity instanceof EntityNPCInterface cnpc) {
            switch (type) {
                case DISPLAY: cnpc.display.load(data); break;
                case STATS: cnpc.stats.load(data); break;
                case INVENTORY: cnpc.inventory.load(data); break;
                case AI: {
                    cnpc.ais.load(data);
                    ClientEventHandler.movingPath.clear();
                    break;
                }
                case ADVANCED: cnpc.advanced.load(data); break;
                case MODEL: ((EntityCustomNpc) cnpc).modelData.load(data); break;
                case TRANSFORM: cnpc.transform.loadOptions(data); break;
                case MOVING_PATH: {
                    cnpc.ais.setMovingPath(NBTTags.getIntegerArraySet(data.getList("MovingPathNew", 10)));
                    ClientEventHandler.movingPath.clear();
                    break;
                }
                case MARK: MarkData.get(cnpc).setNBT(data); break;
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}