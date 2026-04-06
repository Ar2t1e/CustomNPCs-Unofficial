package noppes.npcs.entity.data;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import noppes.npcs.api.entity.data.IEnchantSet;
import noppes.npcs.util.ValueUtil;

public class EnchantSet implements IEnchantSet {

    public double chance = 100.0d;
    public Enchantment ench = Enchantment.byId(0);
    public int[] lvls = new int[] { 0, 1 };
    public DropSet parent;

    public EnchantSet(DropSet p) { parent = p; }

    @Override
    public double getChance() {
        return Math.round(chance * 10000.0d) / 10000.0d;
    }

    @Override
    public String getEnchant() { return ench.getDescriptionId(); }

    public Component getKey() {
        MutableComponent keyName = Component.empty();
        double ch = Math.round(chance * 10.0d) / 10.d;
        String chance = String.valueOf(ch).replace(".", ",");
        if (ch == (int) ch) { chance = String.valueOf((int) ch); }
        chance += "%";
        keyName.append(Component.literal(chance).withStyle(ChatFormatting.YELLOW));
        if (lvls[0] == lvls[1]) {
            keyName.append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + lvls[0]).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("]").withStyle(ChatFormatting.GRAY));
        } else {
            keyName.append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("" + lvls[0]).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("-").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("" + lvls[1]).withStyle(ChatFormatting.GOLD))
                            .append(Component.literal("]").withStyle(ChatFormatting.GRAY)));
        }
        return keyName.append(Component.literal("ID:" + BuiltInRegistries.ENCHANTMENT.getId(ench)).withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(ench.getDescriptionId()).withStyle(ChatFormatting.RESET));
                //.append(Component.literal(" #" + toString().substring(toString().indexOf("@") + 1)).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public int getMaxLevel() {
        return lvls[1];
    }

    @Override
    public int getMinLevel() {
        return lvls[0];
    }

    @SuppressWarnings("all")
    public CompoundTag getNBT() {
        CompoundTag nbtES = new CompoundTag();
        ListTag list = new ListTag();
        list.add(IntTag.valueOf(lvls[0]));
        list.add(IntTag.valueOf(lvls[1]));
        nbtES.put("Levels", list);
        nbtES.putInt("ID", BuiltInRegistries.ENCHANTMENT.getId(ench));
        nbtES.putDouble("Chance", chance);
        return nbtES;
    }

    public void load(CompoundTag nbtES) {
        int[] newLv = new int[2];
        for (int i = 0; i < 2; i++) { newLv[i] = nbtES.getList("Levels", 3).getInt(i); }
        lvls = newLv;
        ench = Enchantment.byId(nbtES.getInt("ID"));
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
        if (enchant == null) { parent.removeEnchant(this); return; }
        ench = enchant;
    }

    @Override
    public void setEnchant(int id) {
        Enchantment newEnch = Enchantment.byId(id);
        if (newEnch != null) {
            ench = newEnch;
        }
    }

    @SuppressWarnings("all")
    @Override
    public boolean setEnchant(String name) {
        Enchantment newEnch = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(name));
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
