package noppes.npcs.packets.server;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.play.server.SPacketSetSlot;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketNbtBookStackSave extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketNbtBookStackSave() { }

    public SPacketNbtBookStackSave(NBTTagCompound stackNBT) { data = stackNBT; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.TOOL_NBTBOOK; }

    @Override
    public void encode(FriendlyByteBuf buf) {buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data  = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack mcItem = new ItemStack(data);
        if (mcItem.isEmpty()) { player.sendMessage(Component.translatable("nbt.book.not.correct.nbt")); }
        else {
            player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, mcItem);
            player.inventoryContainer.detectAndSendChanges();
            player.connection.sendPacket(new SPacketSetSlot(-2, 0, player.inventory.getCurrentItem()));
            PlayerQuestData playerdata = PlayerData.get(player).questData;
            CustomNPCsScheduler.runTack(() -> {
                for (QuestData data : playerdata.activeQuests.values()) {
                    for (QuestObjective obj : data.quest.getObjectives(player)) {
                        if (obj.getEnumType() != EnumQuestTask.ITEM) { continue; }
                        playerdata.checkQuestCompletion(player, data);
                    }
                }
            });
        }
        CustomNpcs.debugData.end("Packets");
    }

}