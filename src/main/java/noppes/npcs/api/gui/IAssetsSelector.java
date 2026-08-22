package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiComponentClicked;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;

@SuppressWarnings("unused")
public interface IAssetsSelector extends ICustomGuiComponent {

    String getSelected();

    IAssetsSelector setSelected(@ParamName("selected") String selected);

    String getRoot();

    IAssetsSelector setRoot(@ParamName("root") String root);

    String getFileType();

    IAssetsSelector setFileType(@ParamName("type") String type);

    IAssetsSelector setOnChange(@ParamName("onChange") GuiComponentUpdate<IAssetsSelector> onChange);

    IAssetsSelector setOnPress(@ParamName("onPress") GuiComponentClicked<IAssetsSelector> onPress);

}
