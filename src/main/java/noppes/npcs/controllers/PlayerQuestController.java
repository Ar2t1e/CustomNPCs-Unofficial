package noppes.npcs.controllers;

import java.util.HashMap;
import java.util.Vector;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;

public class PlayerQuestController {

   @SuppressWarnings("all")
   public static boolean hasActiveQuests(Player player) {
      PlayerQuestData data = PlayerData.get(player).questData;
      return !data.activeQuests.isEmpty();
   }

   public static boolean isQuestActive(Player player, int quest) {
      PlayerQuestData data = PlayerData.get(player).questData;
      return data.activeQuests.containsKey(quest);
   }

   public static boolean isQuestCompleted(Player player, int quest) {
      PlayerQuestData data = PlayerData.get(player).questData;
      QuestData q = data.activeQuests.get(quest);
      return q != null && q.isCompleted;
   }

   public static boolean isQuestFinished(Player player, int questId) { return PlayerData.get(player).questData.hasFinishedQuest(questId); }

   public static boolean canQuestBeAccepted(Player player, int questId) {
      Quest quest = QuestController.instance.quests.get(questId);
      if (quest == null) {
         return false;
      }
      PlayerQuestData data = PlayerData.get(player).questData;
      if (data.activeQuests.containsKey(quest.id)) {
         return false;
      }if (data.hasFinishedQuest(quest.id) && quest.repeat != EnumQuestRepeat.REPEATABLE) {
         if (quest.repeat == EnumQuestRepeat.NONE) {
            return false;
         }
         long questTime = data.getFinishedTime(quest.id);
         long time;
         MinecraftServer server = player.getServer();
         if (server == null) { server = CustomNpcs.Server; }
         if (server != null) { time = server.overworld().getGameTime(); }
         else { time = player.level().getGameTime(); }
         if (quest.repeat == EnumQuestRepeat.MCDAILY) {
            return time - questTime >= 24000L;
         } else if (quest.repeat == EnumQuestRepeat.MCWEEKLY) {
            return time - questTime >= 168000L;
         } else if (quest.repeat == EnumQuestRepeat.RLDAILY) {
            return System.currentTimeMillis() - questTime >= 86400000L;
         } else if (quest.repeat == EnumQuestRepeat.RLWEEKLY) {
            return System.currentTimeMillis() - questTime >= 604800000L;
         }
         return false;
      }
      return true;
   }

   public static void addActiveQuest(Quest quest, Player player, boolean skipBeAccepted) {
      if (player == null || quest == null || !quest.isSetUp()) { return; }
      PlayerData playerdata = PlayerData.get(player);
      PlayerQuestData data = playerdata.questData;
      if (skipBeAccepted || playerdata.scriptData.getPlayer().canQuestBeAccepted(quest.id)) {
         if (EventHooks.onQuestStarted(playerdata.scriptData, quest)) { return; }
         data.activeQuests.put(quest.id, new QuestData(quest));
         Packets.send((ServerPlayer)player, new PacketAchievement(Component.translatable("quest.newquest"), Component.translatable(quest.title), 2, new CompoundTag()));
         Packets.send((ServerPlayer)player, new PacketChat(Component.translatable("quest.newquest").append(":").append(Component.translatable(quest.title))));
         playerdata.updateClient = true;
         CustomNPCsScheduler.runTack(() -> {
            int taskId = 0;
            for (QuestObjective obj : quest.getObjectives(player)) {
               if (obj.getEnumType() == EnumQuestTask.ITEM) {
                  playerdata.questData.checkQuestCompletion(player, playerdata.questData.activeQuests.get(quest.id));
               }
               if (obj.isSetPointOnMiniMap() && !playerdata.minimap.getModName().equals("non")) {
                  String name = quest.getTitle() + "_";
                  if (obj.getType() == EnumQuestTask.ITEM.ordinal() || obj.getType() == EnumQuestTask.CRAFT.ordinal()) {
                     name += obj.getItem().getDisplayName();
                  }
                  if (obj.getType() == EnumQuestTask.DIALOG.ordinal()) {
                     IDialog d = DialogController.instance.get(obj.getTargetID());
                     if (d != null) {
                        name += d.getName();
                     } else {
                        name += obj.getTargetName();
                     }
                  } else {
                     name += obj.getTargetName();
                  }
                  MiniMapData mmd = playerdata.minimap.getQuestTask(quest.id, taskId, name, obj.getCompassDimension());
                  if (mmd == null) {
                     mmd = (MiniMapData) playerdata.minimap.addPoint(obj.getCompassDimension());
                  }
                  mmd.setName(Util.instance.deleteColor(name));
                  mmd.setPos(obj.getCompassPos());
                  mmd.setQuestId(quest.id);
                  mmd.setTaskId(taskId);
               }
               taskId++;
            }
         });
      }
   }

   public static void setQuestFinished(Quest quest, Player player) {
      PlayerData playerdata = PlayerData.get(player);
      PlayerQuestData data = playerdata.questData;
      playerdata.minimap.removeQuestPoints(quest.id);
      data.finish(quest, player);
      if (quest.repeat != EnumQuestRepeat.NONE) { // Change
         for (QuestObjective obj : quest.questInterface.getObjectives(player)) { // forget dialogues
            if (obj.getEnumType() != EnumQuestTask.DIALOG) {
               continue;
            }
            playerdata.dialogData.dialogsRead.remove(obj.getTargetID());
         }
         for (int dID : quest.forgetDialogues) {
            playerdata.dialogData.dialogsRead.remove(dID);
         }
         for (int qID : quest.forgetQuests) {
            data.removeFinishedQuest(qID);
         }
      }
      playerdata.updateClient = true;
   }

   @SuppressWarnings("all")
   public static Vector<Quest> getActiveQuests(Player player) {
      Vector<Quest> quests = new Vector<>();
      PlayerQuestData data = PlayerData.get(player).questData;
      for (QuestData questdata : data.activeQuests.values()) {
         if (questdata.quest != null) { quests.add(questdata.quest); }
      }
      return quests;
   }

   // New from Unofficial (BetaZavr)
   public static boolean getRemoveActiveQuest(Player player, int id) {
      PlayerData playerdata = PlayerData.get(player);
      PlayerQuestData data = playerdata.questData;
      playerdata.minimap.removeQuestPoints(id);
      if (!data.activeQuests.containsKey(id)) { return false; }
      HashMap<Integer, QuestData> newData = new HashMap<>();
      boolean del = false;
      for (int qid : data.activeQuests.keySet()) {
         if (qid == id) {
            del = true;
            Quest quest = QuestController.instance.quests.get(id);
            for (int dialogId : quest.forgetDialogues) {
               playerdata.dialogData.dialogsRead.remove(dialogId);
            }
            for (int questId : quest.forgetQuests) {
               playerdata.questData.removeFinishedQuest(questId);
            }
            continue;
         }
         newData.put(qid, data.activeQuests.get(qid));
      }
      if (del) {
         playerdata.questData.activeQuests.clear();
         playerdata.questData.activeQuests.putAll(newData);
         playerdata.updateClient = true;
      }
      return del;
   }

}
