package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("unused")
public interface IScreenSize {

    double getWidth();

    double getHeight();

    int getWidthPercent(@ParamName("percent") double percent);

    int getHeightPercent(@ParamName("percent") double percent);

}
