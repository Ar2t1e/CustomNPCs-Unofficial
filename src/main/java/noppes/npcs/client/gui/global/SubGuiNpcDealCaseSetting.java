package noppes.npcs.client.gui.global;

import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.entity.data.ICustomDrop;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.data.Deal;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import javax.annotation.Nonnull;

public class SubGuiNpcDealCaseSetting extends GuiNPCInterface implements ITextfieldListener {

    protected final Deal deal;

    public SubGuiNpcDealCaseSetting(Deal dealIn) {
        super();
        setBackground("smallbg.png");
        title = Component.translatable("gui.case").append(":");
        closeOnEsc = true;
        imageWidth = 176;
        imageHeight = 222;

        deal = dealIn;
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        switch (button.id) {
            case 0: deal.setShowInCase(((GuiCheckBoxNop) button).selected()); break;
            case 66: onClose(); break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int lId = 0;
        int x = guiLeft + 4;
        int y = guiTop + 19;
        ICustomDrop[] caseItems = deal.getCaseItems();
        // name
        addLabel(lId++, x, y, Component.translatable("gui.name").append(":"))
                .setSize(166, 12);
        addTextField(0, x, y += 12, 166, 12, deal.getCaseName())
                .setHoverTexts(Component.translatable("market.hover.case.name", Component.translatable(deal.getCaseName()).withStyle(TextFormatting.RESET)));
        addLabel(lId++, x, y += 14, Component.translatable("gui.obj").append(":"))
                .setSize(166, 12);
        // obj model
        addTextField(1, x, y += 12, 166, 12, deal.getCaseObjModel())
                .setHoverTexts("market.hover.case.obj");
        // texture
        addLabel(lId++, x, y += 14, Component.translatable("gui.texture").append(":"))
                .setSize(166, 12);
        ResourceLocation texture = deal.getCaseTexture();
        addTextField(2, x, y += 12, 166, 12, texture)
                .setSize(166, 12)
                .setHoverTexts("market.hover.case.texture");
        // sound
        addLabel(lId++, x, y += 14, "market.case.sound")
                .setSize(166, 12);
        addTextField(3, x, y += 12, 166, 12, deal.getCaseSound())
                .setHoverTexts("market.hover.case.sound");
        // command
        addLabel(lId++, x, y += 14, "advMode.command")
                .setSize(166, 12);
        addTextField(4, x, y += 12, 166, 12, deal.getCaseCommand())
                .setHoverTexts("dialog.option.hover.command");
        // count
        addLabel(lId, x, y += 14, "market.case.count")
                .setSize(166, 12);
        addTextField(5, x, y += 12, 83, 12, "" + deal.getCaseCount())
                .setMinMaxDefault(1, caseItems.length - 1, deal.getCaseCount())
                .setHoverTexts("market.hover.case.count");
        // show items in hover
        addCheckBox(0, x, y + 16, "market.case.show.true", "market.case.show.false", deal.showInCase())
                .setSize(166, 12);
        // exit
        addButton(66, x, guiTop + imageHeight - 20, "gui.back")
                .setSize(80, 16)
                .setHoverTexts("hover.back");
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 0: deal.setCaseName(textField.getValue()); break;
            case 1: deal.setCaseObjModel(textField.getValue().isEmpty() ? null : new ResourceLocation(textField.getValue())); break;
            case 2: deal.setCaseTexture(textField.getValue().isEmpty() ? null : new ResourceLocation(textField.getValue())); break;
            case 3: deal.setCaseSound(textField.getValue().isEmpty() ? null : new ResourceLocation(textField.getValue())); break;
            case 4: deal.setCaseCommand(textField.getValue()); break;
            case 5: deal.setCaseCount(textField.getInteger()); break;
        }
        initGui();
    }

}
