package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.data.IDropNbtSet;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

public class DropNbtSet implements IDropNbtSet {

    public double chance = 100.0d;
    private final DropSet parent;
    public String path = "";
    public int type = 0;
    public int typeList = 0;
    String[] values = new String[0];

    public DropNbtSet(DropSet ds) { parent = ds; }

    public String checkValue(String value, int t) {
        switch (t) {
            case 0: { // boolean
                try {
                    boolean b = Boolean.parseBoolean(value);
                    return String.valueOf(b);
                }
                catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 1: { // byte
                try {
                    byte b = Byte.parseByte(value);
                    return String.valueOf(b);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 2: { // short
                try {
                    short s = Short.parseShort(value);
                    return String.valueOf(s);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 3: { // integer
                try {
                    int b = Integer.parseInt(value);
                    return String.valueOf(b);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 4: { // long
                try {
                    long l = Long.parseLong(value);
                    return String.valueOf(l);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 5: { // float
                try {
                    float f = Float.parseFloat(value);
                    return String.valueOf(f);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 6: { // double
                try {
                    double d = Double.parseDouble(value);
                    return String.valueOf(d);
                } catch (Exception e) { LogWriter.error(e); }
                break;
            }
            case 7: { // byte array
                String[] br = value.split(",");
                StringBuilder text = new StringBuilder();
                for (String str : br) {
                    try {
                        byte b = Byte.parseByte(str);
                        if (!text.isEmpty()) { text.append(","); }
                        text.append(String.valueOf(b));
                    } catch (Exception e) { LogWriter.error(e); }
                }
                if (!text.isEmpty()) { return text.toString(); }
                break;
            }
            case 8: { return value; } // string
            case 9: {
                String[] br = value.split(",");
                StringBuilder text = new StringBuilder();
                for (String str : br) {
                    try {
                        String sc = checkValue(str, typeList);
                        if (sc != null) {
                            if (!text.isEmpty()) { text.append(","); }
                            text.append(sc);
                        }
                    } catch (Exception e) { LogWriter.error(e); }
                }
                if (!text.isEmpty()) { return text.toString(); }
                break;
            } // list
            case 11: {
                String[] br = value.split(",");
                StringBuilder text = new StringBuilder();
                for (String str : br) {
                    try {
                        int i = Integer.parseInt(str);
                        if (!text.isEmpty()) {
                            if (t == type) { text.append(","); }
                            else { text.append(";"); }
                        }
                        text.append(i);
                    } catch (Exception e) { LogWriter.error(e); }
                }
                if (!text.isEmpty()) {
                    return text.toString();
                }
                break;
            } // integer array
        }
        return null;
    }

    @Override
    public double getChance() {
        return Math.round(chance * 10000.0d) / 10000.0d;
    }

    @Override
    public INbt getConstructorTag(INbt nbt) {
        CompoundTag pos = nbt.getMCNBT();
        String key = path;
        if (path.contains(".")) {
            String keyName;
            while (key.contains(".")) {
                keyName = key.substring(0, key.indexOf("."));
                if (!pos.contains(keyName, 10)) { pos.put(keyName, new CompoundTag()); }
                pos = pos.getCompound(keyName);
                key = key.substring(key.indexOf(".") + 1);
            }
        }
        int idx = (int) ((double) values.length * Math.random());
        if (idx >= values.length) { idx = values.length - 1; }
        String value = values[idx];
        switch (type) {
            case 0: { // boolean
                pos.putBoolean(key, Boolean.parseBoolean(value));
                break;
            }
            case 1: { // byte
                pos.putByte(key, Byte.parseByte(value));
                break;
            }
            case 2: { // short
                pos.putShort(key, Short.parseShort(value));
                break;
            }
            case 3: { // integer
                pos.putInt(key, Integer.parseInt(value));
                break;
            }
            case 4: { // long
                pos.putLong(key, Long.parseLong(value));
                break;
            }
            case 5: { // float
                pos.putFloat(key, Float.parseFloat(value));
                break;
            }
            case 6: { // double
                pos.putDouble(key, Double.parseDouble(value));
                break;
            }
            case 7: { // byte array
                String[] brs = value.split(",");
                byte[] br = new byte[brs.length];
                for (int i = 0; i < brs.length; i++) {
                    br[i] = Byte.parseByte(brs[i]);
                }
                pos.putByteArray(key, br);
                break;
            }
            case 8: { // string
                pos.putString(key, value);
                break;
            }
            case 9: { // list
                String[] brs = value.split(",");
                ListTag list = new ListTag();
                for (String br : brs) {
                    if (typeList == 3) {
                        list.add(IntTag.valueOf(Integer.parseInt(br)));
                    } else if (typeList == 5) {
                        list.add(FloatTag.valueOf(Float.parseFloat(br)));
                    } else if (typeList == 6) {
                        list.add(DoubleTag.valueOf(Double.parseDouble(br)));
                    } else if (typeList == 8) {
                        list.add(StringTag.valueOf(br));
                    } else if (typeList == 11) {
                        String[] ints = br.split(";");
                        int[] is = new int[ints.length];
                        for (int j = 0; j < ints.length; j++) { is[j] = Integer.parseInt(ints[j]); }
                        list.add(new IntArrayTag(is));
                    }
                }
                pos.put(key, list);
                break;
            }
            case 11: { // integer array
                String[] ints = value.split(",");
                int[] is = new int[ints.length];
                for (int i = 0; i < ints.length; i++) { is[i] = Integer.parseInt(ints[i]); }
                pos.putIntArray(key, is);
                break;
            }
        }
        return new NBTWrapper(pos);
    }

    public Component getKey() {
        MutableComponent keyName = Component.empty();
        double ch = Math.round(chance * 10.0d) / 10.d;
        String chance = String.valueOf(ch).replace(".", ",");
        if (ch == (int) ch) { chance = String.valueOf((int) ch); }
        chance += "%";
        keyName.append(Component.literal(chance).withStyle(ChatFormatting.YELLOW));
        keyName.append(getPathToKey().withStyle(ChatFormatting.RESET));
        if (values.length == 0) {
            keyName.append(Component.literal("=").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("NULL").withStyle(ChatFormatting.RED))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY));
        } else if (values.length == 1) {
            keyName.append(Component.literal("=").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(values[0]).withStyle(ChatFormatting.RED))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY));
        } else {
            keyName.append(Component.literal("=").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + values.length).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("|").withStyle(ChatFormatting.GRAY));
        }
        return keyName;
        //return keyName.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(ChatFormatting.DARK_GRAY));
    }

    private MutableComponent getPathToKey() {
        String key = path;
        if (path.contains(".")) {
            List<String> keys = new ArrayList<>();
            String preKey = "";
            while (key.contains(".")) {
                preKey = key.substring(0, key.indexOf("."));
                keys.add(preKey);
                key = key.substring(key.indexOf(".") + 1);
            }
            String lastKey = key;
            keys.add(key);
            key = preKey + "." + lastKey;
            if (keys.size() > 2) {
                key = "..." + key;
            }
        }
        return Component.literal(key);
    }

    public CompoundTag getNBT() {
        CompoundTag nbtDS = new CompoundTag();
        nbtDS.putInt("Type", type);
        nbtDS.putInt("TypeList", typeList);
        nbtDS.putString("Path", path);
        nbtDS.putDouble("Chance", chance);
        ListTag vs = new ListTag();
        for (String s : values) {
            if (s != null) { vs.add(StringTag.valueOf(s)); }
        }
        nbtDS.put("Values", vs);
        return nbtDS;
    }

    @Override
    public String getPath() { return path; }

    @Override
    public int getType() { return type; }

    @Override
    public int getTypeList() { return typeList; }

    @Override
    public String[] getValues() { return values; }

    public void load(CompoundTag nbtDS) {
        type = nbtDS.getInt("Type");
        typeList = nbtDS.getInt("TypeList");
        path = nbtDS.getString("Path");
        chance = nbtDS.getDouble("Chance");
        String[] vs = new String[nbtDS.getList("Values", 8).size()];
        for (int i = 0; i < nbtDS.getList("Values", 8).size(); i++) {
            String ch = checkValue(nbtDS.getList("Values", 8).getString(i), type);
            if (ch != null) { vs[i] = ch; }
        }
        values = vs;
    }

    @Override
    public void remove() { parent.removeDropNbt(this); }

    @Override
    public void setChance(double chanceIn) { chance = Math.round(ValueUtil.correctDouble(chanceIn, 0.0001d, 100.0d) * 10000.0d) / 10000.0d; }

    @Override
    public void setPath(String pathIn) {
        if (pathIn == null) { pathIn = ""; }
        path = pathIn;
    }

    @Override
    public void setType(int typeIn) {
        if ((typeIn >= 0 && typeIn <= 9) || typeIn == 11) { type = typeIn; }
    }

    @Override
    public void setTypeList(int typeIn) {
        if (typeIn == 3 || typeIn == 5 || typeIn == 6 || typeIn == 8 || typeIn == 11) { typeList = typeIn; }
    }

    @Override
    public void setValues(String valuesIn) {
        if (valuesIn.contains("|")) {
            List<String> nal = new ArrayList<>();
            while (valuesIn.contains("|")) {
                String key = checkValue(valuesIn.substring(0, valuesIn.indexOf("|")), type);
                if (key != null) { nal.add(key); }
                valuesIn = valuesIn.substring(valuesIn.indexOf("|") + 1);
            }
            nal.add(valuesIn);
            String[] svs = new String[nal.size()];
            for (int i = 0; i < nal.size(); i++) { svs[i] = nal.get(i); }
            values = svs;
        }
        else {
            String ch = checkValue(valuesIn, type);
            if (ch != null) { values = new String[] { ch }; }
        }
    }

    @Override
    public void setValues(String[] valuesIn) {
        List<String> nal = new ArrayList<>();
        for (String str : valuesIn) {
            String key = checkValue(str, type);
            if (key != null) { nal.add(key); }
        }
        String[] svs = new String[nal.size()];
        for (int i = 0; i < nal.size(); i++) { svs[i] = nal.get(i); }
        values = svs;
    }

}
