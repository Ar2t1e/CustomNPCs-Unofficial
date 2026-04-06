package noppes.npcs.api.wrapper;

import java.util.Objects;

import net.minecraft.nbt.*;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.NBTJsonUtil;

public class NBTWrapper implements INbt {

   private final CompoundTag compound;

   public NBTWrapper(CompoundTag compoundIn) {
      compound = compoundIn;
   }

   public void remove(String key) {
      compound.remove(key);
   }

   public boolean has(String key) {
      return compound.contains(key);
   }

   public boolean has(String key, int type) {
      return compound.contains(key, type);
   }

   public boolean getBoolean(String key) {
      return compound.getBoolean(key);
   }

   public void setBoolean(String key, boolean value) {
      compound.putBoolean(key, value);
   }

   public short getShort(String key) {
      return compound.getShort(key);
   }

   public void setShort(String key, short value) {
      compound.putShort(key, value);
   }

   public int getInteger(String key) {
      return compound.getInt(key);
   }

   public void setInteger(String key, int value) {
      compound.putInt(key, value);
   }

   public byte getByte(String key) {
      return compound.getByte(key);
   }

   public void setByte(String key, byte value) {
      compound.putByte(key, value);
   }

   public long getLong(String key) {
      return compound.getLong(key);
   }

   public void setLong(String key, long value) {
      compound.putLong(key, value);
   }

   public double getDouble(String key) {
      return compound.getDouble(key);
   }

   public void setDouble(String key, double value) {
      compound.putDouble(key, value);
   }

   public float getFloat(String key) {
      return compound.getFloat(key);
   }

   public void setFloat(String key, float value) {
      compound.putFloat(key, value);
   }

   public String getString(String key) {
      return compound.getString(key);
   }

   public void setString(String key, String value) {
      compound.putString(key, value);
   }

   public byte[] getByteArray(String key) {
      return compound.getByteArray(key);
   }

   public void setByteArray(String key, byte[] value) {
      compound.putByteArray(key, value);
   }

   public int[] getIntegerArray(String key) {
      return compound.getIntArray(key);
   }

   public void setIntegerArray(String key, int[] value) {
      compound.putIntArray(key, value);
   }

   public Object[] getList(String key, int type) {
      ListTag list = compound.getList(key, type);
      Object[] nbts = new Object[list.size()];
      for(int i = 0; i < list.size(); ++i) {
         switch (list.getElementType()) {
            case 0: {
               nbts[i] = 0;
               break;
            }
            case 1: {
               nbts[i] = ((ByteTag) list.get(i)).getAsByte();
               break;
            }
            case 2: {
               nbts[i] = ((ShortTag) list.get(i)).getAsShort();
               break;
            }
            case 3: {
               nbts[i] = list.getInt(i);
               break;
            }
            case 4: {
               nbts[i] = ((LongTag) list.get(i)).getAsShort();
               break;
            }
            case 5: {
               nbts[i] = list.getFloat(i);
               break;
            }
            case 6: {
               nbts[i] = list.getDouble(i);
               break;
            }
            case 7: {
               nbts[i] = ((ByteArrayTag) list.get(i)).getAsByteArray();
               break;
            }
            case 8: {
               nbts[i] = list.getString(i);
               break;
            }
            case 10: {
               nbts[i] = Objects.requireNonNull(NpcAPI.Instance()).getINbt(list.getCompound(i));
               break;
            }
            case 11: {
               nbts[i] = list.getIntArray(i);
               break;
            }
            case 12: {
               nbts[i] = ((LongArrayTag) list.get(i)).getAsLongArray();
               break;
            }
            case 99: {
               nbts[i] = ((NumericTag) list.get(i)).getAsNumber();
               break;
            }
            default: { break; }
         }
      }
      return nbts;
   }

   public int getListType(String key) {
      Tag b = compound.get(key);
      if (b == null) {
         return 0;
      } else if (b.getId() != 9) {
         throw new CustomNPCsException("NBT tag " + key + " isn't a list");
      } else {
         return ((ListTag)b).getElementType();
      }
   }

   public void setList(String key, Object[] values) {
      ListTag list = new ListTag();
      int type = -1;
      for (int i = 0; i < values.length; i++) {
         Tag tag = getListTag(values[i], type);
         if (tag == null) {
            throw new CustomNPCsException("Value[" + i + "] \"" + values[i] + "\" - cannot be converted to a tag or does not match the storage type of the ListTag!");
         }
         if (type < 0) { type = tag.getId(); }
         list.add(tag);
      }
      compound.put(key, list);
   }

