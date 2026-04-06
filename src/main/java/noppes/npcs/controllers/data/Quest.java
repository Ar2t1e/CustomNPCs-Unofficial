package noppes.npcs.controllers.data;

import com.google.common.base.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.*;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ICompatibilty;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.ICustomDrop;
import noppes.npcs.api.handler.data.IDropSetData;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.constants.EnumRewardType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.db.DatabaseColumn;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketQuestCompletion;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.client.gui.util.quests.QuestInterface;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class Quest implements ICompatibilty, IQuest, Predicate<EntityNPCInterface>, IDropSetData {

   @DatabaseColumn(name = "id", type = DatabaseColumn.Type.INT)
   public int id = -1;
   @DatabaseColumn(name = "title", type = DatabaseColumn.Type.VARCHAR)
   public String title = "default";
   @DatabaseColumn(name = "repeat_type", type = DatabaseColumn.Type.ENUM)
   public EnumQuestRepeat repeat = EnumQuestRepeat.NONE;
   @DatabaseColumn(name = "completion_type", type = DatabaseColumn.Type.ENUM)
   public EnumQuestCompletion completion = EnumQuestCompletion.Npc;
   @DatabaseColumn(name = "category", type = DatabaseColumn.Type.VARCHAR)
   public QuestCategory category;
   @DatabaseColumn(name = "log_text", type = DatabaseColumn.Type.TEXT)
   public String logText = "";
   @DatabaseColumn(name = "complete_text", type = DatabaseColumn.Type.TEXT)
   public String completeText = "";
   @DatabaseColumn(name = "next_quest", type = DatabaseColumn.Type.INT)
   public int nextQuestId = -1;
   @DatabaseColumn(name = "command", type = DatabaseColumn.Type.TEXT)
   public String command = "";
   @DatabaseColumn(name = "quest_data", type = DatabaseColumn.Type.JSON)
   public CompoundTag questData = new CompoundTag();
   @DatabaseColumn(name = "reward_exp", type = DatabaseColumn.Type.INT)
   public int rewardExp = 0;
   @DatabaseColumn(name = "faction_options", type = DatabaseColumn.Type.JSON)
   public FactionOptions factionOptions = new FactionOptions();

   public int version = VersionCompatibility.ModRev;
   public PlayerMail mail = new PlayerMail();
   public final QuestInterface questInterface = new QuestInterface();
   public final Map<Integer, DropSet> rewardItems = new TreeMap<>();

   // New from Unofficial (BetaZavr)
   @DatabaseColumn(name = "cancelable", type = DatabaseColumn.Type.BOOLEAN)
   public boolean cancelable = false;
   @DatabaseColumn(name = "show_progress_in_chat", type = DatabaseColumn.Type.BOOLEAN)
   public boolean showProgressInChat = true;
   @DatabaseColumn(name = "show_progress_in_window", type = DatabaseColumn.Type.BOOLEAN)
   public boolean showProgressInWindow = true;
   @DatabaseColumn(name = "show_reward_text", type = DatabaseColumn.Type.BOOLEAN)
   public boolean showRewardText = true;
   @DatabaseColumn(name = "level", type = DatabaseColumn.Type.INT)
   public int level = 0;
   @DatabaseColumn(name = "reward_money", type = DatabaseColumn.Type.INT)
   public int rewardMoney = 0;
   @DatabaseColumn(name = "reward_donat", type = DatabaseColumn.Type.INT)
   public int rewardDonat = 0;
   @DatabaseColumn(name = "step", type = DatabaseColumn.Type.INT)
   public int step = 0;
   @DatabaseColumn(name = "extra_button", type = DatabaseColumn.Type.INT)
   public int extraButton = 0;
   @DatabaseColumn(name = "forget_dialogues", type = DatabaseColumn.Type.JSON)
   public int[] forgetDialogues = new int[0];
   @DatabaseColumn(name = "forget_quests", type = DatabaseColumn.Type.JSON)
   public int[] forgetQuests = new int[0];
   @DatabaseColumn(name = "completer_pos", type = DatabaseColumn.Type.JSON)
   public int[] completerPos = new int[] { 0, 0, 0 };
   @DatabaseColumn(name = "completer_pos_dimension", type = DatabaseColumn.Type.VARCHAR)
   public ResourceKey<Level> completerPosDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("minecraft", "overworld"));
   @DatabaseColumn(name = "next_quest_title", type = DatabaseColumn.Type.TEXT)
   public String nextQuestTitle = "";
   @DatabaseColumn(name = "reward_text", type = DatabaseColumn.Type.TEXT)
   public String rewardText = "";
   @DatabaseColumn(name = "extra_button_text", type = DatabaseColumn.Type.TEXT)
   public String extraButtonText = "";
   @DatabaseColumn(name = "icon", type = DatabaseColumn.Type.JSON)
   public ResourceLocation icon = new ResourceLocation(CustomNpcs.MODID, "textures/quest/icon/q_0.png");
   public ResourceLocation texture = null;
   @DatabaseColumn(name = "reward_type", type = DatabaseColumn.Type.ENUM)
   public EnumRewardType rewardType = EnumRewardType.RANDOM_ONE;
   @DatabaseColumn(name = "completer", type = DatabaseColumn.Type.VARCHAR)
   public EntityNPCInterface completer = null;
   @DatabaseColumn(name = "completer_uuid", type = DatabaseColumn.Type.JSON)
   private UUID completerUUID = null;

   public Quest(QuestCategory categoryIn) { category = categoryIn; }

   public void load(CompoundTag compound) {
      id = compound.getInt("Id");
      loadPartial(compound);
   }

   public void loadPartial(CompoundTag compound) {
      version = compound.getInt("ModRev");
      VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
      title = compound.getString("Title");
      logText = compound.getString("Text");
      completeText = compound.getString("CompleteText");
      command = compound.getString("QuestCommand");
      nextQuestId = compound.getInt("NextQuestId");
      rewardExp = compound.getInt("RewardExp");

      rewardItems.clear();
      if (compound.contains("Rewards", 10)) {
         ListTag tagList = compound.getCompound("Rewards").getList("NpcMiscInv", 10);
         for(int i = 0, j = 0; i < tagList.size(); ++i) {
            DropSet ds = new DropSet(this);
            ds.setItem(0, ItemStack.of(tagList.getCompound(i)));
            ds.chance = 100.0d;
            ds.amount = new int[] { ds.item.getCount(), ds.item.getCount() };
            ds.pos = j++;
            rewardItems.put(ds.pos, ds);
            LogWriter.info("TEST: pos "+ds.pos);
         }
      } // OLD
      else {
         for (int i = 0; i < compound.getList("Rewards", 10).size(); i++) {
            DropSet ds = new DropSet(this);
            ds.load(compound.getList("Rewards", 10).getCompound(i));
            ds.pos = i;
            rewardItems.put(i, ds);
         }
      } // NEW
      completion = EnumQuestCompletion.values()[compound.getInt("QuestCompletion")];
      repeat = EnumQuestRepeat.values()[compound.getInt("QuestRepeat")];
      questInterface.load(compound, id);
      factionOptions.load(compound.getCompound("QuestFactionPoints"));
      mail.load(compound.getCompound("QuestMail"));

      // New from Unofficial (BetaZavr)
      rewardType = EnumRewardType.values()[compound.getInt("RewardType")];
      rewardMoney = compound.getInt("RewardMoney");
      rewardDonat = compound.getInt("RewardDonat");
      nextQuestTitle = compound.getString("NextQuestTitle");
      if (hasNewQuest()) { nextQuestTitle = getNextQuest().title; }
      else { nextQuestTitle = ""; }
      if (compound.contains("QuestIcon", 8)) { icon = new ResourceLocation(compound.getString("QuestIcon")); }
      else { icon = new ResourceLocation(CustomNpcs.MODID, "textures/quest/icon/q_0.png"); }
      if (compound.contains("QuestTexture", 8)) { texture = new ResourceLocation(compound.getString("QuestTexture")); }
      else { texture = null; }
      extraButtonText = compound.getString("ExtraButtonText");
      level = compound.getInt("QuestLevel");
      cancelable = compound.getBoolean("Cancelable");
      if (compound.contains("ShowProgressInChat", 1)) { showProgressInChat = compound.getBoolean("ShowProgressInChat"); }
      if (compound.contains("ShowProgressInWindow", 1)) { showProgressInWindow = compound.getBoolean("ShowProgressInWindow"); }
      if (compound.contains("ShowRewardText", 1)) { showRewardText = compound.getBoolean("ShowRewardText"); }
      setExtraButton(compound.getInt("ExtraButton"));
      rewardText = compound.getString("AddRewardText");
      step = compound.getInt("Step") % 3;
      if (step < 0) { step *= -1; }
      forgetDialogues = compound.getIntArray("ForgetDialogues");
      forgetQuests = compound.getIntArray("ForgetQuests");
      completer = null;
      completerUUID = null;
      if (compound.contains("CompleterPos", 11)) {
         completerPos = compound.getIntArray("CompleterPos");
         completerPosDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("CompleterPosDimension")));
      }
      try {
         String name = compound.getCompound("CompleterNpc").getString("Name");
         if (compound.contains("CompleterNpc", 8)) { name = compound.getString("CompleterNpc"); } // OLD
         else if (compound.contains("CompleterNpc", 10) &&
                 compound.getCompound("CompleterNpc").contains("UUID") &&
                 Objects.requireNonNull(compound.getCompound("CompleterNpc").get("UUID")).getType() == IntArrayTag.TYPE) {
               completerUUID = compound.getCompound("CompleterNpc").getUUID("UUID");
         }
         List<Level> levels = new ArrayList<>();
         if (CustomNpcs.Server != null) {
            for (ServerLevel level : CustomNpcs.Server.getAllLevels()) { levels.add(level); }
         }
         else if (CustomNpcs.proxy.getPlayer() != null) { levels.add(CustomNpcs.proxy.getPlayer().level()); }
         try {
            for (Level level : levels) {
               for (EntityNPCInterface npc : level.getEntities(CustomEntities.entityCustomNpc,
                       new AABB(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
                       this)) {
                  if (npc.getName().getString().equals(name)) {
                     completer = npc;
                     if (completerUUID == null) { completerUUID = npc.getUUID(); }
                     break;
                  }
               }
               if (completer != null) { break; }
            }
         }
         catch (Exception ignored) { }
         if (completer == null && !levels.isEmpty() && levels.get(0) != null) {
            Optional<Entity> type = EntityType.create(compound, levels.get(0));
            if (type.isPresent()) {
               Entity entity = type.get();
               if (entity instanceof EntityNPCInterface npc) {
                  completer = npc;
                  completerUUID = npc.getUUID();
               }
            }
         }
      } catch (Exception e) { LogWriter.error(e); }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      compound.putInt("Id", id);
      return savePartial(compound);
   }

   public CompoundTag savePartial(CompoundTag compound) {
      compound.putInt("ModRev", version);
      compound.putString("Title", title);
      compound.putString("Text", logText);
      compound.putString("CompleteText", completeText);
      compound.putInt("NextQuestId", nextQuestId);
      compound.putInt("RewardExp", rewardExp);
      compound.putString("QuestCommand", command);
      compound.putInt("QuestCompletion", completion.ordinal());
      compound.putInt("QuestRepeat", repeat.ordinal());
      questInterface.save(compound);
      compound.put("QuestFactionPoints", factionOptions.save(new CompoundTag()));
      compound.put("QuestMail", mail.save());

      // New from Unofficial (BetaZavr)
      compound.putString("NextQuestTitle", nextQuestTitle);
      compound.putInt("RewardMoney", rewardMoney);
      compound.putInt("RewardDonat", rewardDonat);
      compound.putString("QuestIcon", icon.toString());
      if (texture != null) { compound.putString("QuestTexture", texture.toString()); }
      compound.putInt("RewardType", rewardType.ordinal());
      compound.putInt("QuestLevel", level);
      compound.putBoolean("Cancelable", cancelable);
      compound.putBoolean("ShowProgressInChat", showProgressInChat);
      compound.putBoolean("ShowProgressInWindow", showProgressInWindow);
      compound.putBoolean("ShowRewardText", showRewardText);
      compound.putString("ExtraButtonText", extraButtonText);
      compound.putInt("ExtraButton", extraButton);
      compound.putString("AddRewardText", rewardText);
      compound.putInt("Step", step);
      compound.putIntArray("ForgetDialogues", forgetDialogues);
      compound.putIntArray("ForgetQuests", forgetQuests);

      if (completer != null) {
         CompoundTag npcNbt = new CompoundTag();
         completer.save(npcNbt);
         compound.put("CompleterNpc", npcNbt);
         if (completerPos[0] == 0 && completerPos[1] == 0 && completerPos[2] == 0 && completerPosDimension.location().toString().equals("minecraft:overworld")) {
            completerPos[0] = (int) Math.floor(completer.getX());
            completerPos[1] = (int) Math.floor((completer.getY() + 0.5d));
            completerPos[2] = (int) Math.floor(completer.getZ());
            completerPosDimension = completer.level().dimension();
         }
      }
      compound.putIntArray("CompleterPos", completerPos);
      compound.putString("CompleterPosDimension", completerPosDimension.location().toString());

      ListTag dropList = new ListTag();
      int s = 0;
      for (int slot : rewardItems.keySet()) {
         if (rewardItems.get(slot) == null) { continue; }
         if (rewardItems.get(slot).pos != s) { rewardItems.get(slot).pos = s; }
         dropList.add(rewardItems.get(slot).save());
         s++;
      }
      compound.put("Rewards", dropList);

      return compound;
   }

   public boolean hasNewQuest() { return getNextQuest() != null; }

   @Override
   public Quest getNextQuest() { return QuestController.instance == null ? null : QuestController.instance.quests.get(nextQuestId); }

   public boolean complete(Player player, QuestData data) {
      if (completion == EnumQuestCompletion.Instant) {
         if (player instanceof ServerPlayer sPlayer) { Packets.send(sPlayer, new PacketQuestCompletion(data.quest.id)); }
         return true;
      }
      return false;
   }

   public Quest copy() {
      Quest quest = new Quest(category);
      quest.load(save(new CompoundTag()));
      return quest;
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
   public IQuestCategory getCategory() { return category; }

   @Override
   public void save() { QuestController.instance.saveQuest(category, this); }

   @Override
   public void setName(String name) { title = name; }

   @Override
   public void setLogText(String text) { logText = text; }

   @Override
   public String getCompleteText() { return completeText; }

   @Override
   public void setCompleteText(String text) { completeText = text == null ? "" : text; }

   @Override
   public void setNextQuest(IQuest quest) {
      if (quest == null) {
         nextQuestId = -1;
         nextQuestTitle = "";
      }
      else {
         if (quest.getId() < 0) { throw new CustomNPCsException("Quest id is lower than 0"); }
         nextQuestId = quest.getId();
         nextQuestTitle = quest.getTitle().getString();
      }
   }

   public QuestObjective[] getObjectives(Player player) {
      if (player == null) { throw new CustomNPCsException("Player is NULL"); }
      PlayerData data = PlayerData.get(player);
      if (data == null || !data.questData.activeQuests.containsKey(id)) { throw new CustomNPCsException("Player doesnt have this quest active"); }
      return questInterface.getObjectives(player);
   }

   @Override
   public IQuestObjective[] getObjectives(IPlayer<?> player) {
      if (player == null) { throw new CustomNPCsException("Player is NULL"); }
      if (!player.hasActiveQuest(id)) { throw new CustomNPCsException("Player doesnt have this quest active"); }
      return questInterface.getObjectives(player.getMCEntity());
   }

   @Override
   public boolean getIsRepeatable() { return repeat != EnumQuestRepeat.NONE; }

   // New from Unofficial (BetaZavr)
   @Override
   public IQuestObjective addTask() { return questInterface.addTask(EnumQuestTask.ITEM); }

   @Override
   public ICustomNpc<?> getCompleterNpc() {
      if (completer == null) { return null; }
      return (ICustomNpc<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(completer);
   }

   @Override
   public int getExtraButton() { return extraButton; }

   @Override
   public String getExtraButtonText() { return extraButtonText; }

   @Override
   public int[] getForgetDialogues() { return forgetDialogues; }

   @Override
   public int[] getForgetQuests() { return forgetQuests; }

   @Override
   public int getLevel() { return level; }

   @Override
   public int getRewardType() { return rewardType.ordinal(); }

   @Override
   public MutableComponent getTitle() {
      MutableComponent titleCom = Component.empty();
      if (level > 0) {
         titleCom.append(Component.literal("Lv." + level)
                 .withStyle(level <= CustomNpcs.MaxLv / 3 ? ChatFormatting.DARK_GREEN :
                         (float) level <= (float) CustomNpcs.MaxLv / 1.5f ? ChatFormatting.YELLOW : ChatFormatting.RED))
                 .append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
      }
      return titleCom.append(Component.translatable(title));
   }

   @Override
   public boolean isCancelable() { return cancelable; }

   @Override
   public boolean isSetUp() {
      if (questInterface.tasks.length == 0) { return false; }
      for (QuestObjective task : questInterface.tasks) {
         if ((task.getEnumType() == EnumQuestTask.ITEM || task.getEnumType() == EnumQuestTask.CRAFT)) {
            if (task.getItemStack().isEmpty()) { return false; }
         }
         else if (task.getEnumType() == EnumQuestTask.DIALOG) {
            if (DialogController.instance.dialogs.get(task.getTargetID()) == null) { return false; }
         }
         else if (task.getTargetName().isEmpty()) { return false; }
      }
      return true;
   }

   @Override
   public boolean removeTask(IQuestObjective task) { return questInterface.removeTask((QuestObjective) task); }

   @OnlyIn(Dist.DEDICATED_SERVER)
   @Override
   public void sendChangeToAll() { Packets.sendAll(new PacketSyncUpdate(id, 2, save(new CompoundTag()))); }

   @Override
   public void setCancelable(boolean cancelableIn) { cancelable = cancelableIn; }

   @Override
   public void setCompleterNpc(ICustomNpc<?> npc) { completer = (EntityNPCInterface) npc.getMCEntity(); }

   @Override
   public void setExtraButton(int type) {
      if (type < 0) { type *= -1; }
      extraButton = type % 6;
   }

   @Override
   public void setExtraButtonText(String hover) { extraButtonText = hover == null ? "" : hover; }

   @Override
   public void setForgetDialogues(int[] forget) { forgetDialogues = forget; }

   @Override
   public void setForgetQuests(int[] forget) { forgetQuests = forget; }

   @Override
   public void setLevel(int levelIn) {
      if (levelIn < 0 ) { levelIn *= -1; }
      level = ValueUtil.correctInt(levelIn, 1, CustomNpcs.MaxLv);
   }

   @Override
   public void setRewardText(String text) { rewardText = text == null ? "" : text; }

   @Override
   public void setRewardType(int type) {
      if (type < 0) { type *= -1; }
      rewardType = EnumRewardType.values()[type % EnumRewardType.values().length];
   }

   @Override
   public List<ICustomDrop> getRewards() { return new ArrayList<>(rewardItems.values()); }

   public Component getLineKey() {
      boolean b = isSetUp();
      return Component.empty()
              .append(Component.literal("ID:" + id + "-\"").withStyle(ChatFormatting.GRAY))
              .append(getTitle().withStyle(ChatFormatting.RESET))
              .append(Component.literal("\"").withStyle(ChatFormatting.GRAY))
              .append(Component.literal(" (").withStyle(b ? ChatFormatting.DARK_GREEN : ChatFormatting.RED))
              .append(Component.translatable("quest.has." + b))
              .append(Component.literal(")").withStyle(b ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
   }

   @Override
   public boolean apply(EntityNPCInterface npc) { return completerUUID == null || npc.getUUID().equals(completerUUID); }

   @Override
   public List<String> getLogText() {
      List<String> allTextLogs = new ArrayList<>();
      if (showRewardText) {
         List<TempDropData> list = new ArrayList<>();
         for (int i = 0; i < rewardItems.size(); i++) {
            DropSet ds = rewardItems.get(i);
            if (!ds.item.isEmpty()) {
               boolean has = false;
               if (rewardType == EnumRewardType.ALL) {
                  for (TempDropData tdd : list) {
                     if (ItemStack.isSameItemSameTags(ds.item, tdd.stack)) {
                        tdd.add(ds);
                        has = true;
                        break;
                     }
                  }
               }
               if (!has) { list.add(new TempDropData(ds)); }
            }
         }
         if (!list.isEmpty() || rewardExp > 0 || rewardMoney > 0 || rewardDonat > 0 ||!rewardText.isEmpty()) {
            allTextLogs.add("");
            allTextLogs.add(Util.instance.getOldFormattedText(Component.translatable("questlog.reward")));
         }
         if (!list.isEmpty()) {
            allTextLogs.add(Util.instance.getOldFormattedText(Component.translatable("questlog." + (rewardType == EnumRewardType.ONE_SELECT ? "one" :
                            rewardType == EnumRewardType.RANDOM_ONE ? "rnd" : "all") + ".reward")));
            for (TempDropData tdd : list) {
               StringBuilder line = new StringBuilder(" -  ")
                       .append((char) 0xffff).append(" ")
                       .append(Util.instance.getOldFormattedText(tdd.stack.getHoverName()));
               if ((tdd.min == tdd.max || tdd.max < 1) && tdd.min > 1) { line.append(" x").append(tdd.min); }
               else if (tdd.max > 1 || tdd.min > 1) {
                  line.append(" x(")
                          .append(tdd.min > 1 ? tdd.min : "1")
                          .append("...")
                          .append(tdd.max > 1 ? tdd.max : "1")
                          .append(")");
               }

               allTextLogs.add(line.toString());
            }
         }
         if (rewardMoney > 0) {
            allTextLogs.add(Util.instance.getOldFormattedText(Component.translatable("questlog.rewardmoney",
                    Util.instance.getTextReducedNumber(rewardMoney, true, true, false),
                    CustomNpcs.displayCurrencies)));
         }
         if (rewardDonat > 0) {
            allTextLogs.add(Util.instance.getOldFormattedText(Component.translatable("questlog.rewarddonat",
                    Util.instance.getTextReducedNumber(rewardDonat, true, true, false),
                    CustomNpcs.displayCurrencies)));
         }
         if (rewardExp > 0) {
            allTextLogs.add(Util.instance.getOldFormattedText(Component.translatable("questlog.rewardexp", "" + rewardExp)));
         }
      }
      if (!rewardText.isEmpty()) {
         allTextLogs.add(rewardText.contains("%") ? rewardText : Util.instance.getOldFormattedText(Component.translatable(rewardText)));
      }
      if (!logText.isEmpty()) {
         allTextLogs.add("");
         allTextLogs.add(ChatFormatting.BOLD + Util.instance.getOldFormattedText(Component.translatable("gui.description")));
         allTextLogs.add(logText.contains("%") ? logText :
                         Util.instance.getOldFormattedText(Component.translatable(logText)));
      }
      return allTextLogs;
   }

   public boolean hasCompassSettings() {
      for (QuestObjective task : questInterface.tasks) {
         if (task.rangeCompass > 3 && !task.pos.equals(BlockPos.ZERO)) { return true; }
      }
      return false;
   }

   @Override
   public int getNpcLevel() { return level; }

   @Override
   public boolean removeDrop(DropSet dropSet) {
      Map<Integer, DropSet> newDrop = new TreeMap<>();
      boolean del = false;
      int j = 0;
      for (int slot : rewardItems.keySet()) {
         if (rewardItems.get(slot) == dropSet) {
            del = true;
            continue;
         }
         newDrop.put(j, rewardItems.get(slot));
         newDrop.get(j).pos = j;
         j++;
      }
      if (del) {
         rewardItems.clear();
         rewardItems.putAll(newDrop);
      }
      return del;
   }

   public static class TempDropData {

      private boolean isCreate = false;
      private final List<Double> chances = new ArrayList<>();
      public final ItemStack stack;
      public int min;
      public int max;

      public TempDropData(DropSet ds) {
         min = ds.amount[0];
         max = ds.amount[1];
         chances.add(ds.chance);
         stack = ds.item.copy();
      }

      public TempDropData add(DropSet ds) {
         min += ds.amount[0];
         max += ds.amount[1];
         chances.add(ds.chance);
         return this;
      }

      public ItemStack getStack() {
         if (!isCreate) {
            isCreate = true;
            double chance = 0.0d;
            for (double ch : chances) { chance += ch; }
            chance /= chances.size();
            chance = ValueUtil.correctDouble(chance, 0.0d, 100.0d);
            if (chance != 100.0d) {
               CompoundTag compound = stack.getOrCreateTagElement("display");
               ListTag tagList = compound.getList("Lore", 8);
               tagList.add(StringTag.valueOf(Component.Serializer.toJson(
                       Component.translatable("inv.dropChance").append(": " + (Math.round(chance * 10.0d) / 10.0d) + "%"))));
               compound.put("Lore", tagList);
            }
         }
         return stack;
      }

   }

}
