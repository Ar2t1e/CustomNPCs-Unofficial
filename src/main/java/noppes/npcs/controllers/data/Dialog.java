package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.ICompatibilty;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class Dialog implements ICompatibilty, IDialog {

	public int version = VersionCompatibility.ModRev;

	public int id = -1;
	public String title = "";
	public String text = "";
	public int quest = -1;

	public DialogCategory category;
	public final Map<Integer, DialogOption> options = new TreeMap<>();
	public Availability availability = new Availability();
	public FactionOptions factionOptions = new FactionOptions();
	public PlayerMail mail = new PlayerMail();
	public ResourceLocation sound;
	public String command = "";
	public boolean hideNPC = false;
	public boolean showWheel = false;
	public boolean disableEsc = false;

	// New from BetaZavr
	public boolean stopSound = true;
	public boolean showFits = true;
	public int delay = 0;
	public String texture = "";
	public List<StartedNpcData> startedNpcs = new ArrayList<>();

	public Dialog(DialogCategory categoryIn) { category = categoryIn; }

	public void load(NBTTagCompound compound) {
		id = compound.getInteger("DialogId");
		loadPartial(compound);
	}

	public void loadPartial(NBTTagCompound compound) {
		version = compound.getInteger("ModRev");
		VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
		title = compound.getString("DialogTitle");
		text = compound.getString("DialogText");
		quest = compound.getInteger("DialogQuest");
		command = compound.getString("DialogCommand");
		mail.load(compound.getCompoundTag("DialogMail"));
		hideNPC = compound.getBoolean("DialogHideNPC");
		showWheel = compound.getBoolean("DialogShowWheel");
		disableEsc = compound.getBoolean("DialogDisableEsc");
		NBTTagList tagList = compound.getTagList("Options", 10);
		options.clear();
		for (int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound option = tagList.getCompoundTagAt(i);
			int opSlot = option.getInteger("OptionSlot");
			DialogOption dia = new DialogOption();
			dia.load(option.getCompoundTag("Option"));
			options.put(opSlot, dia);
			dia.slot = opSlot;
		}
		availability.load(compound);
		factionOptions.load(compound);

		// New from BetaZavr
		sound = null;
		if (compound.hasKey("DialogSound", 8)) {
			sound = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("DialogSound")));
		}
		if (compound.hasKey("DialogShowFits", 1)) { showFits = compound.getBoolean("DialogShowFits"); }
		if (compound.hasKey("DialogStopSound", 1)) { stopSound = compound.getBoolean("DialogStopSound"); }
		delay = ValueUtil.correctInt(compound.getInteger("ResponseDelay"), 0, 1200);
		texture = compound.getString("DialogTexture");

		startedNpcs.clear();
		NBTTagList list = compound.getTagList("StartedNpcData", 10);
		for (int i = 0; i < list.tagCount(); i++) { startedNpcs.add(new StartedNpcData(list.getCompoundTagAt(i))); }
	}

	@Override
	public void save() { DialogController.instance.saveDialog(category, this); }

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setInteger("DialogId", id);
		return saveToPartial(compound);
	}

	public NBTTagCompound saveToPartial(NBTTagCompound compound) {
		compound.setString("DialogTitle", title);
		compound.setString("DialogText", text);
		compound.setInteger("DialogQuest", quest);
		compound.setString("DialogCommand", command);
		compound.setTag("DialogMail", mail.save());
		compound.setBoolean("DialogHideNPC", hideNPC);
		compound.setBoolean("DialogShowWheel", showWheel);
		compound.setBoolean("DialogDisableEsc", disableEsc);
		if (sound != null) { compound.setString("DialogSound", sound.toString()); }
		NBTTagList list = new NBTTagList();
		for (int opSlot : options.keySet()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("OptionSlot", opSlot);
			nbt.setTag("Option", options.get(opSlot).save());
			list.appendTag(nbt);
		}
		compound.setTag("Options", list);
		availability.save(compound);
		factionOptions.save(compound);
		compound.setInteger("ModRev", version);

		// New from BetaZavr
		compound.setBoolean("DialogStopSound", stopSound);
		compound.setBoolean("DialogShowFits", showFits);
		compound.setInteger("ResponseDelay", delay);
		if (texture != null && !texture.isEmpty()) { compound.setString("DialogTexture", texture); }
		list = new NBTTagList();
		for (StartedNpcData npcData : startedNpcs) { list.appendTag(npcData.save()); }
		compound.setTag("StartedNpcData", list);

		return compound;
	}

	public boolean hasQuest() {
		Quest questIn = getQuest();
		return questIn != null && questIn.isSetUp();
	}

	@Override
	public Quest getQuest() { return QuestController.instance == null ? null : QuestController.instance.quests.get(quest); }

	@Override
	public void setQuest(IQuest questIn) {
		if (questIn == null) { quest = -1; }
		else {
			if (questIn.getId() < 0) {throw new CustomNPCsException("Quest id is lower than 0"); }
			quest = questIn.getId();
		}
	}

	public boolean notHasOtherOptions() {
		for (DialogOption option : options.values()) {
			if (option != null && option.optionType != OptionType.DISABLED) { return false; }
		}
		return true;
	}

	public Dialog copy() {
		Dialog dialog = new Dialog(category);
		NBTTagCompound compound = new NBTTagCompound();
		save(compound);
		dialog.load(compound);
		return dialog;
	}

	public Dialog copy(EntityPlayer player) {
		Dialog dialog = new Dialog(category);
		dialog.id = id;
		dialog.text = text;
		dialog.title = title;
		dialog.quest = quest;
		dialog.sound = sound;
		dialog.mail = mail;
		dialog.command = command;
		dialog.hideNPC = hideNPC;
		dialog.showWheel = showWheel;
		dialog.disableEsc = disableEsc;
		for (int slot : options.keySet()) {
			DialogOption option = options.get(slot);
			if (option.optionType == OptionType.DISABLED || player != null && !option.isAvailable(player)) { continue; }
			if (option.optionType == OptionType.DIALOG_OPTION && !option.hasDialogs()) { continue; }
			dialog.options.put(slot, option);
		}
		// New from BetaZavr
		dialog.stopSound = stopSound;
		dialog.showFits = showFits;
		dialog.delay = delay;
		dialog.texture = texture;
		return dialog;
	}

	@Override
	public int getVersion() { return version; }

	@Override
	public void setVersion(int versionIn) { version = versionIn; }

	@Override
	public int getId() { return id; }

	@Override
	public String getName() { return title; }

	@Override
	public void setName(String name) { title = name; }

	@Override
	public List<IDialogOption> getOptions() { return new ArrayList<>(options.values()); }

	@Override
	public IDialogOption getOption(int slot) {
		IDialogOption option = options.get(slot);
		if (option == null) { throw new CustomNPCsException("There is no DialogOption for slot: " + slot); }
		return option;
	}

	@Override
	public IAvailability getAvailability() { return availability; }

	@Override
	public IDialogCategory getCategory() { return category; }

	@Override
	public String getText() { return text; }

	@Override
	public void setText(String textIn) { text = textIn; }

	@Override
	public String getCommand() { return command; }

	@Override
	public void setCommand(String commandIn) { command = commandIn; }

	// New from BetaZavr
	public static class StartedNpcData {

		public final @Nonnull UUID uuid;
		public final int dim;
		public int slot;

		public StartedNpcData(int slotIn, @Nonnull EntityNPCInterface npc) {
			slot = slotIn;
			uuid = npc.getUniqueID();
			dim = npc.world.provider.getDimension();
		}

		public StartedNpcData(@Nonnull NBTTagCompound compound) {
			slot = compound.getInteger("Slot");
			uuid = Objects.requireNonNull(compound.getUniqueId("UUID"));
			dim = compound.getInteger("Dimension");
		}

		public NBTTagCompound save() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("Slot", slot);
			compound.setUniqueId("UUID", uuid);
			compound.setInteger("Dimension", dim);
			return compound;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			return obj instanceof StartedNpcData && ((StartedNpcData) obj).slot == slot && ((StartedNpcData) obj).uuid.equals(uuid) && ((StartedNpcData) obj).dim == dim;
		}

	}

	public boolean hasDialogs(EntityPlayer player) {
		for (DialogOption option : options.values()) {
			if (option != null && option.optionType == OptionType.DIALOG_OPTION && option.hasDialogs() && option.isAvailable(player)) {
				return true;
			}
		}
		return false;
	}

	public Component getKey() {
		return Component.empty()
				.append(Component.literal("ID:" + id).withStyle(TextFormatting.GRAY))
				.append(Component.literal(" " + category.title + "/").withStyle(TextFormatting.DARK_GRAY))
				.append(Component.literal(title).withStyle(TextFormatting.RESET));
	}

	public void upPos(int optionId) {
		if (!options.containsKey(optionId) || optionId <= 0) { return; }
		Map<Integer, DialogOption> newOptions = new TreeMap<>();
		for (int id : options.keySet()) {
			DialogOption option = options.get(id);
			if (id == optionId - 1) {
				option.slot = id + 1;
				newOptions.put(id + 1, option);
				continue;
			} else if (id == optionId) {
				option.slot = id - 1;
				newOptions.put(id - 1, option);
				continue;
			}
			newOptions.put(id, option);
		}
		options.clear();
		options.putAll(newOptions);
	}

	public void downPos(int optionId) {
		if (!options.containsKey(optionId) || optionId < 0 || optionId >= options.size() - 1) { return; }
		Map<Integer, DialogOption> newOptions = new TreeMap<>();
		for (int id : options.keySet()) {
			DialogOption option = options.get(id);
			if (id == optionId) {
				option.slot = id + 1;
				newOptions.put(id + 1, options.get(id));
				continue;
			} else if (id == optionId + 1) {
				option.slot = id - 1;
				newOptions.put(id - 1, options.get(id));
				continue;
			}
			newOptions.put(id, options.get(id));
		}
		options.clear();
		options.putAll(newOptions);
	}

	public void addNpc(int slot, @Nonnull EntityNPCInterface npc) {
		boolean found = false;
		for (StartedNpcData npcData : startedNpcs) {
			if (npcData.slot == slot &&
					npcData.uuid.equals(npc.getUniqueID()) &&
					npcData.dim == npc.world.provider.getDimension()) {
				found = true;
				break;
			}
		}
		if (!found) {
			startedNpcs.add(new StartedNpcData(slot, npc));
			save();
		}
	}

	public void removeNpc(int slot, EntityNPCInterface npc) {
		for (StartedNpcData npcData : new ArrayList<>(startedNpcs)) {
			if (npcData.slot == slot &&
					npcData.uuid.equals(npc.getUniqueID()) &&
					npcData.dim == npc.world.provider.getDimension()) {
				startedNpcs.remove(npcData);
				save();
				break;
			}
		}
	}

}
