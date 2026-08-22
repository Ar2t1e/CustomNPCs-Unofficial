package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.TreeMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.handler.IKeyBinding;
import noppes.npcs.api.handler.data.IKeySetting;
import noppes.npcs.controllers.data.KeyConfig;

public class KeyController implements IKeyBinding {

	protected static KeyController instance = new KeyController();
	public final TreeMap<Integer, IKeySetting> keybindings = new TreeMap<>();

	public static KeyController getInstance() {
		if (instance == null) { instance = new KeyController(); }
		return instance;
	}

	@Override
	public KeyConfig createKeySetting() {
		KeyConfig ac = new KeyConfig(getUnusedId());
		keybindings.put(ac.getId(), ac);
		update(ac.getId());
		return ac;
	}

	@Override
	public KeyConfig getKeySetting(int id) { return (KeyConfig) keybindings.get(id); }

	@SuppressWarnings("unused")
	public KeyConfig getKeySetting(String name, String category, int keyId, String modifier) {
		for (IKeySetting kb : keybindings.values()) {
			if (kb.getKeyId() != keyId) { continue; }
			KeyConfig kc = (KeyConfig) kb;
			if (!kc.name.equals(name) || !kc.category.equals(category)) { continue; }
			switch (modifier.toLowerCase()) {
				case "shift": {
					if (kc.modifer == 1) { return kc; }
					break;
				}
				case "control": {
					if (kc.modifer == 2) { return kc; }
					break;
				}
				case "alt": {
					if (kc.modifer == 3) { return kc; }
					break;
				}
				default: return kc;
			}
		}
		return null;
	}

	@Override
	public IKeySetting[] getKeySettings() { return keybindings.values().toArray(new IKeySetting[0]); }

	public NBTTagCompound getNBT() {
		NBTTagList list = new NBTTagList();
		for (int id : keybindings.keySet()) {
			NBTTagCompound nbtKey = ((KeyConfig) keybindings.get(id)).save();
			nbtKey.setInteger("ID", id);
			list.appendTag(nbtKey);
		}
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("Data", list);
		return compound;
	}

	public int getUnusedId() {
		int id = 0;
		for (int i : keybindings.keySet()) {
			if (i >= id) { id = i + 1; }
		}
		return id;
	}

	private void loadDefaultKeys() {
		KeyConfig ac = new KeyConfig(0);
		keybindings.put(0, ac);
		save();
	}

	public void loadKey(NBTTagCompound nbtKey) {
		if (nbtKey == null || !nbtKey.hasKey("ID", 3) || nbtKey.getInteger("ID") < 0) { return; }
		int id = nbtKey.getInteger("ID");
		KeyConfig ac;
		if (keybindings.containsKey(id)) {
			((KeyConfig) keybindings.get(id)).load(nbtKey);
			keybindings.get(id);
			return;
		}
		ac = new KeyConfig(id);
		ac.load(nbtKey);
		keybindings.put(id, ac);
		keybindings.get(id);
	}

	public void loadKeys() {
		CustomNpcs.debugData.start(null);
		File saveDir = CustomNpcs.Dir;
		if (saveDir == null) { return; }
		try {
			File file = new File(saveDir, "keys.dat");
			if (file.exists()) { loadKeys(file); }
			else { loadDefaultKeys(); }
		}
		catch (Exception e) { loadDefaultKeys(); }
		CustomNpcs.debugData.end(null);
	}

	private void loadKeys(File file) throws IOException {
		loadKeys(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath())));
	}

	public void loadKeys(NBTTagCompound compound) {
		keybindings.clear();
		if (compound != null) {
			if (compound.hasKey("Data", 9)) {
				for (int i = 0; i < compound.getTagList("Data", 10).tagCount(); ++i) {
					loadKey(compound.getTagList("Data", 10).getCompoundTagAt(i));
				}
			}
		}
	}

	@Override
	public void removeKeySetting(int id) { keybindings.remove(id); }

	public void save() {
		CustomNpcs.debugData.start(null);
		try { CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(new File(CustomNpcs.Dir, "keys.dat").toPath())); }
		catch (Exception e) { LogWriter.error(e); }
		CustomNpcs.debugData.end(null);
	}

	public void update(int id) {
		IKeySetting kb = keybindings.get(id);
		if (kb != null) { Packets.sendAll(new PacketSyncUpdate(0, 9, kb.getNbt().getMCNBT())); } // change or add
		else { Packets.sendAll(new PacketSyncRemove(id, 8)); } // remove
	}

}
