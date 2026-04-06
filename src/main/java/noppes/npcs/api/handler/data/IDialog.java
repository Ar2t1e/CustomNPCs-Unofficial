package noppes.npcs.api.handler.data;

import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

public interface IDialog {

   int getId();

   String getName();

   void setName(@ParamName("name") String name);

   String getText();

   void setText(@ParamName("text") String text);

   IQuest getQuest();

   void setQuest(@ParamName("quest") IQuest quest);

   String getCommand();

   void setCommand(@ParamName("command") String command);

   List<IDialogOption> getOptions();

   IDialogOption getOption(@ParamName("slot") int slot);

   IAvailability getAvailability();

   IDialogCategory getCategory();

   void save();

}
