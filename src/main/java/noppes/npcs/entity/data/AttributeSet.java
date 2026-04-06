package noppes.npcs.entity.data;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.IAttributeSet;
import noppes.npcs.util.ValueUtil;

public class AttributeSet implements IAttributeSet {

	public IAttribute attr;
	public double chance;
	public DropSet parent;
	public int slot; // -1:ALL, 0:MAINHAND, 1:OFFHAND, 2:FEET, 3:LEGS, 4:CHEST, 5:HEAD
	public double[] values;

	public AttributeSet(DropSet p) {
		parent = p;
		values = new double[] { 0.0d, 0.05d };
		attr = SharedMonsterAttributes.MAX_HEALTH;
		chance = 100.0d;
		slot = 0;
	}

	@Override
	public String getAttribute() { return attr.getName(); }

	@Override
	public double getChance() { return Math.round(chance * 10000.0d) / 10000.0d; }

	public Component getKey() {
		Component keyName = Component.empty();
		double ch = Math.round(chance * 10.0d) / 10.d;
		String chance = String.valueOf(ch).replace(".", ",");
		if (ch == (int) ch) { chance = String.valueOf((int) ch); }
		chance += "%";
		keyName.append(Component.literal(chance).withStyle(TextFormatting.YELLOW));
		double v0 = Math.round(values[0] * 1000.0d) / 1000.d;
		String tv0 = String.valueOf(v0).replace(".", ",");
		if (v0 == (int) v0) { tv0 = String.valueOf((int) v0); }
		double v1 = Math.round(values[1] * 1000.0d) / 1000.d;
		String tv1 = String.valueOf(v1).replace(".", ",");
		if (v1 == (int) v1) { tv1 = String.valueOf((int) v1); }
		if (values[0] == values[1]) {
			keyName.append(Component.literal("[").withStyle(TextFormatting.GRAY))
					.append(Component.literal(tv0).withStyle(TextFormatting.GOLD))
					.append(Component.literal("]").withStyle(TextFormatting.GRAY));
		} else {
			keyName.append(Component.literal("[").withStyle(TextFormatting.GRAY))
					.append(Component.literal(tv0).withStyle(TextFormatting.GOLD))
					.append(Component.literal("-").withStyle(TextFormatting.GRAY)
							.append(Component.literal(tv1).withStyle(TextFormatting.GOLD))
							.append(Component.literal("]").withStyle(TextFormatting.GRAY)));
		}
		Component name = Component.translatable("attribute.name." + attr.getName());
		if (name.getString().equals("attribute.name." + attr.getName()) ||
				name.getString().equals("attribute.name.")) { name = Component.literal(attr.getName()); }
		return keyName.append(name.withStyle(TextFormatting.RESET));
		//.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public double getMaxValue() { return values[1]; }

	@Override
	public double getMinValue() { return values[0]; }

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtAS = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagDouble(values[0]));
		list.appendTag(new NBTTagDouble(values[1]));
		nbtAS.setTag("Values", list);
		nbtAS.setString("Name", attr.getName());
		nbtAS.setDouble("Chance", chance);
		nbtAS.setInteger("Slot", slot);
		return nbtAS;
	}

	@Override
	public int getSlot() { return slot; }

	public void load(NBTTagCompound nbtAS) {
		double[] newVs = new double[2];
		for (int i = 0; i < 2; i++) { newVs[i] = nbtAS.getTagList("Values", 6).getDoubleAt(i); }
		values = newVs;
		setAttribute(nbtAS.getString("Name"));
		chance = nbtAS.getDouble("Chance");
		slot = nbtAS.getInteger("Slot");
	}

	@Override
	public void remove() { parent.removeAttribute(this); }

	public void setAttribute(IAttribute iattribute) { attr = iattribute; }

	@Override
	public void setAttribute(String name) {
        switch (name) {
            case "generic.maxHealth":
                attr = SharedMonsterAttributes.MAX_HEALTH;
                break;
            case "generic.followRange":
                attr = SharedMonsterAttributes.FOLLOW_RANGE;
                break;
            case "generic.knockbackResistance":
                attr = SharedMonsterAttributes.KNOCKBACK_RESISTANCE;
                break;
            case "generic.movementSpeed":
                attr = SharedMonsterAttributes.MOVEMENT_SPEED;
                break;
            case "generic.attackDamage":
                attr = SharedMonsterAttributes.ATTACK_DAMAGE;
                break;
            case "generic.attackSpeed":
                attr = SharedMonsterAttributes.ATTACK_SPEED;
                break;
            case "generic.armor":
                attr = SharedMonsterAttributes.ARMOR;
                break;
            case "generic.luck":
                attr = SharedMonsterAttributes.LUCK;
                break;
            default:  // create
                attr = (new RangedAttribute(null, name, 0.0D, -1024.0D, 1024.0D)).setShouldWatch(true);
                break;
        }
	}

	@Override
	public void setChance(double chanceIn) {
		double newChance = ValueUtil.correctDouble(chanceIn, 0.0001d, 100.0d);
		chance = Math.round(newChance * 10000.0d) / 10000.0d;
	}

	@Override
	public void setSlot(int slotIn) {
		if (slotIn < -1 || slotIn > 5) {
			throw new CustomNPCsException("Slot has to be between -1 and 5, given was: " + slotIn);
		}
		slot = slotIn;
	}

	@Override
	public void setValues(double min, double max) {
		double newMin = min;
		double newMax = max;
		if (min > max) {
			newMin = max;
			newMax = min;
		}
		values = new double[] { newMin, newMax };
	}

}
