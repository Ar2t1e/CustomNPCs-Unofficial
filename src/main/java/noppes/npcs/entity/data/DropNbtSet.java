package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.entity.data.IDropNbtSet;
import noppes.npcs.util.ValueUtil;

public class DropNbtSet implements IDropNbtSet {

	protected final DropSet parent;
	protected double chance;
	protected String path;
	protected int type;
	protected int typeList;
	protected String[] values;
	protected Random rnd = new Random();

	public DropNbtSet(DropSet ds) {
		parent = ds;
		path = "";
		values = new String[0];
		type = 0;
		typeList = 0;
		chance = 100.0d;
	}

	public String checkValue(String value, int typeIn) {
		switch (typeIn) {
			case 0: {
				if (value.equalsIgnoreCase("true")) { return "true"; }
				else if (value.equalsIgnoreCase("false")) { return "false"; }
				break;
			} // boolean
			case 1: {
				try {
					byte b = Byte.parseByte(value);
					return String.valueOf(b);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // byte
			case 2: {
				try {
					short s = Short.parseShort(value);
					return String.valueOf(s);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // short
			case 3: {
				try {
					int b = Integer.parseInt(value);
					return String.valueOf(b);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // integer
			case 4: {
				try {
					long l = Long.parseLong(value);
					return String.valueOf(l);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // long
			case 5: {
				try {
					float f = Float.parseFloat(value);
					return String.valueOf(f);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // float
			case 6: {
				try {
					double d = Double.parseDouble(value);
					return String.valueOf(d);
				} catch (Exception e) { LogWriter.error(e); }
				break;
			} // double
			case 8: { return value == null ? "" : value; } // string
			case 7: // byte array
			case 9: // list
			case 11: // integer array
			case 12: {
				if (value.startsWith("[") && value.endsWith("]")) {
					StringBuilder text = new StringBuilder();
					for (String str : value.substring(1, value.length() - 1).split(",")) {
						String sc = checkValue(str, typeIn == 9 ? typeList : typeIn);
						if (sc != null) {
							if (text.length() > 0) { text.append(","); }
							text.append(sc);
						}
					}
					if (text.length() > 0) { return text.toString(); }
				}
				break;
			} // or long array
		}
		return null;
	}

	@Override
	public double getChance() { return Math.round(chance * 10000.0d) / 10000.0d; }

	@Override
	public void createAndPut(IItemStack item) {
		NBTTagCompound compound = item.getNbt().getMCNBT();
		String key = path;
		// create path:
		if (key.contains(".")) {
			String keyName;
			while (key.contains(".")) {
				keyName = key.substring(0, key.indexOf("."));
				key = key.substring(key.indexOf(".") + 1);
				if (compound instanceof NBTTagCompound) {
					if (keyName.contains("[") || keyName.endsWith("]")) {
						try {
							int pos = Integer.parseInt(keyName.substring(keyName.indexOf("[") + 1, keyName.length() - 1));
							if (pos < 0) { pos = 0;}
							keyName = keyName.substring(0, keyName.indexOf("["));
							if (!compound.hasKey(keyName, 9)) { compound.setTag(keyName, new NBTTagList()); }
							NBTTagList list = compound.getTagList(keyName, 9);
							if (pos >= list.tagCount()) {
								while (pos >= list.tagCount()) {
									NBTBase tag;
									switch (list.getTagType() == 0 ? key.contains(".") ? 10 : typeList : list.getTagType()) {
										case 1: tag = new NBTTagByte((byte) 0); break;
										case 2: tag = new NBTTagShort((short) 0); break;
										case 3: tag = new NBTTagInt(0); break;
										case 4: tag = new NBTTagLong(0); break;
										case 5: tag = new NBTTagFloat(0.0f); break;
										case 6: tag = new NBTTagDouble(0.0d); break;
										case 8: tag = new NBTTagString(""); break;
										case 9: tag = new NBTTagList(); break;
										case 11: tag = new NBTTagIntArray(new int[0]); break;
										case 12: tag = new NBTTagLongArray(new long[0]); break;
										default: tag = new NBTTagCompound(); break;
									}
									list.appendTag(tag);
								}
							}
							if (list.get(pos) instanceof NBTTagCompound) { compound = (NBTTagCompound) list.get(pos); }
						}
						catch (Exception e) {
							LogWriter.error("Failed create list tag: " + keyName + " from path: " + path);
							return;
						}
					}
					else {
						if (!compound.hasKey(keyName, 10)) { compound.setTag(keyName, new NBTTagCompound()); }
						compound = compound.getCompoundTag(keyName);
					}
				}
				else {
					LogWriter.error("Failed create compound tag: " + keyName + " from path: " + path);
					return;
				} // error create path
			}
		}
		// set value:
		String rndValue = values[rnd.nextInt(values.length)];
		try {
			switch (type) {
				case 0: compound.setBoolean(key, Boolean.parseBoolean(rndValue)); break;
				case 1: compound.setByte(key, Byte.parseByte(rndValue)); break;
				case 2: compound.setShort(key, Short.parseShort(rndValue)); break;
				case 3: compound.setInteger(key, Integer.parseInt(rndValue)); break;
				case 4: compound.setLong(key, Long.parseLong(rndValue)); break;
				case 5: compound.setFloat(key, Float.parseFloat(rndValue)); break;
				case 6: compound.setDouble(key, Double.parseDouble(rndValue)); break;
				case 7: {
					String[] variants = rndValue.split(",");
					byte[] array = new byte[variants.length];
					for (int i = 0; i < variants.length; i++) { array[i] = Byte.parseByte(variants[i]); }
					compound.setByteArray(key, array);
					break;
				} // byte array
				case 8: compound.setString(key, rndValue); break;
				case 9: { // list
					String[] variants = rndValue.split(",");
					NBTTagList list = new NBTTagList();
					for (String value : variants) {
						switch (typeList) {
							case 1: {
								if (value.equalsIgnoreCase("false")) { list.appendTag(new NBTTagByte((byte) 0)); }
								else if (value.equalsIgnoreCase("true")) { list.appendTag(new NBTTagByte((byte) 1)); }
								else { list.appendTag(new NBTTagByte(Byte.parseByte(value))); }
								break;
							}
							case 2: list.appendTag(new NBTTagShort(Short.parseShort(value))); break;
							case 3: list.appendTag(new NBTTagInt(Integer.parseInt(value))); break;
							case 4: list.appendTag(new NBTTagLong(Long.parseLong(value))); break;
							case 5: list.appendTag(new NBTTagFloat(Float.parseFloat(value))); break;
							case 6: list.appendTag(new NBTTagDouble(Double.parseDouble(value))); break;
							case 7: {
								String[] ints = value.split(";");
								byte[] array = new byte[ints.length];
								for (int i = 0; i < ints.length; i++) { array[i] = Byte.parseByte(variants[i]); }
								list.appendTag(new NBTTagByteArray(array));
								break;
							}
							case 8: list.appendTag(new NBTTagString(value)); break;
							case 11: {
								String[] ints = value.split(";");
								int[] array = new int[ints.length];
								for (int i = 0; i < ints.length; i++) { array[i] = Integer.parseInt(variants[i]); }
								list.appendTag(new NBTTagIntArray(array));
								break;
							}
							case 12: {
								String[] ints = value.split(";");
								long[] array = new long[ints.length];
								for (int i = 0; i < ints.length; i++) { array[i] = Long.parseLong(ints[i]); }
								list.appendTag(new NBTTagLongArray(array));
								break;
							}
						}
					}
					compound.setTag(key, list);
					break;
				}
				case 11: {
					String[] variants = rndValue.split(",");
					int[] array = new int[variants.length];
					for (int i = 0; i < variants.length; i++) { array[i] = Integer.parseInt(variants[i]); }
					compound.setIntArray(key, array);
					break;
				} // integer array
				case 12: {
					String[] variants = rndValue.split(",");
					long[] array = new long[variants.length];
					for (int i = 0; i < variants.length; i++) { array[i] = Long.parseLong(variants[i]); }
					compound.setTag(key, new NBTTagLongArray(array));
					break;
				} // long array
			}
		}
		catch (Exception e) { LogWriter.error("Failed set key: " + key + " with value: " + rndValue + " to path: " + path); }
	}

	public Component getKey() {
		Component keyName = Component.empty();
		double ch = Math.round(chance * 10.0d) / 10.d;
		String chance = String.valueOf(ch).replace(".", ",");
		if (ch == (int) ch) { chance = String.valueOf((int) ch); }
		chance += "%";
		keyName.append(Component.literal(chance).withStyle(TextFormatting.YELLOW));
		keyName.append(getPathToKey().withStyle(TextFormatting.RESET));
		if (values.length == 0) {
			keyName.append(Component.literal("=").withStyle(TextFormatting.AQUA))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY))
					.append(Component.literal("NULL").withStyle(TextFormatting.RED))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY));
		} else if (values.length == 1) {
			keyName.append(Component.literal("=").withStyle(TextFormatting.AQUA))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY))
					.append(Component.literal(values[0]).withStyle(TextFormatting.RED))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY));
		} else {
			keyName.append(Component.literal("=").withStyle(TextFormatting.AQUA))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY))
					.append(Component.literal("" + values.length).withStyle(TextFormatting.GOLD))
					.append(Component.literal("|").withStyle(TextFormatting.GRAY));
		}
		return keyName;
		//return keyName.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(TextFormatting.DARK_GRAY));
	}

