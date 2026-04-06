package noppes.npcs.api.handler.data;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.entity.data.ICustomDrop;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

import java.util.List;

public interface IQuest {

   int getId();

   String getName();

   void setName(@ParamName("name") String name);

   List<String> getLogText();

   void setLogText(@ParamName("text") String text);

   String getCompleteText();

   void setCompleteText(@ParamName("text") String text);

   IQuest getNextQuest();

   void setNextQuest(@ParamName("quest") IQuest quest);

   IQuestObjective[] getObjectives(@ParamName("player") IPlayer<?> player);

   IQuestCategory getCategory();

   List<ICustomDrop> getRewards();

   void save();

   boolean getIsRepeatable();

   // New from Unofficial (BetaZavr)
   IQuestObjective addTask();

   ICustomNpc<?> getCompleterNpc();

   int getExtraButton();

   String getExtraButtonText();

   int[] getForgetDialogues();

   int[] getForgetQuests();

   int getLevel();

   int getRewardType();

   Component getTitle();

   boolean isCancelable();

   boolean isSetUp();

   boolean removeTask(@ParamName("task") IQuestObjective task);

   void sendChangeToAll();

   void setCancelable(@ParamName("cancelable") boolean cancelable);

   void setCompleterNpc(@ParamName("npc") ICustomNpc<?> npc);

   void setExtraButton(@ParamName("type") int type);

   void setExtraButtonText(@ParamName("hover") String hover);

   void setForgetDialogues(@ParamName("forget") int[] forget);

   void setForgetQuests(@ParamName("forget") int[] forget);

   void setLevel(@ParamName("level") int level);

   void setRewardText(@ParamName("text") String text);

   void setRewardType(@ParamName("type") int type);

   // Removed
   //int getType();
   //void setType(int questType);
   //String getNpcName();
   //void setNpcName(String name);

}
