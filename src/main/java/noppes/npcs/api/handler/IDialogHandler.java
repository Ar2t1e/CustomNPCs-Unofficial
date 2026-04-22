package noppes.npcs.api.handler;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;

import java.util.List;

public interface IDialogHandler {

	List<IDialogCategory> categories();

	IDialog get(@ParamName("id") int id);

}
