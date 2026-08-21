package noppes.npcs.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("unused")
public interface INbt {

	void remove(@ParamName("key") String key);

	boolean has(@ParamName("key") String key);

	boolean has(@ParamName("key") String key, @ParamName("type") int type);

	boolean getBoolean(@ParamName("key") String key);

	void setBoolean(@ParamName("key") String key, @ParamName("value") boolean value);

	short getShort(@ParamName("key") String key);

	void setShort(@ParamName("key") String key, @ParamName("value") short value);

	int getInteger(@ParamName("key") String key);

	void setInteger(@ParamName("key") String key, @ParamName("value") int value);

	byte getByte(@ParamName("key") String key);

	void setByte(@ParamName("key") String key, @ParamName("value") byte value);

	long getLong(@ParamName("key") String key);

	void setLong(@ParamName("key") String key, @ParamName("value") long value);

	double getDouble(@ParamName("key") String key);

	void setDouble(@ParamName("key") String key, @ParamName("value") double value);

	float getFloat(@ParamName("key") String key);

	void setFloat(@ParamName("key") String key, @ParamName("value") float value);

	String getString(@ParamName("key") String key);

	void setString(@ParamName("key") String key, @ParamName("value") String value);

	byte[] getByteArray(@ParamName("key") String key);

	void setByteArray(@ParamName("key") String key, @ParamName("value") byte[] value);

	int[] getIntegerArray(@ParamName("key") String key);

	void setIntegerArray(@ParamName("key") String key, @ParamName("value") int[] value);

	Object[] getList(@ParamName("key") String key, @ParamName("type") int type);

	int getListType(@ParamName("key") String key);

	void setList(@ParamName("key") String key, @ParamName("values") Object[] values);

	void addToList(@ParamName("keyList") String keyList, @ParamName("value") Object value);

	INbt getCompound(@ParamName("key") String key);

	void setCompound(@ParamName("key") String key, @ParamName("value") INbt value);

	String[] getKeys();

	int getType(@ParamName("key") String key);

	NBTTagCompound getMCNBT();

	String toJsonString();

	boolean isEqual(@ParamName("nbt") INbt nbt);

	void clear();

	boolean isEmpty();

	void merge(@ParamName("nbt") INbt nbt);

	void mcSetTag(@ParamName("key") String key, @ParamName("tag") NBTBase value);

	NBTBase mcGetTag(@ParamName("key") String key);

}
