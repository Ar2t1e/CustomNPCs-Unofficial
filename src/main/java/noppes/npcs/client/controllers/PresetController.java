package noppes.npcs.client.controllers;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.shared.common.util.LogWriter;

public class PresetController {

	public static PresetController instance;
	private final File dir;
	public HashMap<String, Preset> presets = new HashMap<>();

	public PresetController(File dirIn) {
		PresetController.instance = this;
		dir = dirIn;
		load();
	}

	public void addPreset(Preset preset) {
		StringBuilder name = new StringBuilder(preset.name);
		while (presets.containsKey(name.toString().toLowerCase())) { name.append("_"); }
		preset.name = name.toString();
		presets.put(preset.name.toLowerCase(), preset);
		save();
	}

	public Preset getPreset(String username) {
		if (presets.isEmpty()) { load(); }
		return presets.get(username.toLowerCase());
	}

	public void load() {
		NBTTagCompound compound = loadPreset();
		HashMap<String, Preset> presetsIn = new HashMap<>();
		if (compound != null) {
			NBTTagList list = compound.getTagList("Presets", 10);
			for (int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound comp = list.getCompoundTagAt(i);
				Preset preset = new Preset();
				preset.load(comp);
				presetsIn.put(preset.name.toLowerCase(), preset);
			}
		}
		Preset.FillDefault(presetsIn);
		presets = presetsIn;
	}

	private NBTTagCompound loadPreset() {
		String filename = "presets.dat";
		try {
			File file = new File(dir, filename);
			if (file.exists()) { return CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath())); }
		}
		catch (Exception e) {
			LogWriter.except(e);
			try {
				File file = new File(dir, filename + "_old");
				if (file.exists()) { return CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath())); }
			}
			catch (Exception err) { LogWriter.except(err); }
		}
		return null;
	}

	public void removePreset(String preset) {
		if (preset == null) {
			return;
		}
		presets.remove(preset.toLowerCase());
		save();
	}

	public void save() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (Preset preset : presets.values()) { list.appendTag(preset.save()); }
		compound.setTag("Presets", list);
		savePreset(compound);
	}

	private void savePreset(NBTTagCompound compound) {
		String filename = "presets.dat";
		try {
			File file = new File(dir, filename + "_new");
			File file1 = new File(dir, filename + "_old");
			File file2 = new File(dir, filename);
			CompressedStreamTools.writeCompressed(compound, Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		}
		catch (Exception e) { LogWriter.except(e); }
	}
}
