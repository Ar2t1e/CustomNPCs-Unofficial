package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketDropTemplateClear;
import noppes.npcs.packets.client.PacketDropTemplateSave;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.entity.data.AttributeSet;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.entity.data.EnchantSet;

public class DropController {

	protected static DropController instance = new DropController();
	public final Map<String, DropsTemplate> templates = new TreeMap<>();

	public static DropController getInstance() {
		if (instance == null) { instance = new DropController(); }
		return instance;
	}

	public List<DropSet> getDrops(String saveDropsName) {
		List<DropSet> allDrops = new ArrayList<>();
		if (saveDropsName == null || saveDropsName.isEmpty() || !templates.containsKey(saveDropsName)) { return allDrops; }
		DropsTemplate template = templates.get(saveDropsName);
		if (template == null) { return allDrops; }
		return template.getDrops();
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtFile = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (String template : templates.keySet()) {
			NBTTagCompound nbtTemplate = new NBTTagCompound();
			nbtTemplate.setString("Name", template);
			nbtTemplate.setTag("Groups", templates.get(template).getNBT());
			list.appendTag(nbtTemplate);
		}
		nbtFile.setTag("Templates", list);
		return nbtFile;
	}

	private void loadDefaultDrops() {
		DropsTemplate temp = new DropsTemplate();
		temp.groups.remove("default");
		String name = "Group #0";
		temp.groups.put(name, new TreeMap<>());
		DropSet ds0 = new DropSet(null);
		ds0.amount[0] = 5;
		ds0.amount[1] = 8;
		ds0.chance = 72.5d;
		ds0.item = new ItemStack(Items.COAL);
		ds0.pos = 0;
		temp.groups.get(name).put(0, ds0);
		DropSet ds1 = new DropSet(null);
		ds1.amount[0] = 2;
		ds1.amount[1] = 5;
		ds1.chance = 8.0d;
		ds1.item = new ItemStack(Items.IRON_INGOT);
		ds1.pos = 1;
		temp.groups.get(name).put(1, ds1);
		DropSet ds2 = new DropSet(null);
		ds2.amount[0] = 1;
		ds2.amount[1] = 3;
		ds2.chance = 4.3333d;
		ds2.item = new ItemStack(Items.GOLD_INGOT);
		ds2.pos = 2;
		temp.groups.get(name).put(2, ds2);
		DropSet ds3 = new DropSet(null);
		ds3.amount[0] = 1;
		ds3.amount[1] = 2;
		ds3.chance = 0.575d;
		ds3.item = new ItemStack(Items.DIAMOND);
		ds3.pos = 3;
		temp.groups.get(name).put(3, ds3);

		name = "Group #1";
		temp.groups.put(name, new TreeMap<>());
		DropSet df0 = new DropSet(null);
		df0.amount[0] = 1;
		df0.amount[1] = 1;
		df0.chance = 2.5d;
		df0.item = new ItemStack(Items.IRON_AXE);
		df0.pos = 0;
		EnchantSet ench0 = (EnchantSet) df0.addEnchant(Enchantments.UNBREAKING);
		ench0.setChance(50.0d);
		ench0.setLevels(1, 5);
		AttributeSet attr = (AttributeSet) df0.addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		attr.setChance(25.0d);
		attr.setValues(1.0d, 3.0d);
		attr.setSlot(0);
		df0.addDropNbtSet(8, 12.5d, "display.Name", new String[] { "Sword", "Axe" });
		temp.groups.get(name).put(0, df0);
		templates.put("default", temp);
		save();
	}

	public void load() {
		CustomNpcs.debugData.start(null);
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		if (saveDir != null) {
			templates.clear();
			LogWriter.info("Loading Drops");
			// OLD
			try {
				File file = new File(CustomNpcs.Dir, "drops.dat");
				if (file.exists()) {
					NBTTagCompound nbtFile = CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()));
					load(nbtFile);
				}
			} catch (Exception ignored) { }
			// new
			try {
				File file = new File(saveDir, "drops.dat");
				if (file.exists()) {
					try {
						NBTTagCompound nbtFile = CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()));
						load(nbtFile);
					} catch (IOException e) { LogWriter.error(e); }
				}
				else {
					templates.clear();
					loadDefaultDrops();
				}
			}
			catch (Exception e) {
				LogWriter.error(e);
				try {
					File file = new File(saveDir, "drops.dat_old");
					if (file.exists()) {
						try {
							NBTTagCompound nbtFile = CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()));
							load(nbtFile);
						} catch (IOException err) { LogWriter.error(err); }
					}
				} catch (Exception ee) {
					LogWriter.error(ee);
				}
			}
		}
		if (templates.isEmpty() || notValidDefaultTemplate()) { loadDefaultDrops(); }
		CustomNpcs.debugData.end(null);
	}

	protected void load(NBTTagCompound nbtFile) {
		if (nbtFile.hasKey("Templates", 9)) {
			for (int i = 0; i < nbtFile.getTagList("Templates", 10).tagCount(); i++) {
				NBTTagCompound nbtTemplate = nbtFile.getTagList("Templates", 10).getCompoundTagAt(i);
				if (nbtTemplate.hasKey("Name", 8)) {
					templates.put(nbtTemplate.getString("Name"), new DropsTemplate(nbtTemplate.getCompoundTag("Groups")));
				}
			}
		}
	}

	private boolean notValidDefaultTemplate() {
		DropsTemplate defaultTemplate = templates.get("default");
		if (defaultTemplate == null) {
			for (DropsTemplate template : templates.values()) {
				defaultTemplate = template;
				break;
			}
		}
		if (defaultTemplate == null || defaultTemplate.groups.isEmpty()) { return true; }
		for (Map<Integer, noppes.npcs.entity.data.DropSet> group : defaultTemplate.groups.values()) {
			if (group != null && !group.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void save() {
		if (CustomNpcs.Server == null) { return; }
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, "drops.dat_new");
			File file1 = new File(saveDir, "drops.dat_old");
			File file2 = new File(saveDir, "drops.dat");
			CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		} catch (Exception e) {
			LogWriter.error(e);
		}
	}

	public void sendTo(EntityPlayerMP player) {
		if (templates.isEmpty() || notValidDefaultTemplate()) { loadDefaultDrops(); }
		Map<String, DropsTemplate> tempMap = new TreeMap<>(templates);
		Packets.send(player, new PacketDropTemplateClear());
		for (String template : tempMap.keySet()) {
			NBTTagCompound nbtTemplate = new NBTTagCompound();
			nbtTemplate.setString("Name", template);
			nbtTemplate.setTag("Groups", tempMap.get(template).getNBT());
			Packets.send(player, new PacketDropTemplateSave(nbtTemplate));
		}
		Packets.send(player, new PacketGuiUpdate());
	}

}
