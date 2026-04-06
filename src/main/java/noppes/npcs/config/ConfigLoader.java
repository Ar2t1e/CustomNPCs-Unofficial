package noppes.npcs.config;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ForgeEventHandler;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.shared.common.util.LogWriter;

public class ConfigLoader {

   private static final LinkedList<Field> configFields = new LinkedList<>();
   private final Object parent;
   private boolean updateFile = false;
   private File configFile;

   public ConfigLoader(Object parentIn, String fileName, File directory) {
      parent = parentIn;
      if (!directory.exists() && !directory.mkdir()) { return; }
      configFile = new File(directory, fileName + ".cfg");
      for(Field field : parentIn.getClass().getDeclaredFields()) {
         if (field.isAnnotationPresent(ConfigProp.class)) { configFields.add(field); }
      }
      loadConfig();
   }

    public static void sendTo(ServerPlayer player) {
       CompoundTag compound = new CompoundTag();
       for (Field field : configFields) {
          String key = field.getName();
          try {
             Object value = field.get(CustomNpcs.instance);
             if (value instanceof int[]) { compound.putIntArray(key, (int[]) value); }
             else if (value instanceof Color[]) {
                int[] colors = new int[((Color[]) value).length];
                for (int i = 0; i < colors.length; i++) { colors[i] = ((Color[]) value)[i].getRGB(); }
                compound.putIntArray(key, colors);
             }
             else if (value instanceof Boolean) { compound.putBoolean(key, (boolean) value); }
             else if (value instanceof Integer) { compound.putInt(key, (int) value); }
             else if (value instanceof Color) { compound.putInt(key, ((Color) value).getRGB()); }
             else if (value instanceof String) { compound.putString(key, (String) value); }
             else { LogWriter.warn("Custom object "+key+" = "+value.getClass()); }
          }
          catch (Exception ignored) { }
       }
       if (compound.isEmpty()) { return; }
       ListTag list = new ListTag();
       for (Class<?> cls : ForgeEventHandler.eventNames.keySet()) {
          CompoundTag nbt = new CompoundTag();
          nbt.putString("Name", ForgeEventHandler.eventNames.get(cls));
          nbt.putString("Class", cls.getName());
          list.add(nbt);
       }
       compound.put("ForgeEventNames", list);
       Packets.send(player, new PacketSync(13, compound, false));
    }

   public static void load(CompoundTag compound) {
      ForgeEventHandler.eventNames.clear();
      for (int i = 0; i < compound.getList("ForgeEventNames", 10).size(); i++) {
         CompoundTag nbt = compound.getList("ForgeEventNames", 10).getCompound(i);
         String name = nbt.getString("Name");
         Class<?> cls = null;
         try { cls = Class.forName(nbt.getString("Class")); }
         catch (Exception e) { LogWriter.error(e); }
         ForgeEventHandler.eventNames.put(cls, name);
      }
      compound.remove("ForgeEventNames");
      for (String key : compound.getAllKeys()) {
         Field field = null;
         for (Field f : configFields) {
            if (f.getName().equals(key)) {
               field = f;
               break;
            }
         }
         if (field == null) { continue; }
         Tag tag = compound.get(key);
         if (tag == null) { continue; }
         int id = tag.getId();
         try {
            if (id == 1) { field.set(null, compound.getBoolean(key)); }
            else if (id == 3) {
               if (field.getType() == Color.class) { field.set(null, new Color(compound.getInt(key))); }
               else { field.set(null, compound.getInt(key)); }
            }
            else if (id == 8) { field.set(null, compound.getString(key)); }
            else if (id == 11) {
               if (field.getType() == Color[].class) {
                  int[] arr = compound.getIntArray(key);
                  Color[] colors = new Color[arr.length];
                  for (int i = 0; i < arr.length; i++) { colors[i] = new Color(arr[i]); }
                  field.set(null, colors);
               }
               else { field.set(null, compound.getIntArray(key)); }
            }
         }
         catch (Exception e) { LogWriter.error(e); }
      }
   }

