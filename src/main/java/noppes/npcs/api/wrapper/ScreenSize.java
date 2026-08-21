package noppes.npcs.api.wrapper;

import noppes.npcs.api.IScreenSize;

public class ScreenSize implements IScreenSize {

    private double width;
    private double height;

    public ScreenSize(double widthIn, double heightIn) {
        width = widthIn;
        height = heightIn;
    }

    public void setSize(double widthIn, double heightIn) {
        width = widthIn;
        height = heightIn;
    }

    @Override
    public double getWidth() { return width; }

    @Override
    public double getHeight() { return height; }

    @Override
    public int getWidthPercent(double percent) { return (int)(width * percent / 100.0D); }

    @Override
    public int getHeightPercent(double percent) { return (int)(height * percent / 100.0D); }

}
