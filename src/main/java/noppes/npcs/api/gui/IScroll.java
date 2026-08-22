package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiComponentClicked;

@SuppressWarnings("unused")
public interface IScroll extends ICustomGuiComponent {

	String[] getList();

	IScroll setList(@ParamName("list") String[] list);

	@Deprecated
	int getDefaultSelection();

	@Deprecated
	IScroll setDefaultSelection(@ParamName("selection") int selection);

	int[] getSelection();

	IScroll setSelection(@ParamName("selection") int... selection);

	String[] getSelectionList();

	IScroll setSelectionList(@ParamName("list") String... list);

	boolean isMultiSelect();

	IScroll setMultiSelect(@ParamName("multiSelect") boolean multiSelect);

	IScroll setOnClick(@ParamName("onClick") GuiComponentClicked<IScroll> onClick);

	IScroll setOnDoubleClick(@ParamName("onDoubleClick") GuiComponentClicked<IScroll> onDoubleClick);

	boolean getHasSearch();

	IScroll setHasSearch(@ParamName("bo") boolean bo);

}