    public void loadConfig() {
      try {
         HashMap<String, Field> types = new HashMap<>();
         for (Field field : configFields) {
            ConfigProp prop = field.getAnnotation(ConfigProp.class);
            types.put(!prop.name().isEmpty() ? prop.name() : field.getName(), field);
         }
         if (configFile.exists()) {
            HashMap<String, Object> properties = parseConfig(configFile, types);
            for (String type: properties.keySet()) {
               Field field = types.get(type);
               Object obj = properties.get(type);
               if (!obj.equals(field.get(null))) {
                  field.set(null, obj);
               }
            }
            for (String type: types.keySet()) {
                if (!properties.containsKey(type)) {
                    updateFile = true;
                    break;
                }
            }
         } else {
            updateFile = true;
         }
      } catch (Exception e) {
         updateFile = true;
         LogWriter.except(e);
      }
      if (updateFile) { updateConfig(); }
      updateFile = false;
   }

   private HashMap<String, Object> parseConfig(File file, HashMap<String, Field> types) throws Exception {
      HashMap<String, Object> config = new HashMap<>();
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
      while(true) {
         String strLine;
         do {
            do {
               if ((strLine = reader.readLine()) == null) {
                  reader.close();
                  return config;
               }
            } while(strLine.startsWith("#"));
         } while(strLine.isEmpty());
         int index = strLine.indexOf("=");
         if (index > 0) {
            String name = strLine.substring(0, index);
            String prop = strLine.substring(index + 1);
            if (!types.containsKey(name)) {
               updateFile = true;
            } else {
               Object obj = null;
               try {
                  Class<?> class2 = types.get(name).getType();
                  if (class2.isAssignableFrom(String.class)) { obj = prop; }
                  else if (class2.isAssignableFrom(Integer.TYPE)) { obj = Integer.parseInt(prop); }
                  else if (class2.isAssignableFrom(Long.TYPE)) { obj = Long.parseLong(prop); }
                  else if (class2.isAssignableFrom(Short.TYPE)) { obj = Short.parseShort(prop); }
                  else if (class2.isAssignableFrom(Byte.TYPE)) { obj = Byte.parseByte(prop); }
                  else if (class2.isAssignableFrom(Boolean.TYPE)) { obj = Boolean.parseBoolean(prop); }
                  else if (class2.isAssignableFrom(Float.TYPE)) { obj = Float.parseFloat(prop); }
                  else if (class2.isAssignableFrom(Double.TYPE)) { obj = Double.parseDouble(prop); }
                  else if (class2.isAssignableFrom(Color.class)) {
                     obj = new Color((int) Long.parseLong(prop, 16));
                  }
                  else if (class2.isArray()) {
                     String[] values = prop.replace("[", "").replace("]", "").split(",");
                     if (class2 == Color[].class) {
                        Color[] colors = new Color[values.length];
                        for (int i = 0; i < values.length; i++) { colors[i] = new Color((int) Long.parseLong(values[i], 16)); }
                        obj = colors;
                     }
                     else if (class2 == int[].class) {
                        int[] ints = new int[values.length];
                        for (int i = 0; i < values.length; i++) { ints[i] = Integer.parseInt(values[i]); }
                        obj = ints;
                     }
                     else if (class2 == long[].class) {
                        long[] longs = new long[values.length];
                        for (int i = 0; i < values.length; i++) { longs[i] = Long.parseLong(values[i]); }
                        obj = longs;
                     }
                     else if (class2 == boolean[].class) {
                        boolean[] ints = new boolean[values.length];
                        for (int i = 0; i < values.length; i++) { ints[i] = Boolean.parseBoolean(values[i]); }
                        obj = ints;
                     }
                     else if (class2 == short[].class) {
                        short[] shorts = new short[values.length];
                        for (int i = 0; i < values.length; i++) { shorts[i] = Short.parseShort(values[i]); }
                        obj = shorts;
                     }
                     else if (class2 == byte[].class) {
                        byte[] bytes = new byte[values.length];
                        for (int i = 0; i < values.length; i++) { bytes[i] = Byte.parseByte(values[i]); }
                        obj = bytes;
                     }
                     else if (class2 == float[].class) {
                        float[] floats = new float[values.length];
                        for (int i = 0; i < values.length; i++) { floats[i] = Float.parseFloat(values[i]); }
                        obj = floats;
                     }
                     else if (class2 == double[].class) {
                        double[] doubles = new double[values.length];
                        for (int i = 0; i < values.length; i++) { doubles[i] = Double.parseDouble(values[i]); }
                        obj = doubles;
                     }
                     else if (class2 == String[].class) { obj = values; }
                  }
               }
               catch (Exception e) { LogWriter.error(e); }
               if (obj != null) {
                  config.put(name, obj);
               }
            }
         } else {
            this.updateFile = true;
         }
      }
   }

