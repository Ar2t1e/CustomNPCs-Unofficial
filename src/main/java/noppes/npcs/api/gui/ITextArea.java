package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("unused")
public interface ITextArea extends ITextField {

    void setCodeTheme(@ParamName("bo") boolean bo);

    boolean getCodeTheme();

}
