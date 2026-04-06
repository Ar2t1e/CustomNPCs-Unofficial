package noppes.npcs.client.gui.drop;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.data.EnchantSet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.*;

public class SubGuiDropEnchant extends GuiNPCInterface implements ITextfieldListener {

    protected Object[] enchIds;
    protected int[] levels;
    public EnchantSet enchant;

    public SubGuiDropEnchant(EnchantSet enchantIn) {
        super();
        setBackground("companion_empty.png");
        closeOnEsc = true;
        imageWidth = 172;
        imageHeight = 167;

        enchant = enchantIn;
        List<Integer> el = new ArrayList<>();
        for (Map.Entry<ResourceKey<Enchantment>, Enchantment> entry : BuiltInRegistries.ENCHANTMENT.entrySet()) {
            el.add(BuiltInRegistries.ENCHANTMENT.getId(entry.getValue()));
        }
        Collections.sort(el);
        enchIds = new Object[el.size()];
        for (int i = 0; i < el.size(); i++) { enchIds[i] = el.get(i); }
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        switch (button.id) {
            case 50: enchant.setEnchant(Integer.parseInt(button.getVariants()[button.getValue()].getString())); init(); break;
            case 66: onClose(); break;
        }
    }

    @Override
    public void init() {
        super.init();
        int lId = 60;
        // name
        addLabel(lId++, guiLeft + 4, guiTop + 5, Component.translatable("drop.enchants").append(": ")
                .append(Component.translatable(enchant.getEnchant())));
        // select
        int posId = 0;
        int idName = BuiltInRegistries.ENCHANTMENT.getId(enchant.ench);
        for (int i = 0; i < enchIds.length; i++) {
            if ((int) enchIds[i] == idName) { posId = i; }
        }
        addButton(50, guiLeft + 4, guiTop + 17, true, posId, enchIds)
                .setSize(80, 20)
                .setHoverTexts("drop.hover.enchant.list");
        // levels
        levels = new int[] { enchant.getMinLevel(), enchant.getMaxLevel() };
        addLabel(lId++, guiLeft + 56, guiTop + 48, "type.level");
        String tied = Util.instance.getOldFormattedText(Component.translatable("drop.tied.random"));
        if (enchant.parent.tiedToLevel) { tied = Util.instance.getOldFormattedText(Component.translatable("drop.tied.level")); }
        // min
        addTextField(52, guiLeft + 4, guiTop + 39, 50, 14, "" + levels[0])
                .setMinMaxDefault(0, 100000, enchant.getMinLevel())
                .setHoverTexts(Component.translatable("drop.hover.enchant.levels", "" + enchant.ench.getMaxLevel(), tied));
        // max
        addTextField(53, guiLeft + 4, guiTop + 53, 50, 14, "" + levels[1])
                .setMinMaxDefault(0, 100000, enchant.getMaxLevel())
                .setHoverTexts(Component.translatable("drop.hover.enchant.levels", "" + enchant.ench.getMaxLevel(), tied));
        // chance
        addLabel(lId, guiLeft + 56, guiTop + 74, "drop.chance");
        addTextField(54, guiLeft + 4, guiTop + 69, 50, 20, String.valueOf(enchant.getChance()))
                .setMinMaxDefault(0.0001d, 100.0d, enchant.getChance())
                .setHoverTexts("drop.hover.enchant.chance");
        // done
        addButton(66, guiLeft + 4, guiTop + 142, "gui.done")
                .setSize(80, 20)
                .setHoverTexts("hover.back");
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 52: levels[0] = textField.getInteger(); enchant.setLevels(levels[0], levels[1]); init(); break; // level min
            case 53: levels[1] = textField.getInteger(); enchant.setLevels(levels[0], levels[1]); init(); break; // level max
            case 54: enchant.setChance(textField.getDouble()); init(); break; // chance
        }
    }

}
