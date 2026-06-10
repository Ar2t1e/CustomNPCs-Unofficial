package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketGetFirstID extends PacketServerBasic {

    protected static int channelId;
    private final int type;
    private final int excludedId;

    public SPacketGetFirstID(int typeIn, int excludedIdIn) {
        type = typeIn;
        excludedId = excludedIdIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_FACTION); }

    public static void encode(SPacketGetFirstID msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.type);
        buf.writeInt(msg.excludedId);
    }

    public static SPacketGetFirstID decode(FriendlyByteBuf buf) { return new SPacketGetFirstID(buf.readInt(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids = switch (type) {
            case 0 -> new ArrayList<>(FactionController.instance.factions.keySet());
            case 1 -> new ArrayList<>(DialogController.instance.dialogs.keySet());
            default -> new ArrayList<>(QuestController.instance.quests.keySet());
        };
        Collections.sort(ids);
        int id = 1;
        for (int i : ids) {
            if (id == i && id != excludedId) {
                id++;
                continue;
            }
            break;
        }
        CompoundTag compound = new CompoundTag();
        compound.putInt("MinimumID", id);
        Packets.send(player, new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}
