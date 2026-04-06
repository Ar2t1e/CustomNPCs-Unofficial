package noppes.npcs.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.shared.common.util.LogWriter;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConfigElement {

    public final ConfigProp prop;
    public final Object parent;
    public final Class<?> class2;
    public final String name;
    public Field field;
    public Object firstValue;
    public Object defaultValue;
    public Object value;
    public Object min;
    public Object max;

    public ConfigElement(Object parentIn, Class<?> classIn, String nameIn, Object valueIn, int pos, ConfigProp propIn) {
        parent = parentIn;
        class2 = classIn;
        name = nameIn;
        prop = propIn;
        if (isColor()) { firstValue = ((Color) valueIn).getRGB(); }
        else { firstValue = valueIn; }
        value = firstValue;
        resetDefaults(pos);
    }

    public ConfigElement(Object parentIn, Field fieldIn, ConfigProp propIn) {
        parent = parentIn;
        field = fieldIn;
        class2 = field.getType();
        name = field.getName();
        prop = propIn;
        try {
            if (isColor()) { firstValue = ((Color) field.get(parent)).getRGB(); }
            else { firstValue = field.get(parent); }
            value = firstValue;
        }
        catch (Exception e) { LogWriter.error(e); }
        resetDefault();
    }

    private void resetDefaults(int pos) {
        try {
            if (!prop.def().isEmpty()) {
                String def = prop.def().replace("[", "").replace("]", "").split(",")[pos];
                String minDef = "";
                String maxDef = "";
                if (!prop.min().isEmpty()) { minDef = prop.min().replace("[", "").replace("]", "").split(",")[pos]; }
                if (!prop.max().isEmpty()) { maxDef = prop.max().replace("[", "").replace("]", "").split(",")[pos]; }
                if (isString()) { defaultValue = def; }
                else if (isBoolean()) { defaultValue = Boolean.parseBoolean(def); }
                else if (isInt()) {
                    defaultValue = Integer.parseInt(def);
                    if (!minDef.isEmpty()) { min = Integer.parseInt(minDef); }
                    if (!maxDef.isEmpty()) { max = Integer.parseInt(maxDef); }
                }
                else if (isLong()) {
                    defaultValue = Long.parseLong(def);
                    if (!minDef.isEmpty()) { min = Long.parseLong(minDef); }
                    if (!maxDef.isEmpty()) { max = Long.parseLong(maxDef); }
                }
                else if (isShort()) {
                    defaultValue = Short.parseShort(def);
                    if (!minDef.isEmpty()) { min = Short.parseShort(minDef); }
                    if (!maxDef.isEmpty()) { max = Short.parseShort(maxDef); }
                }
                else if (isByte()) {
                    defaultValue = Byte.parseByte(def);
                    if (!minDef.isEmpty()) { min = Byte.parseByte(minDef); }
                    if (!maxDef.isEmpty()) { max = Byte.parseByte(maxDef); }
                }
                else if (isFloat()) {
                    defaultValue = Float.parseFloat(def);
                    if (!minDef.isEmpty()) { min = Float.parseFloat(minDef); }
                    if (!maxDef.isEmpty()) { max = Float.parseFloat(maxDef); }
                }
                else if (isDouble()) {
                    defaultValue = Double.parseDouble(def);
                    if (!minDef.isEmpty()) { min = Double.parseDouble(minDef); }
                    if (!maxDef.isEmpty()) { max = Double.parseDouble(maxDef); }
                }
                else if (isColor()) { defaultValue = (int) Long.parseLong(def, 16) & 0x00FFFFFF; }
            }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

    private void resetDefault() {
        try {
            if (!prop.def().isEmpty()) {
                if (isString()) { defaultValue = prop.def(); }
                else if (isBoolean()) { defaultValue = Boolean.parseBoolean(prop.def()); }
                else if (isInt()) {
                    defaultValue = Integer.parseInt(prop.def());
                    if (!prop.min().isEmpty()) { min = Integer.parseInt(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Integer.parseInt(prop.max()); }
                }
                else if (isLong()) {
                    defaultValue = Long.parseLong(prop.def());
                    if (!prop.min().isEmpty()) { min = Long.parseLong(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Long.parseLong(prop.max()); }
                }
                else if (isShort()) {
                    defaultValue = Short.parseShort(prop.def());
                    if (!prop.min().isEmpty()) { min = Short.parseShort(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Short.parseShort(prop.max()); }
                }
                else if (isByte()) {
                    defaultValue = Byte.parseByte(prop.def());
                    if (!prop.min().isEmpty()) { min = Byte.parseByte(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Byte.parseByte(prop.max()); }
                }
                else if (isFloat()) {
                    defaultValue = Float.parseFloat(prop.def());
                    if (!prop.min().isEmpty()) { min = Float.parseFloat(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Float.parseFloat(prop.max()); }
                }
                else if (isDouble()) {
                    defaultValue = Double.parseDouble(prop.def());
                    if (!prop.min().isEmpty()) { min = Double.parseDouble(prop.min()); }
                    if (!prop.max().isEmpty()) { max = Double.parseDouble(prop.max()); }
                }
                else if (isColor()) { defaultValue = (int) Long.parseLong(prop.def(), 16) & 0x00FFFFFF; }
                else if (isArray()) {
                    String[] values = prop.def().replace("[", "").replace("]", "").split(",");
                    if (class2 == Color[].class) {
                        Color[] colors = new Color[values.length];
                        for (int i = 0; i < values.length; i++) { colors[i] = new Color((int) Long.parseLong(values[i], 16) & 0x00FFFFFF); }
                        defaultValue = colors;
                    }
                    else if (class2 == int[].class) {
                        int[] ints = new int[values.length];
                        for (int i = 0; i < values.length; i++) { ints[i] = Integer.parseInt(values[i]); }
                        defaultValue = ints;
                    }
                    else if (class2 == long[].class) {
                        long[] longs = new long[values.length];
                        for (int i = 0; i < values.length; i++) { longs[i] = Long.parseLong(values[i]); }
                        defaultValue = longs;
                    }
                    else if (class2 == boolean[].class) {
                        boolean[] ints = new boolean[values.length];
                        for (int i = 0; i < values.length; i++) { ints[i] = Boolean.parseBoolean(values[i]); }
                        defaultValue = ints;
                    }
                    else if (class2 == short[].class) {
                        short[] shorts = new short[values.length];
                        for (int i = 0; i < values.length; i++) { shorts[i] = Short.parseShort(values[i]); }
                        defaultValue = shorts;
                    }
                    else if (class2 == byte[].class) {
                        byte[] bytes = new byte[values.length];
                        for (int i = 0; i < values.length; i++) { bytes[i] = Byte.parseByte(values[i]); }
                        defaultValue = bytes;
                    }
                    else if (class2 == float[].class) {
                        float[] floats = new float[values.length];
                        for (int i = 0; i < values.length; i++) { floats[i] = Float.parseFloat(values[i]); }
                        defaultValue = floats;
                    }
                    else if (class2 == double[].class) {
                        double[] doubles = new double[values.length];
                        for (int i = 0; i < values.length; i++) { doubles[i] = Double.parseDouble(values[i]); }
                        defaultValue = doubles;
                    }
                    else if (class2 == String[].class) { defaultValue = values; }
                }
            }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

    public String getLabel() { return name; }

    public Component getTextValue() {
        if (isArray()) {
            MutableComponent textValue = Component.empty().append(Component.literal("["));
            if (value instanceof Color[] colors) {
                for (int i = 0; i < colors.length; i++) {
                    StringBuilder v = new StringBuilder(Integer.toHexString((colors[i].getRGB()) & 0x00FFFFFF).toUpperCase());
                    while (v.length() < 6) { v.insert(0, "0"); }
                    MutableComponent key = Component.literal(v.toString());
                    key.setStyle(key.getStyle().withColor(colors[i].getRGB() & 0x00FFFFFF));
                    textValue.append(key);
                    if (i < colors.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof int[] ints) {
                for (int i = 0; i < ints.length; i++) {
                    textValue.append("" + ints[i]);
                    if (i < ints.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof long[] longs) {
                for (int i = 0; i < longs.length; i++) {
                    textValue.append("" + longs[i]);
                    if (i < longs.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof boolean[] booleans) {
                for (int i = 0; i < booleans.length; i++) {
                    textValue.append("" + booleans[i]);
                    if (i < booleans.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof short[] shorts) {
                for (int i = 0; i < shorts.length; i++) {
                    textValue.append("" + shorts[i]);
                    if (i < shorts.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof byte[] bytes) {
                for (int i = 0; i < bytes.length; i++) {
                    textValue.append("" + bytes[i]);
                    if (i < bytes.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof float[] floats) {
                for (int i = 0; i < floats.length; i++) {
                    textValue.append(String.valueOf(floats[i]).replace(",", "."));
                    if (i < floats.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof double[] doubles) {
                for (int i = 0; i < doubles.length; i++) {
                    textValue.append(String.valueOf(doubles[i]).replace(",", "."));
                    if (i < doubles.length - 1) { textValue.append(","); }
                }
            }
            else if (value instanceof String[] strings) {
                for (int i = 0; i < strings.length; i++) {
                    textValue.append(strings[i]);
                    if (i < strings.length - 1) { textValue.append(","); }
                }
            }
            return textValue.append("]");
        }
        if (isColor()) {
            StringBuilder v = new StringBuilder(Integer.toHexString((int) value & 0x00FFFFFF).toUpperCase());
            while (v.length() < 6) { v.insert(0, "0"); }
            return Component.literal(v.toString());
        }
        if (isFloat() || isDouble()) { return Component.literal(value.toString().replace(",", ".")); }
        return Component.literal(value.toString());
    }

    public boolean isColor() { return class2.isAssignableFrom(Color.class); }

    public boolean isBoolean() { return class2.isAssignableFrom(Boolean.TYPE); }

    public boolean isInt() { return class2.isAssignableFrom(Integer.TYPE); }

    public boolean isLong() { return class2.isAssignableFrom(Long.TYPE); }

    public boolean isShort() { return class2.isAssignableFrom(Short.TYPE); }

    public boolean isByte() { return class2.isAssignableFrom(Byte.TYPE); }

    public boolean isFloat() { return class2.isAssignableFrom(Float.TYPE); }

    public boolean isDouble() { return class2.isAssignableFrom(Double.TYPE); }

    private boolean isString() { return class2.isAssignableFrom(String.class); }

    public boolean isArray() { return class2.isArray(); }

    public int getInt() { return (int) value; }

    public boolean isChanged() {
        if (isArray()) {
            if (class2 == Color[].class) {
                Color[] values = (Color[]) value;
                Color[] changes = (Color[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].getRGB() != changes[i].getRGB()) { return true; }
                }
            }
            else if (class2 == int[].class) {
                int[] values = (int[]) value;
                int[] changes = (int[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == long[].class) {
                long[] values = (long[]) value;
                long[] changes = (long[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == boolean[].class) {
                boolean[] values = (boolean[]) value;
                boolean[] changes = (boolean[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == short[].class) {
                short[] values = (short[]) value;
                short[] changes = (short[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == byte[].class) {
                byte[] values = (byte[]) value;
                byte[] changes = (byte[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == float[].class) {
                float[] values = (float[]) value;
                float[] changes = (float[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == double[].class) {
                double[] values = (double[]) value;
                double[] changes = (double[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != changes[i]) { return true; }
                }
            }
            else if (class2 == String[].class) {
                String[] values = (String[]) value;
                String[] changes = (String[]) firstValue;
                if (values.length != changes.length) { return true; }
                for (int i = 0; i < values.length; i++) {
                    if (!values[i].equals(changes[i])) { return true; }
                }
            }
            return false;
        }
        return firstValue == null || !firstValue.equals(value);
    }

    public boolean isDefault() {
        if (isArray()) {
            if (class2 == Color[].class) {
                Color[] values = (Color[]) value;
                Color[] defaults = (Color[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].getRGB() != defaults[i].getRGB()) { return false; }
                }
            }
            else if (class2 == int[].class) {
                int[] values = (int[]) value;
                int[] defaults = (int[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == long[].class) {
                long[] values = (long[]) value;
                long[] defaults = (long[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == boolean[].class) {
                boolean[] values = (boolean[]) value;
                boolean[] defaults = (boolean[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == short[].class) {
                short[] values = (short[]) value;
                short[] defaults = (short[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == byte[].class) {
                byte[] values = (byte[]) value;
                byte[] defaults = (byte[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == float[].class) {
                float[] values = (float[]) value;
                float[] defaults = (float[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == double[].class) {
                double[] values = (double[]) value;
                double[] defaults = (double[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != defaults[i]) { return false; }
                }
            }
            else if (class2 == String[].class) {
                String[] values = (String[]) value;
                String[] defaults = (String[]) defaultValue;
                if (values.length != defaults.length) { return false; }
                for (int i = 0; i < values.length; i++) {
                    if (!values[i].equals(defaults[i])) { return false; }
                }
            }
            return true;
        }
        return defaultValue == null || defaultValue.equals(value);
    }

    public List<ConfigElement> getArrayElements() {
        List<ConfigElement> list = new ArrayList<>();
        if (isArray()) {
            if (class2 == Color[].class) {
                Color[] values = (Color[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, Color.class, name, values[i], i, prop)); }
            }
            else if (class2 == int[].class) {
                int[] values = (int[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, int.class, name, values[i], i, prop)); }
            }
            else if (class2 == long[].class) {
                long[] values = (long[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, long.class, name, values[i], i, prop)); }
            }
            else if (class2 == boolean[].class) {
                boolean[] values = (boolean[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, boolean.class, name, values[i], i, prop)); }
            }
            else if (class2 == short[].class) {
                short[] values = (short[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, short.class, name, values[i], i, prop)); }
            }
            else if (class2 == byte[].class) {
                byte[] values = (byte[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, byte.class, name, values[i], i, prop)); }
            }
            else if (class2 == float[].class) {
                float[] values = (float[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, float.class, name, values[i], i, prop)); }
            }
            else if (class2 == double[].class) {
                double[] values = (double[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, double.class, name, values[i], i, prop)); }
            }
            else if (class2 == String[].class) {
                String[] values = (String[]) value;
                for (int i = 0; i < values.length; i++) { list.add(new ConfigElement(parent, String.class, name, values[i], i, prop)); }
            }
        }
        return list;
    }

    public void setValue(Object v) {
        if (v == null) { return; }
        if (isArray() || v.getClass().isArray()) { return; }
        try {
            value = v;
            if (field != null) {
                if (isColor()) { field.set(parent, new Color(0xFF000000 | (int) v & 0x00FFFFFF)); }
                else { field.set(parent, v); }
            }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

    public void setArrayValue(List<ConfigElement> list) {
        if (class2 == Color[].class) {
            Color[] values = (Color[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) {
                if (list.get(i).value.getClass() == Integer.class) { values[i] = new Color((int) list.get(i).value); }
                else if (Color.class.isAssignableFrom(list.get(i).value.getClass())) { values[i] = (Color) list.get(i).value; }
            }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == int[].class) {
            int[] values = (int[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (int) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == long[].class) {
            long[] values = (long[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (long) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == boolean[].class) {
            boolean[] values = (boolean[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (boolean) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == short[].class) {
            short[] values = (short[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (short) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == byte[].class) {
            byte[] values = (byte[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (byte) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == float[].class) {
            float[] values = (float[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (float) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == double[].class) {
            double[] values = (double[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (double) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
        else if (class2 == String[].class) {
            String[] values = (String[]) value;
            for (int i = 0; i < list.size() && i < values.length; i++) { values[i] = (String) list.get(i).value; }
            try { field.set(parent, values); } catch (Exception e) { LogWriter.error(e); }
        }
    }

}
