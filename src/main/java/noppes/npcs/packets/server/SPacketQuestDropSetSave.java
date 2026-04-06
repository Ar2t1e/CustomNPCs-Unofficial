package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketQuestDropSetSave extends PacketServerBasic {

    protected static int channelId;
    private final int questId;
    private final CompoundTag data;

    public SPacketQuestDropSetSave(int questIdIn, CompoundTag dataIn) {
        questId = questIdIn;
        data = dataIn;
    }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_MARKETS; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketQuestDropSetSave msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.questId);
        buf.writeNbt(msg.data);
    }

    public static SPacketQuestDropSetSave decode(FriendlyByteBuf buf) {
        return new SPacketQuestDropSetSave(buf.readInt(), buf.readAnySizeNbt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Quest quest = QuestController.instance.get(questId);
        if (quest != null) {
            int pos = data.getInt("Slot");
            if (pos < 0 || !quest.rewardItems.containsKey(pos)) {
                pos = quest.rewardItems.size();
                DropSet ds = new DropSet(quest);
                ds.pos = pos;
                quest.rewardItems.put(ds.pos, ds);
            }
            quest.rewardItems.get(pos).load(data);
            Packets.send(player, new PacketGuiUpdate());
        }
        CustomNpcs.debugData.end("Packets");
    }

}