	private Component getPathToKey() {
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

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtDS = new NBTTagCompound();
		nbtDS.setInteger("Type", type);
		nbtDS.setInteger("TypeList", typeList);
		nbtDS.setString("Path", path);
		nbtDS.setDouble("Chance", chance);
		NBTTagList vs = new NBTTagList();
		for (String s : values) {
			if (s != null) {
				vs.appendTag(new NBTTagString(s));
			}
		}
		nbtDS.setTag("Values", vs);
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

	public void load(NBTTagCompound nbtDS) {
		type = nbtDS.getInteger("Type");
		typeList = nbtDS.getInteger("TypeList");
		path = nbtDS.getString("Path");
		chance = nbtDS.getDouble("Chance");
		String[] vs = new String[nbtDS.getTagList("Values", 8).tagCount()];
		for (int i = 0; i < nbtDS.getTagList("Values", 8).tagCount(); i++) {
			String ch = checkValue(nbtDS.getTagList("Values", 8).getStringTagAt(i), type);
			if (ch != null) { vs[i] = ch; }
		}
		values = vs;
	}

	@Override
	public void remove() { parent.removeDropNbt(this); }

	@Override
	public void setChance(double chanceIn) {
		double newChance = ValueUtil.correctDouble(chanceIn, 0.0001d, 100.0d);
		chance = Math.round(newChance * 10000.0d) / 10000.0d;
	}

	@Override
	public void setPath(String pathIn) { path = pathIn; }

	@Override
	public void setType(int typeIn) {
		if ((typeIn >= 0 && typeIn <= 9) || typeIn == 11 || typeIn == 12) { type = typeIn; }
	}

	@Override
	public void setTypeList(int typeIn) {
		if ((typeIn >= 0 && typeIn <= 8) || typeIn == 11 || typeIn == 12) { typeList = typeIn; }
	}

	@Override
	public void setValues(String valuesIn) {
		if (valuesIn.contains("|")) {
			List<String> variants = new ArrayList<>();
			String variant;
			while (valuesIn.contains("|")) {
				variant = checkValue(valuesIn.substring(0, valuesIn.indexOf("|")), type); // check part
				if (variant != null) { variants.add(variant); }
				valuesIn = valuesIn.substring(valuesIn.indexOf("|") + 1);
			}
			variant = checkValue(valuesIn, type); // check value or end part
			if (variant != null) { variants.add(variant); }
			values = variants.toArray(new String[0]);
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
