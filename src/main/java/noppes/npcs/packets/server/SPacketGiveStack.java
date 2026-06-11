package noppes.npcs.packets.server;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.Arrays;
import java.util.List;

public class SPacketGiveStack extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketGiveStack() { }

    public SPacketGiveStack(NBTTagCompound stackNBT) { data = stackNBT; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() {
        return Arrays.asList(CustomNpcsPermissions.TOOL_NBTBOOK, CustomNpcsPermissions.GLOBAL_FACTION);
    }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data  = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ItemStack mcItem = new ItemStack(data);
        if (!mcItem.isEmpty() && player.inventory.addItemStackToInventory(mcItem.copy())) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7f + 1.0f) * 2.0f);
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