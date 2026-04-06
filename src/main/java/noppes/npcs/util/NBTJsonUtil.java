package noppes.npcs.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import noppes.npcs.mixin.nbt.IListTagMixin;

public class NBTJsonUtil {

   public static String Convert(CompoundTag compound) {
      List<JsonLine> list = new ArrayList<>();
      JsonLine line = ReadTag("", compound, list);
      line.removeComma();
      return ConvertList(list);
   }

   public static CompoundTag Convert(String json) throws JsonException {
      json = json.trim();
      JsonFile file = new JsonFile(json);
      if (json.startsWith("{") && json.endsWith("}")) {
         CompoundTag compound = new CompoundTag();
         FillCompound(compound, file);
         return compound;
      } else {
         throw new JsonException("Not properly incapsulated between \"{ }\"", file);
      }
   }

   public static void FillCompound(CompoundTag compound, JsonFile json) throws JsonException {
      if (json.startsWith("{") || json.startsWith(",")) {
         json.cut(1);
      }

      if (!json.startsWith("}")) {
         int index = json.keyIndex();
         if (index < 1) {
            throw new JsonException("Expected key after ,", json);
         } else {
            String key = json.substring(0, index);
            json.cut(index + 1);
            Tag base = ReadValue(json);
            if (base == null) {
               base = StringTag.valueOf("");
            }

            if (key.startsWith("\"")) {
               key = key.substring(1);
            }

            if (key.endsWith("\"")) {
               key = key.substring(0, key.length() - 1);
            }

            compound.put(key, base);
            if (json.startsWith(",")) {
               FillCompound(compound, json);
            }

         }
      }
   }

   public static Tag ReadValue(JsonFile json) throws JsonException {
      if (json.startsWith("{")) {
         CompoundTag compound = new CompoundTag();
         FillCompound(compound, json);
         if (!json.startsWith("}")) {
            throw new JsonException("Expected }", json);
         } else {
            json.cut(1);
            return compound;
         }
      } else if (json.startsWith("[")) {
         json.cut(1);
         ListTag list = new ListTag();
         if (json.startsWith("B;") || json.startsWith("I;") || json.startsWith("L;")) {
            json.cut(2);
         }

         for(Tag value = ReadValue(json); value != null; value = ReadValue(json)) {
            list.add(value);
            if (!json.startsWith(",")) {
               break;
            }

            json.cut(1);
         }

         if (!json.startsWith("]")) {
            throw new JsonException("Expected ]", json);
         } else {
            json.cut(1);
            int i;
            if (list.getElementType() == 3) {
               int[] arr = new int[list.size()];

               for(i = 0; !list.isEmpty(); ++i) {
                  arr[i] = ((IntTag)list.remove(0)).getAsInt();
               }

               return new IntArrayTag(arr);
            } else if (list.getElementType() == 1) {
               byte[] arr = new byte[list.size()];

               for(i = 0; !list.isEmpty(); ++i) {
                  arr[i] = ((ByteTag)list.remove(0)).getAsByte();
               }

               return new ByteArrayTag(arr);
            } else if (list.getElementType() != 4) {
               return list;
            } else {
               long[] arr = new long[list.size()];

               for(i = 0; !list.isEmpty(); ++i) {
                  arr[i] = ((LongTag)list.remove(0)).getAsByte();
               }

               return new LongArrayTag(arr);
            }
         }
      } else {
         StringBuilder s;
         if (json.startsWith("\"")) {
            json.cut(1);
            s = new StringBuilder();

            String cut = "";
            for(boolean ignore = false; !json.startsWith("\"") || ignore; s.append(cut)) {
               cut = json.cutDirty(1);
               ignore = cut.equals("\\");
            }

            json.cut(1);
            return StringTag.valueOf(s.toString().replace("\\\\", "\\").replace("\\\"", "\""));
         } else {
            s = new StringBuilder();
            while (!json.startsWith(",", "]", "}")) { s.append(json.cut(1)); }
            s = new StringBuilder(s.toString().trim().toLowerCase());
            if (s.isEmpty()) { return null; }
            try {
               if (s.toString().endsWith("d")) {
                  return s.toString().equals("nand") ? DoubleTag.valueOf(Double.NaN) : DoubleTag.valueOf(Double.parseDouble(s.substring(0, s.length() - 1)));
               } else if (s.toString().endsWith("f")) {
                  return FloatTag.valueOf(Float.parseFloat(s.substring(0, s.length() - 1)));
               } else if (s.toString().endsWith("b")) {
                  return ByteTag.valueOf(Byte.parseByte(s.substring(0, s.length() - 1)));
               } else if (s.toString().endsWith("s")) {
                  return ShortTag.valueOf(Short.parseShort(s.substring(0, s.length() - 1)));
               } else if (s.toString().endsWith("l")) {
                  return LongTag.valueOf(Long.parseLong(s.substring(0, s.length() - 1)));
               } else {
                  return s.toString().contains(".") ? DoubleTag.valueOf(Double.parseDouble(s.toString())) : IntTag.valueOf(Integer.parseInt(s.toString()));
               }
            } catch (NumberFormatException var5) {
               throw new JsonException("Unable to convert: " + s + " to a number", json);
            }
         }
      }
   }

