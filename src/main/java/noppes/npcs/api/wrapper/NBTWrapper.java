package noppes.npcs.api.wrapper;

import net.minecraft.nbt.*;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.NBTJsonUtil;

import java.util.Objects;

public class NBTWrapper implements INbt {

	private final NBTTagCompound compound;

	public NBTWrapper(NBTTagCompound compound) {
		this.compound = compound;
	}

	@Override
	public void clear() {
		for (String name : compound.getKeySet()) {
			compound.removeTag(name);
		}
	}

	@Override
	public boolean isEmpty() { return compound.hasNoTags(); }

	@Override
	public boolean getBoolean(String key) {
		return this.compound.getBoolean(key);
	}

	@Override
	public byte getByte(String key) {
		return this.compound.getByte(key);
	}

	@Override
	public byte[] getByteArray(String key) {
		return this.compound.getByteArray(key);
	}

	@Override
	public INbt getCompound(String key) {
		return Objects.requireNonNull(NpcAPI.Instance()).getINbt(compound.getCompoundTag(key));
	}

	@Override
	public double getDouble(String key) {
		return this.compound.getDouble(key);
	}

	@Override
	public float getFloat(String key) {
		return this.compound.getFloat(key);
	}

	@Override
	public int getInteger(String key) {
		return this.compound.getInteger(key);
	}

	@Override
	public int[] getIntegerArray(String key) {
		return this.compound.getIntArray(key);
	}

	@Override
	public String[] getKeys() {
		return this.compound.getKeySet().toArray(new String[0]);
	}

	@Override
	public Object[] getList(String key, int type) {
		NBTTagList list = this.compound.getTagList(key, type);
		Object[] nbts = new Object[list.tagCount()];
		for (int i = 0; i < list.tagCount(); ++i) {
			if (list.getTagType() == 10) {
				nbts[i] = Objects.requireNonNull(NpcAPI.Instance()).getINbt(list.getCompoundTagAt(i));
			} else if (list.getTagType() == 8) {
				nbts[i] = list.getStringTagAt(i);
			} else if (list.getTagType() == 6) {
				nbts[i] = list.getDoubleAt(i);
			} else if (list.getTagType() == 5) {
				nbts[i] = list.getFloatAt(i);
			} else if (list.getTagType() == 3) {
				nbts[i] = list.getIntAt(i);
			} else if (list.getTagType() == 11) {
				nbts[i] = list.getIntArrayAt(i);
			}
		}
		return nbts;
	}

	@Override
	public int getListType(String key) {
		NBTBase b = this.compound.getTag(key);
        if (b.getId() != 9) {
			throw new CustomNPCsException("NBT tag " + key + " isn't a list");
		}
		return ((NBTTagList) b).getTagType();
	}


	@Override
	public void addToList(String keyList, Object value) {
		if (!compound.hasKey(keyList)) { compound.setTag(keyList, new NBTTagList()); }
		NBTBase list = compound.getTag(keyList);
		if (!(list instanceof NBTTagList)) { throw new CustomNPCsException("\"" + keyList + "\" - already exists and is not a \"" + keyList + "\" ListTag!"); }
		if (((NBTTagList) list).tagCount() == 0) {
			NBTBase tag = getListTag(value, -1);
			if (tag != null) { ((NBTTagList) list).appendTag(tag); }
			return;
		}
		NBTBase tag = getListTag(value, -1);
		if (tag == null) {
			throw new CustomNPCsException("Value \"" + value + "\" - cannot be converted to tag from \"" + keyList + "\" NBTTagList!");
		}
		if (tag.getId() != ((NBTTagList) list).getTagType()) {
			throw new CustomNPCsException("Value \"" + value + "\" - does not match storage type in \"" + keyList + "\"NBTTagList!");
		}
		((NBTTagList) list).appendTag(tag);
	}

