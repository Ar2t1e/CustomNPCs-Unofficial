package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.ArrayList;
import java.util.List;

public class SubGuiEditBankAccess extends GuiNPCInterface
        implements ICustomScrollListener, ITextfieldListener {

    protected GuiCustomScrollNop scroll;
    protected String sel;

    public final List<String> names;
    public String owner;
    public boolean white;
    public boolean isChanging;

    public SubGuiEditBankAccess(Bank bank) {
        super();
        setBackground("smallbg.png");
        imageWidth = 176;
        imageHeight = 223;

        owner = bank.owner;
        names = new ArrayList<>(bank.access);
        white = bank.isWhiteList;
        isChanging = bank.isChanging;
        sel = "";
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                SubGuiEditText gui = new SubGuiEditText(3, "");
                gui.hovers.put(0, List.of(Component.translatable("hover.player")));
                setSubGui(gui);
                break;
            } // add
            case 1: {
                int i = scroll.getSelectedIndex();
                if (!scroll.hasSelected() || i >= names.size()) { return; }
                if (sel.equals(scroll.getSelected())) { sel = ""; }
                names.remove(i);
                scroll.setSelectedIndex(i - 1);
                if (i == 0) {
                    if (names.isEmpty()) { scroll.setSelectedIndex(-1); }
                    else { scroll.setSelectedIndex(0); }
                }
                init();
                break;
            } // delete
            case 2: {
                white = ((GuiCheckBoxNop) button).selected();
                button.setHoverTexts("bank.hover." + (white ? "iswhite" : "isblack"));
                break;
            } // add
            case 3: {
                isChanging = ((GuiCheckBoxNop) button).selected();
                button.setHoverTexts("bank.hover.changed." + isChanging);
                break;
            } // changed
            case 66: onClose(); break;
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {
        if (subgui instanceof SubGuiEditText gui) {
            String name = gui.text[0];
            if (name.length() < 4 || name.indexOf(' ') != -1
                    || (!Character.isLetter(name.charAt(0)) && name.charAt(0) != '_') || names.contains(name)
                    || owner.equals(name)) {
                return;
            }
            sel = name;
            names.add(sel);
            init();
        }
    }

    @Override
    public void init() {
        super.init();
        int x = guiLeft + 5;
        int y = guiTop + 14;
        // owner
        addLabel(0, x + 2, y - 10, Component.translatable("bank.owner").append(":"));
        addTextField(0, x + 1, y, 164, 18, owner)
                .setHoverTexts("bank.hover.owner");
        // data
        if (scroll == null) { scroll = addScroll(0).setSize(166, 145); }
        add(scroll.setPos(x, y += 22)
                .setList(names));
        if (!sel.isEmpty()) { scroll.setSelectedIndex(sel); }
        else {
            sel = "";
            if (scroll.hasSelected()) { sel = scroll.getSelected(); }
        }
        // white / black list
        addCheckBox(2, x, (y += scroll.height + 24), "bank.iswhite", "bank.isblack", white)
                .setSize(82, 12)
                .setHoverTexts("bank.hover." + (white ? "iswhite" : "isblack"));
        // changed
        addCheckBox(3, x + 86, y, "bank.changed.true", "bank.changed.false", isChanging)
                .setSize(82, 12)
                .setHoverTexts("bank.hover.changed." + isChanging);
        // exit
        addButton(66, x += 1, (y += 14), "gui.back")
                .setSize(54, 20)
                .setHoverTexts("hover.back");
        // add
        addButton(0, x += 56, y, "gui.add")
                .setSize(54, 20)
                .setHoverTexts("bank.hover.player.add");
        // del
        addButton(1, x + 56, y, "gui.remove")
                .setSize(54, 20)
                .setIsEnabled(scroll.hasSelected())
                .setHoverTexts("bank.hover.player.del");
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        sel = scroll.getSelected();
        if (getButton(1) != null) { getButton(1).setIsEnabled(scroll.hasSelected()); }
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (textField.getValue().isEmpty()) {
            owner = textField.getValue();
            return;
        }
        else {
            for (String name : PlayerDataController.instance.getPlayerNames()) {
                if (textField.getValue().equalsIgnoreCase(name)) {
                    owner = name;
                    textField.setValue(name);
                    return;
                }
            }
        }
        textField.setValue(owner);
    }

}