   private static JsonLine ReadTag(String name, Tag base, List<JsonLine> list) {
      if (!name.isEmpty()) {
         name = "\"" + name + "\": ";
      }

      JsonLine line;
      if (base.getId() == 9) {
         list.add(new JsonLine(name + "["));
         ListTag tags = (ListTag)base;
         line = null;
         List<Tag> data = ((IListTagMixin) tags).getList();

         Tag b;
         for(Iterator<Tag> var6 = data.iterator(); var6.hasNext(); line = ReadTag("", b, list)) {
            b = var6.next();
         }

         if (line != null) {
            line.removeComma();
         }

         list.add(new JsonLine("]"));
      }
      else if (base.getId() == 10) {
         list.add(new JsonLine(name + "{"));
         CompoundTag compound = (CompoundTag) base;
         line = null;

         List<String> keys = new ArrayList<>(compound.getAllKeys());
         Collections.sort(keys);
         for (String key : keys) {
            line = ReadTag(key, compound.get(key), list);
         }

         if (line != null) {
            line.removeComma();
         }

         list.add(new JsonLine("}"));
      } else if (base.getId() == 11) {
         list.add(new JsonLine(name + base.toString().replaceFirst(",]", "]")));
      } else if (base.getId() == 8) {
         list.add(new JsonLine(name + quoteAndEscape(base.getAsString())));
      } else {
         list.add(new JsonLine(name + base));
      }

      line = list.get(list.size() - 1);
      line.line = line.line + ",";
      return line;
   }

   private static String ConvertList(List<JsonLine> list) {
      StringBuilder json = new StringBuilder();
      int tab = 0;
      for (JsonLine tag : list) {
         if (tag.reduceTab()) {
            --tab;
         }
         json.append("    ".repeat(Math.max(0, tab)));
         json.append(tag).append("\n");
         if (tag.increaseTab()) { ++tab; }
      }
      return json.toString();
   }

   public static CompoundTag LoadFile(File file) throws IOException, JsonException {
      return Convert(Files.readString(file.toPath(), StandardCharsets.UTF_8));
   }

   public static void SaveFile(File file, CompoundTag compound) throws IOException, JsonException {
      String json = Convert(compound);
      try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
         writer.write(json);
      }
   }

   public static void main(String[] args) {
      CompoundTag comp = new CompoundTag();
      CompoundTag comp2 = new CompoundTag();
      comp2.putByteArray("test", new byte[]{0, 0, 1, 1, 0});
      comp.put("comp", comp2);
      Convert(comp);
   }

   public static String quoteAndEscape(String p_193588_0_) {
      StringBuilder stringbuilder = new StringBuilder("\"");

      for(int i = 0; i < p_193588_0_.length(); ++i) {
         char c0 = p_193588_0_.charAt(i);
         if (c0 == '\\' || c0 == '"') {
            stringbuilder.append('\\');
         }

         stringbuilder.append(c0);
      }

      return stringbuilder.append('"').toString();
   }

   public static JsonObject ConvertToJson(CompoundTag compound) {
      JsonObject jsonObject = new JsonObject();
      for (String key : compound.getAllKeys()) {
         Tag value = compound.get(key);
         Object convertedValue = convertTag(value);
         if (convertedValue instanceof JsonObject jsObject) { jsonObject.add(key, jsObject); }
         else if (convertedValue instanceof JsonPrimitive jsPrimitive) { jsonObject.add(key, jsPrimitive); }
      }
      return jsonObject;
   }

   private static Object convertTag(Tag tag) {
      if (tag instanceof CompoundTag) {
         return ConvertToJson((CompoundTag) tag);
      } else if (tag instanceof ListTag) {
         return convertList((ListTag) tag);
      } else if (tag instanceof StringTag) {
         return new JsonPrimitive(tag.getAsString());
      } else if (tag instanceof ByteTag) {
         return new JsonPrimitive(((ByteTag) tag).getAsByte());
      } else if (tag instanceof ShortTag) {
         return new JsonPrimitive(((ShortTag) tag).getAsShort());
      } else if (tag instanceof IntTag) {
         return new JsonPrimitive(((IntTag) tag).getAsInt());
      } else if (tag instanceof LongTag) {
         return new JsonPrimitive(((LongTag) tag).getAsLong());
      } else if (tag instanceof FloatTag) {
         return new JsonPrimitive(((FloatTag) tag).getAsFloat());
      } else if (tag instanceof DoubleTag) {
         return new JsonPrimitive(((DoubleTag) tag).getAsDouble());
      }
      throw new IllegalArgumentException("Unsupported tag type: " + tag.getClass().getSimpleName());
   }

   private static JsonObject convertList(ListTag listTag) {
      JsonObject jsonArray = new JsonObject();
      for (int i = 0; i < listTag.size(); i++) {
         Tag element = listTag.get(i);
         Object convertedElement = convertTag(element);
         if (convertedElement instanceof JsonObject || convertedElement instanceof JsonPrimitive) {
             assert convertedElement instanceof JsonObject;
             jsonArray.add(Integer.toString(i), (JsonObject) convertedElement);
         }
      }
      return jsonArray;
   }

   static class JsonLine {
      private String line;

      public JsonLine(String line) {
         this.line = line;
      }

      public void removeComma() {
         if (this.line.endsWith(",")) {
            this.line = this.line.substring(0, this.line.length() - 1);
         }

      }

      public boolean reduceTab() {
         int length = this.line.length();
         return length == 1 && (this.line.endsWith("}") || this.line.endsWith("]")) || length == 2 && (this.line.endsWith("},") || this.line.endsWith("],"));
      }

      public boolean increaseTab() {
         return this.line.endsWith("{") || this.line.endsWith("[");
      }

      public String toString() {
         return this.line;
      }
   }

   public static class JsonException extends Exception {
      public JsonException(String message, JsonFile json) {
         super(message + "; Error in " + json.getCurrentPos());
      }
   }
}
