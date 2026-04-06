package noppes.npcs.roles;

import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class JobConversation extends JobInterface {

   public Availability availability = new Availability();
   private final ArrayList<String> names = new ArrayList<>();
   private final HashMap<String, EntityNPCInterface> npcs = new HashMap<>();
   public TreeMap<Integer, JobConversation.ConversationLine> lines = new TreeMap<>();
   public int quest = -1;
   public int generalDelay = 400;
   public int ticks = 100;
   public int range = 20;
   private JobConversation.ConversationLine nextLine;
   private boolean hasStarted = false;
   private int startedTicks = 20;
   public boolean mode = false;

   public JobConversation(EntityNPCInterface npc) {
      super(npc);
      type = JobType.CONVERSATION;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.CONVERSATION;
      availability.load(compound.getCompound("ConversationAvailability"));
      quest = compound.getInt("ConversationQuest");
      generalDelay = compound.getInt("ConversationDelay");
      range = compound.getInt("ConversationRange");
      if (compound.contains("ConversationMode", 3)) {
         mode = compound.getInt("ConversationMode") != 0;
      } // OLD
      else { mode = compound.getBoolean("ConversationMode"); }

      ListTag tagList = compound.getList("ConversationLines", 10);
      names.clear();
      lines.clear();
      for(int i = 0; i < tagList.size(); ++i) {
         CompoundTag nbt = tagList.getCompound(i);
         JobConversation.ConversationLine line = new ConversationLine();
         line.readAdditionalSaveData(nbt);
         if (!line.npc.isEmpty() && !names.contains(line.npc.toLowerCase())) { names.add(line.npc.toLowerCase()); }
         lines.put(nbt.getInt("Slot"), line);
      }
      ticks = generalDelay;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.put("ConversationAvailability", availability.save(new CompoundTag()));
      compound.putInt("ConversationQuest", quest);
      compound.putInt("ConversationDelay", generalDelay);
      compound.putInt("ConversationRange", range);
      compound.putBoolean("ConversationMode", mode);
      ListTag tagList = new ListTag();
      for (int slot : lines.keySet()) {
         ConversationLine line = lines.get(slot);
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("Slot", slot);
         line.addAdditionalSaveData(nbt);
         tagList.add(nbt);
      }
      compound.put("ConversationLines", tagList);
      if (hasQuest()) { compound.putString("ConversationQuestTitle", getQuest().title); }
      return compound;
   }

   @Override
   public void aiUpdateTask() {
      --ticks;
      if (ticks <= 0 && nextLine != null) {
         say(nextLine);
         boolean seenNext = false;
         JobConversation.ConversationLine compare = nextLine;
         nextLine = null;
         for (ConversationLine line : lines.values()) {
            if (!line.isEmpty()) {
               if (seenNext) {
                  nextLine = line;
                  break;
               }
               if (line == compare) { seenNext = true; }
            }
         }
         if (nextLine != null) { ticks = nextLine.delay; }
         else if (hasQuest() && npc != null) {
            List<Player> inRange = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(range, range, range));
            for (Player player : inRange) {
               if (availability.isAvailable(player)) {
                  PlayerQuestController.addActiveQuest(getQuest(), player, false);
               }
            }
         }
      }
   }

   @Override
   public boolean aiShouldExecute() {
      if (!lines.isEmpty() && npc != null && !npc.isKilled() && !npc.isAttacking() && shouldRun()) {
         if (!hasStarted && mode) {
            if (startedTicks-- > 0) { return false; }
            startedTicks = 10;
            if (npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(range, range, range)).isEmpty()) { return false; }
         }
         for (ConversationLine line : lines.values()) {
            if (line != null && !line.isEmpty()) {
               nextLine = line;
               break;
            }
         }
         return nextLine != null;
      }
      return false;
   }

   @Override
   public boolean aiContinueExecute() {
      for (EntityNPCInterface npc : new ArrayList<>(npcs.values())) {
         if (npc.isKilled() || npc.isAttacking()) { return false; }
      }
      return nextLine != null;
   }

   @Override
   public void stop() {
      nextLine = null;
      ticks = generalDelay;
      hasStarted = false;
   }

   @Override
   public void aiStartExecuting() {
      startedTicks = 20;
      hasStarted = true;
   }

   @Override
   public void killed() { reset(); }

   @Override
   public void reset() {
      hasStarted = false;
      stop();
      ticks = 60;
   }

   private boolean shouldRun() {
      --ticks;
      if (ticks <= 0 && npc != null) {
         npcs.clear();
         List<EntityNPCInterface> list = npc.level().getEntitiesOfClass(EntityNPCInterface.class, npc.getBoundingBox().inflate(10.0D, 10.0D, 10.0D));
         for (EntityNPCInterface npc : list) {
            String name = npc.getName().getString().toLowerCase();
            if (!npc.isKilled() && !npc.isAttacking() && names.contains(name)) { npcs.put(name, npc); }
         }
         boolean bo = names.size() == npcs.size();
         if (!bo) { ticks = 20; }
         return bo;
      }
      return false;
   }

   public boolean hasQuest() { return getQuest() != null; }

   public Quest getQuest() { return npc == null || npc.isClientSide() ? null : QuestController.instance.quests.get(quest); }

   private void say(JobConversation.ConversationLine line) {
      if (npc != null) {
         List<Player> inRange = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(range, range, range));
         EntityNPCInterface npcIn = npcs.get(line.npc.toLowerCase());
         if (npcIn != null) {
            for (Player player : inRange) {
               if (availability.isAvailable(player)) { npcIn.say(player, line); }
            }
         }
      }
   }

   public JobConversation.ConversationLine getLine(int slot) {
      if (lines.containsKey(slot)) { return lines.get(slot); }
      JobConversation.ConversationLine line = new ConversationLine();
      lines.put(slot, line);
      return line;
   }

   public static class ConversationLine extends Line {
      public String npc = "";
      public int delay = 40;

      public void addAdditionalSaveData(CompoundTag compound) {
         compound.putString("Line", text);
         compound.putString("Npc", npc);
         compound.putString("Sound", sound);
         compound.putInt("Delay", delay);
      }

      public void readAdditionalSaveData(CompoundTag compound) {
         text = compound.getString("Line");
         npc = compound.getString("Npc");
         sound = compound.getString("Sound");
         delay = compound.getInt("Delay");
      }

      public boolean isEmpty() {return npc.isEmpty() || text.isEmpty(); }

   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isWorking() { return hasStarted; }

}
