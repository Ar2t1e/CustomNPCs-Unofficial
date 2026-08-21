package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

public interface IComponentStyle {

    int getColor();

    boolean isUnderlined();

    boolean isStrikethrough();

    boolean isObfuscated();

    void setColor(@ParamName("color") int color);

    String getInsertion();

    boolean isBold();

    boolean isItalic();

    void setBold(@ParamName("isBold") boolean isBold);

    void setUnderlined(@ParamName("isUnderlined") boolean isUnderlined);

    void setStrikethrough(@ParamName("isStrikethrough") boolean isStrikethrough);

    void setItalic(@ParamName("isItalic") boolean isItalic);

    void setObfuscated(@ParamName("isObfuscated") boolean isObfuscated);

    void setInsertion(@ParamName("insertion") String insertion);

}
