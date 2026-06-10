package noppes.npcs.packets.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumRewardType;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.List;

public class SPacketQuestChooseReward extends PacketServerBasic {

    protected static int channelId;
    private final int id;

    public SPacketQuestChooseReward(int idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketQuestChooseReward msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

    public static SPacketQuestChooseReward decode(FriendlyByteBuf buf) { return new SPacketQuestChooseReward(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Quest quest = QuestController.instance.get(id);
        if (quest != null) {
            if (quest.rewardType == EnumRewardType.ONE_SELECT) {
                double baseChance = 1.0d;
                double luck = player.getAttributeValue(Attributes.LUCK);
                if (luck != 0.0d) {
                    if (luck < 0) {
                        luck *= -1;
                        baseChance -= luck * luck * -0.005555d + luck * 0.255555d; // 1lv = 25%$ 10lv = 200%
                    } else {
                        baseChance += luck * luck * -0.005555d + luck * 0.255555d; // 1lv = 25%$ 10lv = 200%
                    }
                }
                // Luck
                List<ItemStack> createRewardItems = new ArrayList<>();
                for (DropSet ds : quest.rewardItems.values()) {
                    ItemStack stack = ds.createMCLoot(baseChance);
                    if (!stack.isEmpty()) { createRewardItems.add(stack); }
                }
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeInt(id);
                buf.writeInt(createRewardItems.size());
                for (ItemStack stack : createRewardItems) { buf.writeItemStack(stack, false); }
                Packets.send(player, new PacketGuiOpen(EnumGuiType.QuestChooseReward, buf));
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}