   private Tag getListTag(Object value, int type) {
      if (value instanceof Tag) {
         if (type < 0 || ((Tag) value).getId() == type) { return (Tag) value; }
      } else if (value instanceof Boolean) {
         if (type < 0 || type == 1) { return ByteTag.valueOf((Boolean) value); }
      } else if (value instanceof Byte) {
         if (type < 0 || type == 2) { return ByteTag.valueOf((Byte) value); }
      } else if (value instanceof Integer) {
         if (type < 0 || type == 3) { return IntTag.valueOf((Integer) value); }
      } else if (value instanceof Short) {
         if (type < 0 || type == 4) { return ShortTag.valueOf((Short) value); }
      } else if (value instanceof Float) {
         if (type < 0 || type == 5) { return FloatTag.valueOf((Float) value); }
      } else if (value instanceof Double) {
         if (type < 0 || type == 6) { return DoubleTag.valueOf((Double) value); }
      } else if (value instanceof Byte[]) {
         if (type < 0 || type == 7) {
            byte[] data;
            if (value instanceof Byte[]) {
               data = new byte[((Byte[]) value).length];
               for (int i = 0; i < data.length; i++) { data[i] = ((Byte[]) value)[i]; }
            } else { data = (byte[]) value; }
            return new ByteArrayTag(data);
         }
      } else if (value instanceof String) {
         if (type < 0 || type == 8) { return StringTag.valueOf((String) value); }
      } else if (value instanceof INbt) {
         if (type < 0 || type == 10) { return ((INbt) value).getMCNBT(); }
      } else if (value instanceof int[] || value instanceof Integer[]) {
         if (type < 0 || type == 11) {
            int[] data;
            if (value instanceof Integer[]) {
               data = new int[((Integer[]) value).length];
               for (int i = 0; i < data.length; i++) { data[i] = ((Integer[]) value)[i]; }
            } else { data = (int[]) value; }
            return new IntArrayTag(data);
         }
      }
      else if (value instanceof long[] || value instanceof Long[]) {
         if (type < 0 || type == 12) {
            long[] data;
            if (value instanceof Long[]) {
               data = new long[((Long[]) value).length];
               for (int i = 0; i < data.length; i++) { data[i] = ((Long[]) value)[i]; }
            } else { data = (long[]) value; }
            return new LongArrayTag(data);
         }
      }
      return null;
   }

   @Override
   public void addToList(String keyList, Object value) {
      Tag list = compound.get(keyList);
      if (list == null) { compound.put(keyList, (list = new ListTag())); }
      else if (!(list instanceof ListTag)) { throw new CustomNPCsException("\"" + keyList + "\" - already exists and is not a \"" + keyList + "\" ListTag!"); }
      if (((ListTag) list).isEmpty()) {
         Tag tag = getListTag(value, -1);
         if (tag != null) { ((ListTag) list).add(tag); }
         return;
      }
      Tag tag = getListTag(value, -1);
      if (tag == null) {
         throw new CustomNPCsException("Value \"" + value + "\" - cannot be converted to tag from \"" + keyList + "\" ListTag!");
      }
      if (tag.getId() != ((ListTag) list).getElementType()) {
         throw new CustomNPCsException("Value \"" + value + "\" - does not match storage type in \"" + keyList + "\"ListTag!");
      }
      ((ListTag) list).add(tag);
   }

   public INbt getCompound(String key) {
      return Objects.requireNonNull(NpcAPI.Instance()).getINbt(compound.getCompound(key));
   }

   public void setCompound(String key, INbt value) {
      if (value == null) {
         throw new CustomNPCsException("Value cant be null");
      } else {
         compound.put(key, value.getMCNBT());
      }
   }

   public String[] getKeys() {
      return compound.getAllKeys().toArray(new String[0]);
   }

   public int getType(String key) {
      if (!compound.contains(key)) { return -1; }
      return Objects.requireNonNull(compound.get(key)).getId();
   }

   public CompoundTag getMCNBT() {
      return compound;
   }

   public String toJsonString() {
      return NBTJsonUtil.Convert(compound);
   }

   public boolean isEqual(INbt nbt) {
      return nbt != null && compound.equals(nbt.getMCNBT());
   }

   public void clear() {
      for (String name : compound.getAllKeys()) { compound.remove(name); }
   }

   public boolean isEmpty() {
      return compound.isEmpty();
   }

   public void merge(INbt nbt) {
      compound.merge(nbt.getMCNBT());
   }

   public void mcSetTag(String key, Tag tag) {
      compound.put(key, tag);
   }

   public Tag mcGetTag(String key) {
      return compound.get(key);
   }
}
