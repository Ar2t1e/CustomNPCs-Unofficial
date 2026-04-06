package noppes.npcs.util;

public class JsonFile {
    private final String original;
    private String text;

    public JsonFile(String textIn) {
        text = textIn;
        original = textIn;
    }

    public int keyIndex() {
        boolean hasQuote = false;

        for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (i == 0 && c == '"') {
                hasQuote = true;
            } else if (hasQuote && c == '"') {
                hasQuote = false;
            }

            if (!hasQuote && c == ':') {
                return i;
            }
        }

        return -1;
    }

    public String cutDirty(int i) {
        String s = text.substring(0, i);
        text = text.substring(i);
        return s;
    }

    public String cut(int i) {
        String s = text.substring(0, i);
        text = text.substring(i).trim();
        return s;
    }

    public String substring(int beginIndex, int endIndex) {
        return text.substring(beginIndex, endIndex);
    }

    public int indexOf(String s) {
        return text.indexOf(s);
    }

    public String getCurrentPos() {
        int lengthOr = original.length();
        int lengthCur = text.length();
        int currentPos = lengthOr - lengthCur;
        String done = original.substring(0, currentPos);
        String[] lines = done.split("\r\n|\r|\n");
        int pos = 0;
        String line = "";
        if (lines.length > 0) {
            pos = lines[lines.length - 1].length();
            line = original.split("\r\n|\r|\n")[lines.length - 1].trim();
        }

        return "Line: " + lines.length + ", Pos: " + pos + ", Text: " + line;
    }

    public boolean startsWith(String... ss) {
        for (String s : ss) {
            if (text.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean endsWith(String s) {
        return text.endsWith(s);
    }

}