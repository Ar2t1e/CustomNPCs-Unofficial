package noppes.npcs.client.gui.elements;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiEditProperty extends GuiBasic implements ITextfieldListener {

    protected NBTTagCompound nbtData;
    protected final NBTTagCompound firstProp;
    protected byte type;
    protected int typeIndex;

    protected static final Object[] PROP_TYPES = {
            Component.translatable("ai.tactic.none"), // 0
            Component.translatable("element.property.type.boolean"),   // 1
            Component.translatable("element.property.type.integer"),   // 3
            Component.translatable("element.property.type.direction")  // 4
    };

    public SubGuiEditProperty(NBTTagCompound parentNbt) {
        super();
        setBackground("smallbg.png");
        closeOnEsc = true;
        imageWidth = 200;
        imageHeight = 110;
        title = Component.translatable("element.edit.property.title");

        nbtData = parentNbt;
        firstProp = parentNbt.getCompoundTag("Property").copy();

        type = nbtData.getCompoundTag("Property").hasKey("Type") ? nbtData.getCompoundTag("Property").getByte("Type") : (byte) 0;
        // Map 1->0, 3->1, 4->2 for the dropdown
        typeIndex = type == 1 ? 1 : type == 3 ? 2 : type == 4 ? 3 : 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x0 = guiLeft + 5;
        int x1 = x0 + 76;
        int y = guiTop + 17;

        String name = nbtData.getCompoundTag("Property").hasKey("Name") ? nbtData.getCompoundTag("Property").getString("Name") : type == 4 ? "facing" : "value";
        int min = nbtData.getCompoundTag("Property").hasKey("Min") ? nbtData.getCompoundTag("Property").getInteger("Min") : 0;
        int max = nbtData.getCompoundTag("Property").hasKey("Max") ? nbtData.getCompoundTag("Property").getInteger("Max") : 3;

        addLabel(0, x0, y + 2, "element.property.type")
                .setSize(74, 10);
        addButton(0, x1, y, true, typeIndex, PROP_TYPES)
                .setSize(114, 16)
                .setHoverTexts("element.hover.property.type");

        addLabel(1, x0, (y += 18) + 2, "element.property.name")
                .setSize(74, 10)
                .setIsVisible(typeIndex > 0);
        addTextField(0, x1 + 1, y + 1, 112, 14, name)
                .setMaxStringLength(16)
                .setResourceLocationType(2)
                .setIsVisible(typeIndex > 0)
                .setHoverTexts("element.hover.registry.name");
        // int min
        addLabel(2, x0, (y += 18) + 2, "gui.min")
                .setSize(74, 10)
                .setIsVisible(typeIndex == 2);
        addTextField(1, x1 + 1, y + 1, 112, 14, min)
                .setMinMaxDefault(0, 64, min)
                .setIsVisible(typeIndex == 2)
                .setHoverTexts("element.hover.property.min");
        // int max
        addLabel(3, x0, (y += 18) + 2, "gui.max")
                .setSize(74, 10)
                .setIsVisible(typeIndex == 2);
        addTextField(2, x1 + 1, y + 1, 112, 14, max)
                .setMinMaxDefault(0, 64, max)
                .setIsVisible(typeIndex == 2)
                .setHoverTexts("element.hover.property.max");
        // done
        y = guiTop + imageHeight - 21;
        addButton(1, x0, y, "gui.done")
                .setSize(60, 16)
                .setHoverTexts("element.hover.property.done");
        addButton(2, guiLeft + imageWidth / 2 - 30, y, "gui.clear")
                .setSize(60, 16)
                .setHoverTexts("element.hover.property.clear");
        addButton(66, guiLeft + imageWidth - 65, y, "gui.cancel")
                .setSize(60, 16)
                .setHoverTexts("hover.exit");
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                typeIndex = button.getValue();
                type = (byte) (typeIndex == 1 ? 1 : typeIndex == 2 ? 3 : typeIndex == 3 ? (byte) 4 : (byte) 0);
                if (type == 0) {
                    nbtData.removeTag("Property");
                }
                else {
                    nbtData.getCompoundTag("Property").setByte("Type", type);
                    if (type != 3) {
                        nbtData.getCompoundTag("Property").removeTag("Min");
                        nbtData.getCompoundTag("Property").removeTag("Max");
                    }
                    else {
                        nbtData.getCompoundTag("Property").setInteger("Min", 0);
                        nbtData.getCompoundTag("Property").setInteger("Max", 3);
                    }
                }
                initGui();
                break;
            } // change type
            case 1: onClose(); break; // done
            case 2: {
                nbtData.removeTag("Property");
                typeIndex = 0;
                type = (byte) 0;
                initGui();
                break;
            } // clear
            case 66: {
                if (firstProp.hasNoTags()) { nbtData.removeTag("Property"); } else { nbtData.setTag("Property", firstProp); }
                onClose();
                break;
            } // cancel
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 0: { nbtData.getCompoundTag("Property").setString("Name", textField.getValue()); } // name
            case 1: { nbtData.getCompoundTag("Property").setString("Min", textField.getValue()); } // int min
            case 2: { nbtData.getCompoundTag("Property").setString("Max", textField.getValue()); } // int max
        }
    }

}
