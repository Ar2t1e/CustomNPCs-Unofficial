package noppes.npcs.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;

public class TempFile {

    private static final int maxPart = 30000;

    public String name = "";
    public final Map<Integer, String> data = new TreeMap<>();
    public int fileType = 0; // 0 - simple text, 1 - nbt json, 2 - compressed nbt
    public int saveType = 0; // 0 - temp file, 1 - client script, 2 - normal save
    public int tryLoads = 0;
    public long size = 0;
    public long lastLoad = System.currentTimeMillis();

    public TempFile() { }

    public TempFile(String nameIn, int filetype, int savetype, long sizeIn) {
        name = nameIn;
        fileType = filetype;
        saveType = savetype;
        size = sizeIn;
        tryLoads = 0;
        lastLoad = System.currentTimeMillis();
    }

    public CompoundTag getDataNbt() {
        if (fileType == 0) { return null; }
        try {
            return NBTJsonUtil.Convert(getDataText());
        } catch (Exception e) { LogWriter.error("Error:", e); }
        return null;
    }

    public String getDataText() {
        StringBuilder text = new StringBuilder();
        for (String str : data.values()) {
            text.append(str);
        }
        return text.toString();
    }

    public int getNextPart() {
        if (data.isEmpty()) {
            return -1;
        }
        int i = 0;
        for (String str : data.values()) {
            if (str == null || str.isEmpty()) {
                break;
            }
            i++;
        }
        return i;
    }

    public CompoundTag getTitle() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", name);
        nbt.putInt("filetype", fileType);
        nbt.putInt("savetype", saveType);
        nbt.putInt("parts", data.size());
        nbt.putLong("size", size);
        return nbt;
    }

    public boolean isLoad() {
        if (data.isEmpty()) {
            return false;
        }
        for (String str : data.values()) {
            if (str == null || str.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void reset(File file) throws IOException {
        if (file == null || !file.exists()) {
            data.clear();
            size = -1;
            return;
        }
        saveType = 0;
        size = file.length();
        try {
            CompoundTag nbt = NbtIo.read(file);
            fileType = 2;
            reset(NBTJsonUtil.Convert(nbt));
            return;
        } catch (IOException e) { LogWriter.error("Error:", e); }

        String text = Util.instance.loadFile(file);

        try {
            NBTJsonUtil.Convert(text);
            fileType = 1;
        } catch (Exception e) { LogWriter.error("Error:", e); }
        reset(text);
    }

    public void reset(String text) {
        if (text == null || text.isEmpty()) {
            data.clear();
            size = -1;
            return;
        }
        if (size < 0) {
            size = text.getBytes().length;
        }
        int part = 0;
        while (!text.isEmpty()) {
            int end = Math.min(text.length(), TempFile.maxPart);
            data.put(part, text.substring(0, end));
            if (end == text.length()) {
                break;
            }
            text = text.substring(end);
            part++;
        }
    }

    public void save() {
        if (!isLoad()) { return; }
        File file = new File(name);
        if (saveType == 0) {
            File dir = new File(CustomNpcs.Dir, "temp files");
            if (dir.exists() || dir.mkdir()) {
                file = new File(dir, file.getName());
            }
        } else if (saveType == 1) {
            File dir = new File(CustomNpcs.Dir, "client scripts/ecmascript");
            if (dir.exists() || dir.mkdir()) {
                file = new File(dir, file.getName());
            }
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) { return; }
            } catch (IOException ignored) { return; }
        }
        if (file.exists()) {
            saveTo(file);
            return;
        }
        LogWriter.error("Unable to create file: " + file.getAbsolutePath() + ". Path is incorrect!");
    }

    private void saveTo(File file) {
        switch (fileType) {
            case 1: {
                try {
                    Util.instance.saveFile(file, NBTJsonUtil.Convert(getDataNbt()));
                    LogWriter.debug("Save nbt json to file: " + file.getAbsolutePath());
                } catch (Exception e) {
                    LogWriter.error("Error save nbt json to file: " + file.getAbsolutePath(), e);
                }
                break;
            }
            case 2: {
                try {
                    NbtIo.write(getDataNbt(), file);
                    LogWriter.debug("Save nbt compressed to file: " + file.getAbsolutePath());
                } catch (IOException e) {
                    LogWriter.error("Error save nbt compressed to file: " + file.getAbsolutePath(), e);
                }
                break;
            }
            default: {
                if (Util.instance.saveFile(file, getDataText())) { LogWriter.debug("Save text to file: " + file.getAbsolutePath()); }
                break;
            }
        }
    }

    public void setTitle(CompoundTag nbt) {
        name = nbt.getString("name");
        fileType = nbt.getInt("filetype");
        saveType = nbt.getInt("savetype");
        size = nbt.getLong("size");
        data.clear();
        lastLoad = 0L;
        for (int i = 0; i < nbt.getInt("parts"); i++) { data.put(i, ""); }
    }

}
