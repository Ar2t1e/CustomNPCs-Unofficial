package noppes.npcs.entity.data;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.IAttributeSet;
import noppes.npcs.util.ValueUtil;

public class AttributeSet implements IAttributeSet {

    public Attribute attr;
    public double chance;
    public DropSet parent;
    public int slot; // -1:ALL, 0:MAINHAND, 1:OFFHAND, 2:FEET, 3:LEGS, 4:CHEST, 5:HEAD
    public double[] values;

    public AttributeSet(DropSet p) {
        parent = p;
        values = new double[] { 0.0d, 0.05d };
        attr = new RangedAttribute(Attributes.MAX_HEALTH.getDescriptionId(), Attributes.MAX_HEALTH.getDefaultValue(),-1024.0d, 1024.0d);
        chance = 100.0d;
        slot = 0;
    }

    @Override
    public String getAttribute() { return attr.getDescriptionId(); }

    @Override
    public double getChance() {
        return Math.round(chance * 10000.0d) / 10000.0d;
    }

    public Component getKey() {
        MutableComponent keyName = Component.empty();
        double ch = Math.round(chance * 10.0d) / 10.d;
        String chance = String.valueOf(ch).replace(".", ",");
        if (ch == (int) ch) { chance = String.valueOf((int) ch); }
        chance += "%";
        keyName.append(Component.literal(chance).withStyle(ChatFormatting.YELLOW));
        double v0 = Math.round(values[0] * 1000.0d) / 1000.d;
        String tv0 = String.valueOf(v0).replace(".", ",");
        if (v0 == (int) v0) { tv0 = String.valueOf((int) v0); }
        double v1 = Math.round(values[1] * 1000.0d) / 1000.d;
        String tv1 = String.valueOf(v1).replace(".", ",");
        if (v1 == (int) v1) { tv1 = String.valueOf((int) v1); }
        if (values[0] == values[1]) {
            keyName.append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(tv0).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("]").withStyle(ChatFormatting.GRAY));
        } else {
            keyName.append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(tv0).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("-").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(tv1).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("]").withStyle(ChatFormatting.GRAY)));
        }
        MutableComponent name = Component.translatable("attribute.name." + attr.getDescriptionId());
        if (name.getString().equals("attribute.name." + attr.getDescriptionId()) ||
                name.getString().equals("attribute.name.")) { name = Component.literal(attr.getDescriptionId()); }
        return keyName.append(name.withStyle(ChatFormatting.RESET));
                //.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public double getMaxValue() { return values[1]; }

    @Override
    public double getMinValue() { return values[0]; }

    public CompoundTag getNBT() {
        CompoundTag nbtAS = new CompoundTag();
        ListTag list = new ListTag();
        list.add(DoubleTag.valueOf(values[0]));
        list.add(DoubleTag.valueOf(values[1]));
        nbtAS.put("Values", list);
        nbtAS.putString("Name", attr.getDescriptionId());
        nbtAS.putDouble("Chance", chance);
        nbtAS.putInt("Slot", slot);
        return nbtAS;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public void load(CompoundTag nbtAS) {
        double[] newVs = new double[2];
        for (int i = 0; i < 2; i++) { newVs[i] = nbtAS.getList("Values", 6).getDouble(i); }
        values = newVs;
        setAttribute(nbtAS.getString("Name"));
        chance = nbtAS.getDouble("Chance");
        slot = nbtAS.getInt("Slot");
    }

    @Override
    public void remove() { parent.removeAttribute(this); }

    @Override
    public void setAttribute(Attribute attribute) { attr = attribute; }

    @Override
    public void setAttribute(String name) {
        attr = switch (name) {
            case "generic.maxHealth" -> Attributes.MAX_HEALTH;
            case "generic.followRange" -> Attributes.FOLLOW_RANGE;
            case "generic.knockbackResistance" -> Attributes.KNOCKBACK_RESISTANCE;
            case "generic.movementSpeed" -> Attributes.MOVEMENT_SPEED;
            case "generic.attackDamage" -> Attributes.ATTACK_DAMAGE;
            case "generic.attackSpeed" -> Attributes.ATTACK_SPEED;
            case "generic.armor" -> Attributes.ARMOR;
            case "generic.luck" -> Attributes.LUCK;
            default -> new RangedAttribute(name, 0.0D, -1024.0D, 1024.0D);
        };
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
