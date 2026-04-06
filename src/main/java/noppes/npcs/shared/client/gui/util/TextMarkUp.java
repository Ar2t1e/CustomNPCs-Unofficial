package noppes.npcs.shared.client.gui.util;

public class TextMarkUp {

    public int start;
    public int end;
    public int level;
    public char c;

    public TextMarkUp(int startIn, int endIn, char cIn, int levelIn) {
        start = startIn;
        end = endIn;
        c = cIn;
        level = levelIn;
    }

}