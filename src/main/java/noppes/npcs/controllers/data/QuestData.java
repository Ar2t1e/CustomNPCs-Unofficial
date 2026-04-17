package noppes.npcs.controllers.data;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.client.gui.util.quests.QuestObjective;

public class QuestData {

	public Quest quest;
	public long startIn = System.currentTimeMillis();
	public boolean isCompleted = false;
	public final NBTTagCompound extraData = new NBTTagCompound();

	public QuestData(Quest questIn) {
		quest = questIn;
		int pos = 0;
		for (QuestObjective task : questIn.questInterface.tasks) {
			if (task.getEnumType() == EnumQuestTask.KILL || task.getEnumType() == EnumQuestTask.AREAKILL
					|| task.getEnumType() == EnumQuestTask.MANUAL) {
				if (!extraData.hasKey("Targets", 9)) { extraData.setTag("Targets", new NBTTagList()); }
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("Slot", task.getTargetName());
				nbt.setInteger("Value", 0);
				nbt.setInteger("ObjectPos", pos);
				extraData.getTagList("Targets", 10).appendTag(nbt);
			}
			else if (task.getEnumType() == EnumQuestTask.CRAFT) {
				if (!task.getItem().isEmpty()) {
					if (extraData.hasKey("Crafts", 9)) { extraData.setTag("Crafts", new NBTTagList()); }
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setTag("Item", task.getItemStack().writeToNBT(new NBTTagCompound()));
					nbt.setInteger("Value", 0);
					nbt.setInteger("ObjectPos", pos);
					extraData.getTagList("Crafts", 10).appendTag(nbt);
				}
			}
			else if (task.getEnumType() == EnumQuestTask.LOCATION) {
				if (extraData.hasKey("Locations", 9)) { extraData.setTag("Locations", new NBTTagList()); }
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("Location", task.getTargetName());
				nbt.setBoolean("Found", false);
				nbt.setInteger("ObjectPos", pos);
				extraData.getTagList("Locations", 10).appendTag(nbt);
			}
			pos++;
		}
	}

	public QuestData load(NBTTagCompound compound) {
		isCompleted = compound.getBoolean("QuestCompleted");
		startIn = compound.getLong("StartIn");
		for (String key : new ArrayList<>(extraData.getKeySet())) { extraData.removeTag(key); }
		for (String key : new ArrayList<>(compound.getCompoundTag("ExtraData").getKeySet())) {
			extraData.setTag(key, compound.getCompoundTag("ExtraData").getTag(key));
		}
		return this;
	}

	public void save(NBTTagCompound compound) {
		compound.setBoolean("QuestCompleted", isCompleted);
		compound.setLong("StartIn", startIn);
		compound.setTag("ExtraData", extraData);
	}

}
