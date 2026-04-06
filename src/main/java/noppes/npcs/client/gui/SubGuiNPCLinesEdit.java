package noppes.npcs.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SubGuiNPCLinesEdit extends GuiNPCInterface
        implements ICustomScrollListener, ITextfieldListener {

    protected final Map<Component, Integer> data = new LinkedHashMap<>();
    protected GuiCustomScrollNop scroll;
    protected Component select = Component.empty();
    public final int id;
    public Lines lines;

    public SubGuiNPCLinesEdit(int idIn, EntityNPCInterface npc, Lines linesIn, String titleIn) {
        super(npc);
        setBackground("menubg.png");
        imageWidth = 256;
        imageHeight = 217;
        closeOnEsc = true;
        id = idIn;

        lines = linesIn.copy();
        title = titleIn == null ? Component.empty() : Component.translatable(titleIn);
        Packets.sendServer(new SPacketMenuGet(EnumMenuType.ADVANCED));
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        if (select.getString().isEmpty() && scroll.hasSelected()) { select = scroll.getNormalSelected(); }
        switch (button.id) {
            case 0: setSubGui(new SubGuiEditText(0, CustomNpcs.DefaultInteractLine)); break; // add
            case 1: {
                if (!data.containsKey(select)) { return; }
                lines.remove(data.get(select));
                if (scroll != null && scroll.hasSelected()) { scroll.setSelect(scroll.getSelectedIndex() - 1); }
                init();
                break;
            } // remove
            case 2: {
                if (!data.containsKey(select) || !lines.lines.containsKey(data.get(select))) {
                    setSubGui(new SubGuiEditText(0, CustomNpcs.DefaultInteractLine)); // add
                    return;
                }
                setSubGui(new SubGuiSoundSelection(this, 0, npc, lines.lines.get(data.get(select)).getSound()));
                break;
            } // sel sound
            case 66: onClose(); break;
        }
    }

    @Override
    public void init() {
        super.init();
        data.clear();
        int p = 0;
        Component t = Component.empty()
                .append(Component.translatable("parameter.position").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
        Component m = Component.empty().append(Component.translatable("parameter.iline.text").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(":").withStyle(ChatFormatting.GRAY));
        Component s = Component.empty().append(Component.translatable("parameter.sound.name").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(":").withStyle(ChatFormatting.GRAY));
        List<Component> suffixes = new ArrayList<>();
        LinkedHashMap<Integer, List<Component>> hts= new LinkedHashMap<>();
        for (int i : lines.lines.keySet()) {
            Line l = lines.lines.get(i);
            data.put(Component.empty()
                    .append(Component.literal(i + ": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(l.getText()).withStyle(ChatFormatting.RESET)), i);
            List<Component> hover = new ArrayList<>();
            hover.add(t.copy().append(Component.literal("" + i).withStyle(ChatFormatting.WHITE)));
            hover.add(m);
            hover.add(Component.literal(l.getText()));
            if (!l.getSound().isEmpty()) {
                hover.add(s);
                hover.add(Component.literal(l.getSound()));
                suffixes.add(Component.empty()
                        .append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("S").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("]").withStyle(ChatFormatting.GRAY)));
            } else {
                suffixes.add(Component.empty());
            }
            hts.put(p, hover);
        }
        if (scroll == null) { scroll = addScroll(0).setSize(imageWidth - 12, imageHeight - 85); }
        List<Component> list = new ArrayList<>(data.keySet());
        Line line = null;
        if (!select.getString().isEmpty()) {
            boolean hasInList = false;
            for (Component c : list) {
                if (Util.instance.deleteColor(c.getString()).equals(Util.instance.deleteColor(select.getString()))) {
                    select = c;
                    line = lines.lines.get(data.get(select));
                    scroll.setSelected(select);
                    hasInList = true;
                }
            }
            if (!hasInList) { select = Component.empty(); }
        }
        scroll.setUnsortedList(list)
                .setSuffixes(suffixes).setHoverTexts(hts);
        add(scroll.setPos(guiLeft + 6, guiTop + 14));
        // title
        int lId = 0;
        addLabel(lId++, guiLeft, guiTop + 4, title).setCenter(imageWidth);
        // text
        int x = guiLeft + 6;
        int y = guiTop + scroll.height + 38;
        // text
        addLabel(lId++, x, y + 5, Component.translatable("gui.message").append(":"))
                .setSize(60, 10);
        addTextField(0, x + 63, y + 1, 180, 18, line == null ? "" : line.getText())
                .setHoverTexts("lines.hover.text");
        // sound
        addLabel(lId, x, (y += 22) + 5, Component.translatable("advanced.sounds").append(":"))
                .setSize(54, 10);
        addTextField(1, x + 37, y + 1, 155, 18, line == null ? "" : line.getSound())
                .setHoverTexts("lines.hover.sound");
        addButton(2, guiLeft + imageWidth - 55, y, "availability.select")
                .setSize(50, 20)
                .setHoverTexts("bard.hover.select");
        // select
        addButton(0, guiLeft + imageWidth - 107, y += 22, "gui.add")
                .setSize(80, 20)
                .setHoverTexts("lines.hover.add");
        addButton(1, guiLeft + imageWidth - 25, y, "X")
                .setSize(20, 20)
                .setHoverTexts("lines.hover.remove");
        // back
        addButton(66, x, y, "gui.done")
                .setSize(50, 20)
                .setHoverTexts("hover.back");
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (!data.containsKey(scroll.getNormalSelected())) { return; }
        select = scroll.getNormalSelected();
        init();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    @Override
    public void subGuiClosed(Screen subgui) {
        if (subgui instanceof SubGuiEditText gui) {
            if (gui.cancelled || gui.text[0].isEmpty()) { return; }
            Line line = new Line(gui.text[0]);
            lines.correctLines();
            int p = lines.lines.size();
            lines.lines.put(p, line);
            select = Component.empty()
                    .append(Component.literal(p + ": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(line.getText()).withStyle(ChatFormatting.RESET));
            init();
        }
        else if (subgui instanceof SubGuiSoundSelection gui) {
            if (!data.containsKey(select)) { return; }
            if (gui.resource == null || !data.containsKey(select) || !lines.lines.containsKey(data.get(select))) { return; }
            lines.lines.get(data.get(select)).setSound(gui.resource.toString());
            init();
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (!hasSubGui() && textField.id == 0) {
            if (!data.containsKey(select) || !lines.lines.containsKey(data.get(select))) { return; }
            lines.lines.get(data.get(select)).setText(textField.getValue());
            select = Component.literal(textField.getValue());
            init();
        }
    }

}
