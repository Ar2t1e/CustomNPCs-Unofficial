package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.client.TextBlockClient;

public class PlayerDialogData implements IPlayerData {

	protected static final String dataName = "DialogData";

	public final Map<Integer, Set<Integer>> dialogsRead = new TreeMap<>();

	@Override
	public void load(NBTTagCompound compound) {
		dialogsRead.clear();
		if (compound != null && compound.hasKey(dataName, 9)) {
			NBTTagList dialogs = compound.getTagList(dataName, 10);
			for (int i = 0; i < dialogs.tagCount(); ++i) {
				NBTTagCompound nbtDialog = dialogs.getCompoundTagAt(i);
				Set<Integer> set = new TreeSet<>();
				for (int id : nbtDialog.getIntArray("OptionRead")) { set.add(id); }
				dialogsRead.put(nbtDialog.getInteger("Dialog"), set);
			}
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		NBTTagList dialogs = new NBTTagList();
		for (int dialogId : dialogsRead.keySet()) {
			NBTTagCompound nbtDialog = new NBTTagCompound();
			nbtDialog.setInteger("Dialog", dialogId);
			int[] set = new int[dialogsRead.get(dialogId).size()];
			int i = 0;
			for (int id :dialogsRead.get(dialogId)) { set[i++] = id; }
			nbtDialog.setIntArray("OptionRead", set);
			dialogs.appendTag(nbtDialog);
		}
		compound.setTag(dataName, dialogs);
		return compound;
	}

	// New from Unofficial (BetaZavr)
	public void clear() { dialogsRead.clear(); }

	public boolean has(int dialogId) { return dialogsRead.containsKey(dialogId); }

	public void read(int dialogId) {
		if (has(dialogId)) { return; }
		dialogsRead.put(dialogId, new TreeSet<>());
	}

	public void option(int dialogId, int optionId) {
		if (!dialogsRead.containsKey(dialogId)) { dialogsRead.put(dialogId, new TreeSet<>()); }
		dialogsRead.get(dialogId).add(optionId);
	}

	@SuppressWarnings("unused")
    public void addLogs(List<TextBlockClient> lines, String texture) {

    }

}
