package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketQuestMinID extends PacketServerBasic {

    protected static int channelId;
    private final int questId;

    public SPacketQuestMinID(int questIdIn) { questId = questIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

    public static void encode(SPacketQuestMinID msg, FriendlyByteBuf buf) { buf.writeInt(msg.questId); }

    public static SPacketQuestMinID decode(FriendlyByteBuf buf) { return new SPacketQuestMinID(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids = new ArrayList<>(QuestController.instance.quests.keySet());
        Collections.sort(ids);
        int id = 1;
        for (int i : ids) {
            if (id == i && id != questId) {
                id++;
                continue;
            }
            break;
        }
        CompoundTag compound = new CompoundTag();
        compound.putInt("MinimumID", id);
        Packets.sendServer(new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}