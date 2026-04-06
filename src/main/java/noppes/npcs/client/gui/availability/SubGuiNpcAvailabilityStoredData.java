package noppes.npcs.client.gui.availability;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumAvailabilityStoredData;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.AvailabilityStoredData;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcAvailabilityStoredData
        extends GuiNPCInterface
        implements ICustomScrollListener, ITextfieldListener {

    protected final Availability availability;
    protected final Map<Component, AvailabilityStoredData> data = new LinkedHashMap<>();
    protected GuiCustomScrollNop scroll;
    protected AvailabilityStoredData select = null;
    protected int keyError;

    public SubGuiNpcAvailabilityStoredData(Availability availabilityIn) {
        super();
        setBackground("menubg.png");
        imageWidth = 316;
        imageHeight = 217;
        closeOnEsc = true;

        availability = availabilityIn;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                if (select == null) { return; }
                select.type = EnumAvailabilityStoredData.values()[button.getValue()];
                init();
                break;
            }
            case 2: { // remove
                if (select == null) {
                    return;
                }
                availability.storeddata.remove(select);
                select = null;
                init();
                break;
            }
            case 3: { // more
                if (getTextField(0) == null || getTextField(1) == null || getButton(0) == null) {
                    return;
                }
                String key = getTextField(0).getValue();
                int i = 0;
                if (select != null) {
                    while (i < availability.storeddata.size()) {
                        AvailabilityStoredData asd = availability.storeddata.get(i);
                        i++;
                        if (asd == select) {
                            continue;
                        }
                        if (asd.key.equals(key)) {
                            key += "_";
                            i = 0;
                        }
                    }
                    select.key = key;
                    select.value = getTextField(1).getValue();
                    select.type = EnumAvailabilityStoredData.values()[getButton(0).getValue()];
                    select = null;
                } else {
                    while (i < availability.storeddata.size()) {
                        AvailabilityStoredData asd = availability.storeddata.get(i);
                        i++;
                        if (asd.key.equals(key)) {
                            key += "_";
                            i = 0;
                        }
                    }
                    availability.storeddata.add(new AvailabilityStoredData(key, getTextField(1).getValue(), EnumAvailabilityStoredData.values()[getButton(0).getValue()]));
                }
                init();
                break;
            }
            case 66: onClose(); break;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (keyError > 0) {
            keyError--;
            if (getTextField(0) != null) {
                GuiTextFieldNop textField = getTextField(0);
                if (keyError != 0) {
                    textField.setTextColor(0xFFFF0000);
                    textField.setTextColorUneditable(0xFFFF0000);
                } else {
                    textField.setTextColor(0xFFFFFFFF);
                    textField.setTextColorUneditable(0xFFFFFFFF);
                }
            }
        }
        if (getButton(3) != null && getTextField(0) != null) {
            getButton(3).setIsEnabled(!getTextField(0).getValue().isEmpty());
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
        // title
        addLabel(1, guiLeft + 6, guiTop + 4, "availability.available.5")
                .setSize(imageWidth - 12, 12)
                .setCenter(imageWidth - 12);
        // title
        // exit
        int y = guiTop + imageHeight - 46;
        addButton(66, guiLeft + 6, y + 22, "gui.done")
                .setSize(70, 20)
                .setHoverTexts("hover.back");
        // data list
        data.clear();
        Component selKey = Component.empty();
        for (AvailabilityStoredData sd : availability.storeddata) {
            Component type = switch(sd.type) {
                case ONLY -> Component.literal("+").withStyle(ChatFormatting.GREEN);
                case EXCEPT -> Component.literal("-").withStyle(ChatFormatting.RED);
                case SMALLER -> Component.literal("<").withStyle(ChatFormatting.YELLOW);
                case EQUAL -> Component.literal("=").withStyle(ChatFormatting.LIGHT_PURPLE);
                case BIGGER -> Component.literal(">").withStyle(ChatFormatting.AQUA);
            };
            MutableComponent key = Component.empty().append(type)
                    .append(Component.literal("\"").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(sd.key).withStyle(ChatFormatting.RESET))
                    .append(Component.literal("\"").withStyle(ChatFormatting.GOLD));
            if (!sd.value.isEmpty()) {
                key.append(Component.literal(" - ").withStyle(ChatFormatting.RESET))
                        .append(Component.literal("\"").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(sd.value).withStyle(ChatFormatting.RESET))
                        .append(Component.literal("\"").withStyle(ChatFormatting.AQUA));
            }
            data.put(key, sd);
            if (select != null && select.key.equals(sd.key)) {
                select = sd;
                selKey = key;
            }
        }
        if (select != null && selKey.getString().isEmpty()) { select = null; }
        if (scroll == null) { scroll = addScroll(0).setSize(imageWidth - 12, imageHeight - 64); }
        scroll.setNormalList(new ArrayList<>(data.keySet()));
        if (!selKey.getString().isEmpty()) { scroll.setSelected(selKey); }
        else { scroll.setSelect(-1); }
        add(scroll.setPos(guiLeft + 6, guiTop + 14));
        // type
        Object[] enumNames = new Object[EnumAvailabilityStoredData.values().length];
        int i = 0;
        for (EnumAvailabilityStoredData easd : EnumAvailabilityStoredData.values()) { enumNames[i++] = "availability." + easd.name().toLowerCase(); }
        addButton(0, guiLeft + 6, y, false, select == null || select.type == null ? 0 : select.type.ordinal(), enumNames)
                .setSize(50, 20)
                .setHoverTexts("availability.hover.sdtype." + (select == null || select.type == null  ? 0 : select.type.ordinal()));
        addButton(2, guiLeft + 290, y, "X")
                .setSize(20, 20)
                .setIsEnabled(select != null)
                .setHoverTexts("availability.hover.remove");
        // key
        int x = guiLeft + 58;
        addTextField(0, x, y + 1, 112, 18, select != null ? select.key : "")
                .setMaxStringLength(120)
                .setHoverTexts("availability.hover.sd.key");
        // value
        addTextField(1, x + 116, y + 1, 112, 18, select != null ? select.value : "")
                .setMaxStringLength(120)
                .setHoverTexts("availability.hover.sd.value");
        // extra
        addButton(3, guiLeft + imageWidth - 76, y + 22, "availability.more")
                .setSize(70, 20)
                .setHoverTexts("availability.hover.more");
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (!data.containsKey(scroll.getNormalSelected())) { return; }
        select = data.get(scroll.getNormalSelected());
        init();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (textField.id == 0) {
            if (textField.isEmpty()) { return; }
            String key = textField.getValue();
            int i = 0;
            while (i < availability.storeddata.size()) {
                AvailabilityStoredData asd = availability.storeddata.get(i);
                i++;
                if (asd == select) {
                    continue;
                }
                if (asd.key.equals(key)) {
                    key += "_";
                    i = 0;
                }
            }
            if (!textField.getValue().equals(key)) {
                textField.setValue(key);
                keyError = 60;
            }
            if (select != null) {
                select.key = key;
                init();
            }
        }
        else if (select != null) {
            select.value = textField.getValue();
            init();
        }
    }

}
