package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
import noppes.npcs.db.DatabaseColumn;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class Dialog implements ICompatibilty, IDialog {

   public int version = VersionCompatibility.ModRev;

   @DatabaseColumn(name = "id", type = DatabaseColumn.Type.INT)
   public int id = -1;
   @DatabaseColumn(name = "title", type = DatabaseColumn.Type.VARCHAR)
   public String title = "";
   @DatabaseColumn(name = "text", type = DatabaseColumn.Type.TEXT)
   public String text = "";
   @DatabaseColumn(name = "quest", type = DatabaseColumn.Type.INT)
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

   // New from Unofficial (BetaZavr)
   public boolean stopSound = true;
   public boolean showFits = true;
   public int delay = 0;
   public String texture = "";
   public List<StartedNpcData> startedNpcs = new ArrayList<>();

   public Dialog(DialogCategory categoryIn) { category = categoryIn; }

   public void load(CompoundTag compound) {
      id = compound.getInt("DialogId");
      loadPartial(compound);
   }

   public void loadPartial(CompoundTag compound) {
      version = compound.getInt("ModRev");
      VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
      title = compound.getString("DialogTitle");
      text = compound.getString("DialogText");
      quest = compound.getInt("DialogQuest");
      command = compound.getString("DialogCommand");
      mail.load(compound.getCompound("DialogMail"));
      hideNPC = compound.getBoolean("DialogHideNPC");
      showWheel = compound.getBoolean("DialogShowWheel");
      disableEsc = compound.getBoolean("DialogDisableEsc");

      ListTag tagList = compound.getList("Options", 10);
      options.clear();
      for(int slot = 0; slot < tagList.size(); ++slot) {
         CompoundTag option = tagList.getCompound(slot);
         DialogOption dia = new DialogOption();
         dia.load(option.getCompound("Option"));
         dia.slot = slot;
         options.put(dia.slot, dia);
      }
      availability.load(compound);
      factionOptions.load(compound);

      // New from Unofficial (BetaZavr)
      sound = null;
      if (compound.contains("DialogSound", 8)) {
         sound = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("DialogSound")));
      }
      if (compound.contains("DialogStopSound", 1)) { stopSound = compound.getBoolean("DialogStopSound"); }
      if (compound.contains("DialogShowFits", 1)) { showFits = compound.getBoolean("DialogShowFits"); }
      delay = ValueUtil.correctInt(compound.getInt("ResponseDelay"), 0, 1200);
      texture = compound.getString("DialogTexture");

      startedNpcs.clear();
      ListTag list = compound.getList("StartedNpcData", 10);
      for (int i = 0; i < list.size(); i++) { startedNpcs.add(new StartedNpcData(list.getCompound(i))); }
   }

   public void save() { DialogController.instance.saveDialog(category, this); }

   @Override
   public CompoundTag save(CompoundTag compound) {
      compound.putInt("DialogId", id);
      return saveToPartial(compound);
   }

   public CompoundTag saveToPartial(CompoundTag compound) {
      compound.putString("DialogTitle", title);
      compound.putString("DialogText", text);
      compound.putInt("DialogQuest", quest);
      compound.putString("DialogCommand", command);
      compound.put("DialogMail", mail.save());
      compound.putBoolean("DialogHideNPC", hideNPC);
      compound.putBoolean("DialogShowWheel", showWheel);
      compound.putBoolean("DialogDisableEsc", disableEsc);
      compound.putInt("ModRev", version);
      if (sound != null) { compound.putString("DialogSound", sound.toString()); }
      ListTag list = new ListTag();
      for (Map.Entry<Integer, DialogOption> entry : options.entrySet()) {
         CompoundTag nbt = new CompoundTag();
         entry.getValue().slot = entry.getKey();
         nbt.put("Option", entry.getValue().save());
         list.add(nbt);
      }
      compound.put("Options", list);
      availability.save(compound);
      factionOptions.save(compound);

      // New from Unofficial (BetaZavr)
      compound.putBoolean("DialogStopSound", stopSound);
      compound.putBoolean("DialogShowFits", showFits);
      compound.putInt("ResponseDelay", delay);
      if (texture != null && !texture.isEmpty()) { compound.putString("DialogTexture", texture); }

      list = new ListTag();
      for (StartedNpcData npcData : startedNpcs) { list.add(npcData.save()); }
      compound.put("StartedNpcData", list);

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
      CompoundTag compound = new CompoundTag();
      save(compound);
      dialog.load(compound);
      return dialog;
   }

   public Dialog copy(Player player) {
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
         option.slot = slot;
         if (option.optionType == OptionType.DISABLED || player != null && !option.isAvailable(player)) { continue; }
         if (option.optionType == OptionType.DIALOG_OPTION && !option.hasDialogs()) { continue; }
         dialog.options.put(slot, option);
      }
      // New from Unofficial (BetaZavr)
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

   // New from Unofficial (BetaZavr)
   public static class StartedNpcData {

      public final @Nonnull UUID uuid;
      public final @Nonnull ResourceKey<Level> dim;
      public int slot;

      public StartedNpcData(int slotIn, @Nonnull EntityNPCInterface npc) {
         slot = slotIn;
         uuid = npc.getUUID();
         dim = npc.level().dimension();
      }

      public StartedNpcData(@Nonnull CompoundTag compound) {
         slot = compound.getInt("Slot");
         uuid = compound.getUUID("UUID");
         dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("Dimension")));
      }

      public CompoundTag save() {
         CompoundTag compound = new CompoundTag();
         compound.putInt("Slot", slot);
         compound.putUUID("UUID", uuid);
         compound.putString("Dimension", dim.location().toString());
         return compound;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) { return true; }
         return obj instanceof StartedNpcData npcData && npcData.slot == slot && npcData.uuid.equals(uuid) && npcData.dim.equals(dim);
      }

   }

   public boolean hasDialogs(Player player) {
      for (DialogOption option : options.values()) {
         if (option != null && option.optionType == OptionType.DIALOG_OPTION && option.hasDialogs() && option.isAvailable(player)) {
            return true;
         }
      }
      return false;
   }

   public Component getKey() {
      return Component.empty()
              .append(Component.literal("ID:" + id).withStyle(ChatFormatting.GRAY))
              .append(Component.literal(" " + category.title + "/").withStyle(ChatFormatting.DARK_GRAY))
              .append(Component.literal(title).withStyle(ChatFormatting.RESET));
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
         }
         else if (id == optionId) {
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
         }
         else if (id == optionId + 1) {
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
                 npcData.uuid.equals(npc.getUUID()) &&
                 npcData.dim.equals(npc.level().dimension())) {
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
                 npcData.uuid.equals(npc.getUUID()) &&
                 npcData.dim.equals(npc.level().dimension())) {
            startedNpcs.remove(npcData);
            save();
            break;
         }
      }
   }

}
