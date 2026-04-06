package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.INPCRole;

public interface IRoleDialog extends INPCRole {

   String getDialog();

   void setDialog(@ParamName("text") String text);

   String getOption(@ParamName("option") int option);

   void setOption(@ParamName("option") int option, @ParamName("text") String text);

   String getOptionDialog(@ParamName("option") int option);

   void setOptionDialog(@ParamName("option") int option, @ParamName("text") String text);

}
