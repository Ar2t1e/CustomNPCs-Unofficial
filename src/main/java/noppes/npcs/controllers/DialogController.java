package noppes.npcs.controllers;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.util.Util;
import noppes.npcs.util.NBTJsonUtil;

public class DialogController implements IDialogHandler {

	public static DialogController instance = new DialogController();
	public final TreeMap<Integer, DialogCategory> categories = new TreeMap<>();
	public final TreeMap<Integer, DialogCategory> categoriesSync = new TreeMap<>();
	public final TreeMap<Integer, Dialog> dialogs = new TreeMap<>();
	private int lastUsedCatID = 0;
	private int lastUsedDialogID = 0;

	private final DialogGuiSettings guiSettings = new DialogGuiSettings();

	public DialogController() { DialogController.instance = this; }

	@Override
	public IDialogCategory[] categories() {
		return categories.values().toArray(new IDialogCategory[0]);
	}

	public boolean containsCategoryName(DialogCategory category) {
		for (DialogCategory cat : categories.values()) {
			if (category.id != cat.id && cat.title.equalsIgnoreCase(category.title)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsDialogName(DialogCategory category, Dialog dialog) {
		for (Dialog dia : category.dialogs.values()) {
			if (dia.id != dialog.id && dia.title.equalsIgnoreCase(dialog.title)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Dialog get(int id) { return dialogs.get(id); }

	private File getDir() {
		return new File(CustomNpcs.getWorldSaveDirectory(), "dialogs");
	}

	public boolean hasDialog(int dialogId) {
		return dialogs.containsKey(dialogId);
	}

	public void load() {
		CustomNpcs.debugData.start(null);
		LogWriter.info("Loading Dialogs");
		loadCategories();
		try {
			File file = new File(CustomNpcs.getWorldSaveDirectory(), "dialog_gui_settings.dat");
			if (file.exists()) { guiSettings.load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); }
			else { saveSettings(); }
		} catch (Exception e) { LogWriter.except(e); }
		LogWriter.info("Done loading Dialogs");
		CustomNpcs.debugData.end(null);
	}

	private void loadCategories() {
		categories.clear();
		dialogs.clear();
		lastUsedCatID = 0;
		lastUsedDialogID = 0;
		try {
			File file = new File(CustomNpcs.getWorldSaveDirectory(), "dialog.dat");
			if (file.exists()) {
				loadCategoriesOld(file);
				file.delete();
				file = new File(CustomNpcs.getWorldSaveDirectory(), "dialog.dat_old");
				if (file.exists()) { file.delete(); }
				return;
			}
		} catch (Exception e) { LogWriter.except(e); }
		File dir = getDir();
		if (!dir.exists()) {
			dir.mkdir();
			loadDefaultDialogs();
		}
		else {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File dialogFile : files) {
					if (dialogFile.isDirectory()) {
						DialogCategory category = loadCategoryDir(dialogFile);
						for (Map.Entry<Integer, Dialog> entry : new ArrayList<>(category.dialogs.entrySet())) {
							Integer id = entry.getKey();
							if (id > lastUsedDialogID) { lastUsedDialogID = id; }
							Dialog dialog = entry.getValue();
							if (dialogs.containsKey(id)) {
								LogWriter.error("Duplicate dialog ID:" + dialog.id + " from category " + category.title);
								category.dialogs.remove(id);
							} else {
								dialogs.put(id, dialog);
							}
						}
						++lastUsedCatID;
						category.id = lastUsedCatID;
						categories.put(category.id, category);
					}
				}
			}
		}
	}

	private void loadCategoriesOld(File file) throws Exception {
		NBTTagCompound compound = CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()));
		NBTTagList list = compound.getTagList("Data", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
			DialogCategory category = new DialogCategory();
			category.load(list.getCompoundTagAt(i));
			saveCategory(category);
			Iterator<Map.Entry<Integer, Dialog>> ita = category.dialogs.entrySet().iterator();
			while (ita.hasNext()) {
				Map.Entry<Integer, Dialog> entry = ita.next();
				Dialog dialog = entry.getValue();
				dialog.id = entry.getKey();
				if (dialogs.containsKey(dialog.id)) { ita.remove(); }
				else { saveDialog(category, dialog); }
			}
		}
	}

	private DialogCategory loadCategoryDir(File dir) {
		DialogCategory category = new DialogCategory();
		category.title = dir.getName();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".json")) {
					try {
						Dialog dialog = new Dialog(category);
						dialog.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
						dialog.loadPartial(NBTJsonUtil.LoadFile(file));
						category.dialogs.put(dialog.id, dialog);
					} catch (Exception e) {
						LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
					}
				}
			}
		}
		return category;
	}

	private void loadDefaultDialogs() {
		DialogCategory cat = new DialogCategory();
		cat.id = lastUsedCatID++;
		cat.title = "Villager";

		Dialog dia1 = new Dialog(cat);
		dia1.id = lastUsedDialogID++;
		dia1.title = "Start";
		dia1.text = "dialog.base.1.text";

		Dialog dia2 = new Dialog(cat);
		dia2.id = lastUsedDialogID++;
		dia2.title = "Ask about village";
		dia2.text = Util.instance.deleteColor(Component.translatable("dialog.base.2.text").getString());

		Dialog dia3 = new Dialog(cat);
		dia3.id = lastUsedDialogID++;
		dia3.title = "Who are you";
		dia3.text = Util.instance.deleteColor(Component.translatable("dialog.base.3.text").getString());

		Dialog dia4 = new Dialog(cat);
		dia4.id = lastUsedDialogID++;
		dia4.title = "Quest description";
		dia4.text = Util.instance.deleteColor(Component.translatable("dialog.base.4.text").getString());

		Dialog dia5 = new Dialog(cat);
		dia5.id = lastUsedDialogID++;
		dia5.title = "Issue a quest";
		dia5.text = Util.instance.deleteColor(Component.translatable("dialog.base.5.text").getString());
		dia5.quest = 0;
		for (Quest quest : new ArrayList<>(QuestController.instance.quests.values())) {
			if (quest.title.equals("quest.base.0")) {
				dia5.quest = quest.id;
				break;
			}
		}

		cat.dialogs.put(dia1.id, dia1);
		cat.dialogs.put(dia2.id, dia2);
		cat.dialogs.put(dia3.id, dia3);
		cat.dialogs.put(dia4.id, dia4);
		cat.dialogs.put(dia5.id, dia5);

		DialogOption option = new DialogOption();
		option.title = "dialog.base.1.option.0";
		option.addDialog(dia2.id);
		option.optionType = OptionType.DIALOG_OPTION;

		DialogOption option1 = new DialogOption();
		option1.title = "dialog.base.1.option.1";
		option1.addDialog(dia3.id);
		option1.optionType = OptionType.DIALOG_OPTION;

		DialogOption option2 = new DialogOption();
		option2.title = "dialog.base.1.option.3";
		option2.addDialog(dia4.id);
		option2.optionType = OptionType.DIALOG_OPTION;

		DialogOption option3 = new DialogOption();
		option3.title = "dialog.base.1.option.2";
		option3.optionType = OptionType.QUIT_OPTION;

		DialogOption option4 = new DialogOption();
		option4.title = Util.instance.deleteColor(Component.translatable("dialog.base.4.option.0").getString());
		option4.addDialog(dia1.id);

		DialogOption option5 = new DialogOption();
		option5.title = Util.instance.deleteColor(Component.translatable("dialog.base.5.option.0").getString());
		option5.addDialog(dia5.id);

		dia1.options.put(0, option);
		dia1.options.put(1, option1);
		dia1.options.put(2, option2);
		dia1.options.put(3, option3);

		dia2.options.put(0, option2);
		dia2.options.put(1, option4);

		dia3.options.put(0, option2);
		dia3.options.put(1, option4);

		dia4.options.put(0, option5);
		dia4.options.put(1, option4);

		dia5.options.put(1, option3);

		saveCategory(cat);
		saveDialog(cat, dia1);
		saveDialog(cat, dia2);
		saveDialog(cat, dia3);
		saveDialog(cat, dia4);
		saveDialog(cat, dia5);
	}

	public void removeCategory(int category) {
		DialogCategory cat = categories.get(category);
		if (cat == null) { return; }
		File dir = new File(getDir(), cat.title);
		if (!Util.instance.removeFile(dir)) { LogWriter.error("Error delete " + dir + "; no access or file not uploaded!"); }
		for (int dia : cat.dialogs.keySet()) { dialogs.remove(dia); }
		categories.remove(category);
		Packets.sendAll(new PacketSyncRemove(category, 5));
	}

	public void removeDialog(Dialog dialog) {
		DialogCategory category = dialog.category;
		File file = new File(new File(getDir(), category.title), dialog.id + ".json");
		if (file.delete()) {
			category.dialogs.remove(dialog.id);
			dialogs.remove(dialog.id);
			Packets.sendAll(new PacketSyncRemove(dialog.id, 4));
		}
	}

	public void saveCategory(DialogCategory category) {
		CustomNpcs.debugData.start(null);
		category.title = NoppesStringUtils.cleanFileName(category.title);
		if (category.title.isEmpty()) {
			category.title = "default";
			while (containsCategoryName(category)) { category.title += "_"; }
		}
		if (categories.containsKey(category.id)) {
			DialogCategory currentCategory = categories.get(category.id);
			if (!currentCategory.title.equals(category.title)) {
				while (containsCategoryName(category)) { category.title += "_"; }
				File newDir = new File(getDir(), category.title);
				File oldDir = new File(getDir(), currentCategory.title);
				if (newDir.exists()) {
					CustomNpcs.debugData.end(null);
					if (oldDir.exists()) { Util.instance.removeFile(oldDir); }
					return;
				}
				else if (!oldDir.renameTo(newDir)) {
					CustomNpcs.debugData.end(null);
					return;
				}
			}
			category.dialogs.clear();
			category.dialogs.putAll(currentCategory.dialogs);
		}
		else {
			if (category.id < 0) {
				++lastUsedCatID;
				category.id = lastUsedCatID;
			}
			while (containsCategoryName(category)) { category.title += "_"; }
			File dir = new File(getDir(), category.title);
			if (!dir.exists()) { dir.mkdirs(); }
		}
		categories.put(category.id, category);
		for (Dialog dialog : dialogs.values()) {
			if (dialog.category.id == category.id) { dialog.category = category; }
		}
		Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, EnumSync.DialogCategoriesData, category.save(new NBTTagCompound()));
		CustomNpcs.debugData.end(null);
	}

	public void saveDialog(DialogCategory category, Dialog dialog) {
		if (category == null) { return; }
		CustomNpcs.debugData.start(null);
		while(containsDialogName(dialog.category, dialog)) { dialog.title = dialog.title + "_"; }
		if (dialog.id < 0) {
			++lastUsedDialogID;
			dialog.id = lastUsedDialogID;
		}
		dialogs.put(dialog.id, dialog);
		category.dialogs.put(dialog.id, dialog);

		File dir = new File(getDir(), category.title);
		if (!dir.exists()) { dir.mkdirs(); }
		File file = new File(dir, dialog.id + ".json_new");
		File file2 = new File(dir, dialog.id + ".json");
		try {
			NBTTagCompound compound = dialog.save(new NBTTagCompound());
			Util.instance.saveFile(file, compound);
			if (file2.exists() && file2.delete() && file.renameTo(file2)) {

			}
			Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, EnumSync.DialogData, compound, category.id);
		}
		catch (Exception e) { LogWriter.except(e);}
		CustomNpcs.debugData.end(null);
	}

	public void saveSettings() {
		CustomNpcs.debugData.start(null);
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, "dialog_gui_settings.dat_new");
			File file2 = new File(saveDir, "dialog_gui_settings.dat_old");
			File file3 = new File(saveDir, "dialog_gui_settings.dat");
			CompressedStreamTools.writeCompressed(guiSettings.save(), Files.newOutputStream(file.toPath()));
			if (file2.exists()) { file2.delete(); }
			file3.renameTo(file2);
			if (file3.exists()) { file3.delete(); }
			file.renameTo(file3);
			if (file.exists()) { file.delete(); }
		} catch (Exception e) { LogWriter.error(e); }
		Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, EnumSync.DialogGuiSettings, guiSettings.save());
		CustomNpcs.debugData.end(null);
	}

	public DialogGuiSettings getGuiSettings() { return guiSettings; }

}
