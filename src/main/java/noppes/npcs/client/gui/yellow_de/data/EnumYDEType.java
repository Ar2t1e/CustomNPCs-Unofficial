package noppes.npcs.client.gui.yellow_de.data;

import java.awt.*;

public enum EnumYDEType {
    CATEGORY(0), DIALOG(1), NPC(2), OPTION(3), QUEST(4), AREA(5);

    public final int color;
    public final int hoverColor;
    public final int disableColor;

    EnumYDEType(int type) {
        switch (type) {
            case 1: {
                color = new Color(0x2E6399).getRGB();
                hoverColor = new Color(0x637E99).getRGB();
                disableColor = new Color(0x3A4A59).getRGB();
                break;
            } // DIALOG
            case 2: {
                color = new Color(0x52992E).getRGB();
                hoverColor = new Color(0x759963).getRGB();
                disableColor = new Color(0x44593A).getRGB();
                break;
            } // NPC
            case 3: {
                color = new Color(0xB34712).getRGB();
                hoverColor = new Color(0xB37150).getRGB();
                disableColor = new Color(0x734934).getRGB();
                break;
            } // OPTION
            case 4: {
                color = new Color(0x99872E).getRGB();
                hoverColor = new Color(0x999063).getRGB();
                disableColor = new Color(0x59543A).getRGB();
                break;
            } // QUEST
            default: {
                color = new Color(0x522E99).getRGB();
                hoverColor = new Color(0x756399).getRGB();
                disableColor = new Color(0x443A59).getRGB();
                break;
            } // AREA or CATEGORY
        }
    }

}
