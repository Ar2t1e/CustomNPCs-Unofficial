package noppes.npcs.entity.data;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.entity.data.IEnchantSet;
import noppes.npcs.util.ValueUtil;

public class EnchantSet implements IEnchantSet {

	public double chance;
	public Enchantment ench;
	public int[] lvls;
	public DropSet parent;

	public EnchantSet(DropSet p) {
		parent = p;
		lvls = new int[] { 0, 1 };
		ench = Enchantment.getEnchantmentByID(0);
		chance = 100.0d;
	}

	@Override
	public double getChance() { return Math.round(chance * 10000.0d) / 10000.0d; }

	@Override
	public String getEnchant() { return ench.getName(); }

	public Component getKey() {
		Component keyName = Component.empty();
		double ch = Math.round(chance * 10.0d) / 10.d;
		String chance = String.valueOf(ch).replace(".", ",");
		if (ch == (int) ch) { chance = String.valueOf((int) ch); }
		chance += "%";
		keyName.append(Component.literal(chance).withStyle(TextFormatting.YELLOW));
		if (lvls[0] == lvls[1]) {
			keyName.append(Component.literal("[").withStyle(TextFormatting.GRAY))
					.append(Component.literal("" + lvls[0]).withStyle(TextFormatting.GOLD))
					.append(Component.literal("]").withStyle(TextFormatting.GRAY));
		} else {
			keyName.append(Component.literal("[").withStyle(TextFormatting.GRAY))
					.append(Component.literal("" + lvls[0]).withStyle(TextFormatting.GOLD))
					.append(Component.literal("-").withStyle(TextFormatting.GRAY)
							.append(Component.literal("" + lvls[1]).withStyle(TextFormatting.GOLD))
							.append(Component.literal("]").withStyle(TextFormatting.GRAY)));
		}
		return keyName.append(Component.literal("ID:" + Enchantment.getEnchantmentID(ench)).withStyle(TextFormatting.GRAY))
				.append(Component.translatable(ench.getName()).withStyle(TextFormatting.RESET));
		//.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public int getMaxLevel() { return lvls[1]; }

	@Override
	public int getMinLevel() { return lvls[0]; }

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtES = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagInt(lvls[0]));
		list.appendTag(new NBTTagInt(lvls[1]));
		nbtES.setTag("Levels", list);
		nbtES.setInteger("ID", Enchantment.getEnchantmentID(ench));
		nbtES.setDouble("Chance", chance);
		return nbtES;
	}

	public void load(NBTTagCompound nbtES) {
		int[] newLv = new int[2];
		for (int i = 0; i < 2; i++) { newLv[i] = nbtES.getTagList("Levels", 3).getIntAt(i); }
		lvls = newLv;
		ench = Enchantment.getEnchantmentByID(nbtES.getInteger("ID"));
		chance = nbtES.getDouble("Chance");
	}

	@Override
	public void remove() { parent.removeEnchant(this); }

	@Override
	public void setChance(double chanceIn) {
		double newChance = ValueUtil.correctDouble(chanceIn, 0.0001d, 100.0d);
		chance = Math.round(newChance * 10000.0d) / 10000.0d;
	}

	@Override
	public void setEnchant(Enchantment enchant) {
		if (enchant == null) {
			parent.removeEnchant(this);
			return;
		}
		ench = enchant;
	}

	@Override
	public boolean setEnchant(int id) {
		Enchantment newEnch = Enchantment.getEnchantmentByID(id);
		if (newEnch != null) {
			ench = newEnch;
			return true;
		}
		return false;
	}

	@Override
	public boolean setEnchant(String name) {
		Enchantment newEnch = Enchantment.getEnchantmentByLocation(name);
		if (newEnch != null) {
			ench = newEnch;
			return true;
		}
		return false;
	}

	@Override
	public void setLevels(int min, int max) {
		int newMin = min;
		int newMax = max;
		if (min > max) {
			newMin = max;
			newMax = min;
		}
		lvls = new int[] { newMin, newMax };
	}

}
