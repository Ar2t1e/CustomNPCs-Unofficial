package noppes.npcs.packets.server;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
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
    private int id;

    public SPacketQuestChooseReward() { }

    public SPacketQuestChooseReward(int idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Quest quest = QuestController.instance.get(id);
        if (quest != null) {
            if (quest.rewardType == EnumRewardType.ONE_SELECT) {
                double baseChance = 1.0d;
                IAttributeInstance l = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK);
                if (l != null && l.getAttributeValue() != 0.0d) {
                    double luck = l.getAttributeValue();
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