	private NBTBase getListTag(Object value, int type) {
		if (value instanceof NBTBase) {
			if (type < 0 || ((NBTBase) value).getId() == type) { return (NBTBase) value; }
		} else if (value instanceof Boolean) {
			if (type < 0 || type == 1) { return new NBTTagByte((byte) ((boolean) value ? 1 : 0)); }
		} else if (value instanceof Byte) {
			if (type < 0 || type == 2) { return new NBTTagByte((Byte) value); }
		} else if (value instanceof Integer) {
			if (type < 0 || type == 3) { return new NBTTagInt((Integer) value); }
		} else if (value instanceof Short) {
			if (type < 0 || type == 4) { return new NBTTagShort((Short) value); }
		} else if (value instanceof Float) {
			if (type < 0 || type == 5) { return new NBTTagFloat((Float) value); }
		} else if (value instanceof Double) {
			if (type < 0 || type == 6) { return new NBTTagDouble((Double) value); }
		} else if (value instanceof Byte[]) {
			if (type < 0 || type == 7) {
				byte[] data;
				if (value instanceof Byte[]) {
					data = new byte[((Byte[]) value).length];
					for (int i = 0; i < data.length; i++) { data[i] = ((Byte[]) value)[i]; }
				} else { data = (byte[]) value; }
				return new NBTTagByteArray(data);
			}
		} else if (value instanceof String) {
			if (type < 0 || type == 8) { return new NBTTagString((String) value); }
		} else if (value instanceof INbt) {
			if (type < 0 || type == 10) { return ((INbt) value).getMCNBT(); }
		} else if (value instanceof int[] || value instanceof Integer[]) {
			if (type < 0 || type == 11) {
				int[] data;
				if (value instanceof Integer[]) {
					data = new int[((Integer[]) value).length];
					for (int i = 0; i < data.length; i++) { data[i] = ((Integer[]) value)[i]; }
				} else { data = (int[]) value; }
				return new NBTTagIntArray(data);
			}
		}
		else if (value instanceof long[] || value instanceof Long[]) {
			if (type < 0 || type == 12) {
				long[] data;
				if (value instanceof Long[]) {
					data = new long[((Long[]) value).length];
					for (int i = 0; i < data.length; i++) { data[i] = ((Long[]) value)[i]; }
				} else { data = (long[]) value; }
				return new NBTTagLongArray(data);
			}
		}
		return null;
	}

	@Override
	public long getLong(String key) {
		return this.compound.getLong(key);
	}

	@Override
	public NBTTagCompound getMCNBT() {
		return this.compound;
	}

	@Override
	public short getShort(String key) {
		return this.compound.getShort(key);
	}

	@Override
	public String getString(String key) {
		return this.compound.getString(key);
	}

	@Override
	public int getType(String key) {
		return this.compound.getTagId(key);
	}

	@Override
	public boolean has(String key) {
		return this.compound.hasKey(key);
	}

	@Override
	public boolean has(String key, int type) {
		return compound.hasKey(key, type);
	}

	@Override
	public boolean isEqual(INbt nbt) {
		return nbt != null && this.compound.equals(nbt.getMCNBT());
	}

	@Override
	public void merge(INbt nbt) {
		this.compound.merge(nbt.getMCNBT());
	}

	@Override
	public void remove(String key) {
		this.compound.removeTag(key);
	}

	@Override
	public void setBoolean(String key, boolean value) {
		this.compound.setBoolean(key, value);
	}

	@Override
	public void setByte(String key, byte value) {
		this.compound.setByte(key, value);
	}

	@Override
	public void setByteArray(String key, byte[] value) {
		this.compound.setByteArray(key, value);
	}

	@Override
	public void setCompound(String key, INbt value) {
		if (value == null) {
			throw new CustomNPCsException("Value cant be null");
		}
		this.compound.setTag(key, value.getMCNBT());
	}

	@Override
	public void setDouble(String key, double value) {
		this.compound.setDouble(key, value);
	}

	@Override
	public void setFloat(String key, float value) {
		this.compound.setFloat(key, value);
	}

	@Override
	public void setInteger(String key, int value) {
		this.compound.setInteger(key, value);
	}

	@Override
	public void setIntegerArray(String key, int[] value) {
		this.compound.setIntArray(key, value);
	}

	@Override
	public void setList(String key, Object[] values) {
		NBTTagList list = new NBTTagList();
		int type = -1;
		for (int i = 0; i < values.length; i++) {
			NBTBase tag = getListTag(values[i], type);
			if (tag == null) {
				throw new CustomNPCsException("Value[" + i + "] \"" + values[i] + "\" - cannot be converted to a tag or does not match the storage type of the ListTag!");
			}
			if (type < 0) { type = tag.getId(); }
			list.appendTag(tag);
		}
		compound.setTag(key, list);
	}

	@Override
	public void setLong(String key, long value) {
		this.compound.setLong(key, value);
	}

	@Override
	public void setShort(String key, short value) {
		this.compound.setShort(key, value);
	}

	@Override
	public void setString(String key, String value) {
		this.compound.setString(key, value);
	}

	@Override
	public String toJsonString() {
		return NBTJsonUtil.Convert(this.compound);
	}

	@Override
	public NBTBase mcGetTag(String key) {
		return compound.getTag(key);
	}

	@Override
	public void mcSetTag(String key, NBTBase tag) {
		compound.setTag(key, tag);
	}

}
