package noppes.npcs.shared.client.gui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextLineData {

    private static final char colorChar = '\uffff';
    public final String text;
    public int start;
    public int end;
    private final Map<List<TextMarkUp>, String> data = new HashMap<>();

    public TextLineData(String textIn, int startIn, int endIn) {
        text = textIn;
        start = startIn;
        end = endIn;
    }

    public String getFormattedString(List<TextMarkUp> makeup) {
        if (data.containsKey(makeup)) { return data.get(makeup); }
        StringBuilder builder = new StringBuilder(text);
        int found = 0;
        for (TextMarkUp entry : makeup) {
            if (entry.start >= start && entry.start < end) {
                builder.insert(entry.start - start + found * 2, Character.toString(colorChar) + entry.c);
                ++found;
            }
            if (entry.start < start && entry.end > start) {
                builder.insert(0, Character.toString(colorChar) + entry.c);
                ++found;
            }
            if (entry.end >= start && entry.end < end) {
                builder.insert(entry.end - start + found * 2, Character.toString(colorChar) + 'r');
                ++found;
            }
        }
        data.put(makeup, builder.toString());
        return data.get(makeup);
    }

}