   public void updateConfig() {
      try {
         LogWriter.debug("Try update config in "+configFile);
         if (configFile == null || !configFile.exists() && !configFile.createNewFile()) { return; }
         BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
         for (Field field : configFields) {
            ConfigProp prop = field.getAnnotation(ConfigProp.class);
            if (!prop.info().isEmpty()) { out.write("#" + prop.info() + System.lineSeparator()); }
            String name = !prop.name().isEmpty() ? prop.name() : field.getName();
            try {
               Object object = field.get(parent);
               StringBuilder value;
               if (object.getClass().isArray()) {
                  value = new StringBuilder("[");
                  if (object instanceof Color[] colors) {
                     for (int i = 0; i < colors.length; i++) {
                        value.append(Integer.toHexString(colors[i].getRGB()).toUpperCase());
                        if (i < colors.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof int[] ints) {
                     for (int i = 0; i < ints.length; i++) {
                        value.append(ints[i]);
                        if (i < ints.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof long[] longs) {
                     for (int i = 0; i < longs.length; i++) {
                        value.append(longs[i]);
                        if (i < longs.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof boolean[] booleans) {
                     for (int i = 0; i < booleans.length; i++) {
                        value.append(booleans[i]);
                        if (i < booleans.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof short[] shorts) {
                     for (int i = 0; i < shorts.length; i++) {
                        value.append(shorts[i]);
                        if (i < shorts.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof byte[] bytes) {
                     for (int i = 0; i < bytes.length; i++) {
                        value.append(bytes[i]);
                        if (i < bytes.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof float[] floats) {
                     for (int i = 0; i < floats.length; i++) {
                        value.append(String.valueOf(floats[i]).replace(",", "."));
                        if (i < floats.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof double[] doubles) {
                     for (int i = 0; i < doubles.length; i++) {
                        value.append(String.valueOf(doubles[i]).replace(",", "."));
                        if (i < doubles.length - 1) { value.append(","); }
                     }
                  }
                  else if (object instanceof String[] strings) {
                     for (int i = 0; i < strings.length; i++) {
                        value.append(strings[i]);
                        if (i < strings.length - 1) { value.append(","); }
                     }
                  }
                  value.append("]");
               }
               else {
                  if (Color.class.isAssignableFrom(field.getType())) {
                     value = new StringBuilder(Integer.toHexString(((Color) object).getRGB()).toUpperCase());
                  }
                  else { value = new StringBuilder(object.toString()); }
               }
               out.write(name + "=" + value + System.lineSeparator());
               out.write(System.lineSeparator());
            }
            catch (Exception e) { LogWriter.error(e); }
         }
         out.close();
      } catch (IOException e) {
         LogWriter.error(e);
      }
   }

   public List<ConfigElement> getChildElements() {
      Map<String, TreeMap<String, ConfigElement>> types = new HashMap<>();
      for (Field field : configFields) {
         ConfigProp prop = field.getAnnotation(ConfigProp.class);
         if (prop == null) { continue; }
         if (!types.containsKey(prop.type())) { types.put(prop.type(), new TreeMap<>()); }
         types.get(prop.type()).put(field.getName(), new ConfigElement(parent, field, prop));
      }
      List<ConfigElement> list = new ArrayList<>();
      if (types.containsKey("common")) { list.addAll(types.get("common").values()); }
      if (types.containsKey("server")) { list.addAll(types.get("server").values()); }
      if (types.containsKey("client")) { list.addAll(types.get("client").values()); }
      return list;
   }

}
