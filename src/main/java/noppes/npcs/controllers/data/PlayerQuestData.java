package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.client.gui.util.quests.QuestInterface;

public class PlayerQuestData implements IPlayerData {

	public HashMap<Integer, QuestData> activeQuests = new HashMap<>(); // [qID, data]
	public HashMap<Integer, Long> finishedQuests = new HashMap<>(); // [qID, time]
	public long overworldTime = 0L;

	// New from Unofficial (BetaZavr)
	public boolean updateClient; // ServerTickHandler.cnpcPlayerTick() 114

	public PlayerQuestData() {
	}

	public boolean checkQuestCompletion(EntityPlayer player, QuestData data) {
		QuestInterface inter = data.quest.questInterface;
		if (inter.isCompleted(player)) {
			if (data.isCompleted && data.quest.completion == EnumQuestCompletion.Npc) { return false; }
			if (!data.quest.complete(player, data)) {
				Packets.send((EntityPlayerMP)player, new PacketAchievement(Component.translatable("quest.completed"), Component.translatable(data.quest.title), 2, new NBTTagCompound()));
				Packets.send((EntityPlayerMP)player, new PacketChat(Component.translatable("quest.completed").append(": ").append(Component.translatable(data.quest.title))));
			}
			data.isCompleted = true;
			this.updateClient = true;
			EventHooks.onQuestFinished(PlayerData.get(player).scriptData, data.quest);
			return true;
		}
		return false;
	}

	public QuestData getQuestCompletion(EntityPlayer player, EntityNPCInterface npc) {
		for (QuestData data : this.activeQuests.values()) {
			Quest quest = data.quest;
			if (quest != null && quest.completer != null && quest.completion == EnumQuestCompletion.Npc &&
					quest.completer.getName().equals(npc.getName()) && quest.questInterface.isCompleted(player)) {
				return data;
			}
		}
		return null;
	}

	@Override
	public void load(NBTTagCompound mainCompound) {
		if (mainCompound == null) {
			return;
		}
		NBTTagCompound compound = mainCompound.getCompoundTag("QuestData");

		HashMap<Integer, Long> finishedMap = new HashMap<>();
		if (compound.hasKey("CompletedQuests", 9) && compound.getTagList("CompletedQuests", 10).tagCount() > 0) {
			for (int i = 0; i < compound.getTagList("CompletedQuests", 10).tagCount(); ++i) {
				NBTTagCompound dataNBT = compound.getTagList("CompletedQuests", 10).getCompoundTagAt(i);
				finishedMap.put(dataNBT.getInteger("Quest"), dataNBT.getLong("Date"));
			}
		}
		this.finishedQuests = finishedMap;

		HashMap<Integer, QuestData> activeMap = new HashMap<>();
		if (compound.hasKey("ActiveQuests", 9) && compound.getTagList("ActiveQuests", 10).tagCount() > 0) {
			for (int i = 0; i < compound.getTagList("ActiveQuests", 10).tagCount(); ++i) {
				NBTTagCompound dataNBT = compound.getTagList("ActiveQuests", 10).getCompoundTagAt(i);
				int id = dataNBT.getInteger("Quest");
				Quest quest = QuestController.instance.quests.get(id);
				if (quest != null) {
					QuestData data = new QuestData(quest);
					data.readEntityFromNBT(dataNBT);
					activeMap.put(id, data);
				}
			}
		}
		this.activeQuests = activeMap;

	}

	@Override
	public NBTTagCompound save(NBTTagCompound mainCompound) {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList finishedList = new NBTTagList();
		for (int quest : this.finishedQuests.keySet()) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("Quest", quest);
			nbttagcompound.setLong("Date", this.finishedQuests.get(quest));
			finishedList.appendTag(nbttagcompound);
		}
		compound.setTag("CompletedQuests", finishedList);

		NBTTagList activeList = new NBTTagList();
		for (int id : this.activeQuests.keySet()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Quest", id);
			this.activeQuests.get(id).writeEntityToNBT(nbt);
			activeList.appendTag(nbt);
		}
		compound.setTag("ActiveQuests", activeList);

		mainCompound.setTag("QuestData", compound);
		return mainCompound;
	}

}
