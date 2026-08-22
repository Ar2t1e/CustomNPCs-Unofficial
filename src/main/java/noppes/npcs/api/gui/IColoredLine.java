package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("unused")
public interface IColoredLine extends ICustomGuiComponent {

    int getColor();

    IColoredLine setColor(@ParamName("color") int color);

    int getXEnd();

    int getYEnd();

    IColoredLine setEnd(@ParamName("x") int x, @ParamName("y") int y);

    float getThickness();

    IColoredLine setThickness(@ParamName("thickness") float thickness);

}
