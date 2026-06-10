package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSyncUpdate extends PacketServerBasic {

    protected static int channelId;
    private final int type;
    private final CompoundTag data;

    public SPacketSyncUpdate(int typeIn, CompoundTag dataIn) {
        type = typeIn;
        data = dataIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketSyncUpdate msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.type);
        buf.writeNbt(msg.data);
    }

    public static SPacketSyncUpdate decode(FriendlyByteBuf buf) {
        return new SPacketSyncUpdate(buf.readInt(), buf.readNbt(new NbtAccounter(Long.MAX_VALUE)));
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        switch (type) {
            case 0: {
                CustomNpcs.TypeShowQuestCompass = data.getInt("value");
                Packets.sendAll(new PacketSyncUpdate(CustomNpcs.TypeShowQuestCompass, 16, new CompoundTag()));
                break;
            } // TypeShowQuestCompass
            case 5: {
                PlayerData.get(player).dialogData.load(data);
                break;
            } // player dialogs data
            case 6: {
                PlayerData.get(player).minimap.load(data);
                break;
            } // minimap
            case 10: {
                PlayerData.get(player).compass.load(data);
                break;
            } // player compass data
        }
        CustomNpcs.debugData.end("Packets");
    }

}
