package noppes.npcs.client.gui.global;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SubGuiDataSend extends GuiNPCInterface implements ITextfieldListener {

    public boolean cancelled = true;
    public int day = -1;
    public int month = -1;
    public int year = -1;
    public long time;

    public SubGuiDataSend() {
        super();
        setBackground("smallbg.png");
        imageWidth = 176;
        imageHeight = 71;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        if (button.id == 0) { cancelled = false; }
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(guiLeft, guiTop, 0.0f);
        matrixStack.scale(bgScale, bgScale, bgScale);
        RenderSystem.setShaderColor(2.0F, 2.0F, 2.0F, 1.0F);
        graphics.blit(background, 0, imageHeight - 1, 0, 218, imageWidth, 4);
        matrixStack.popPose();
    }

    @Override
    public void init() {
        super.init();
        Calendar cal = Calendar.getInstance();
        if (year == -1) { year = cal.get(Calendar.YEAR); }
        if (month == -1) { month = cal.get(Calendar.MONTH); }
        if (day == -1) {
            day = cal.get(Calendar.DAY_OF_MONTH);
            if (day < 5) {
                month--;
                if (month < 0) {
                    month = 11;
                    year--;
                }
            }
            day = 1;
        }
        if (year < 2011) { year = 2011; }
        if (year == 2011 && month < 10) { month = 10; }
        if (year == 2011 && month == 11 && day < 18) { day = 18; }
        GregorianCalendar setCal = new GregorianCalendar(year, month, day);
        time = setCal.getTimeInMillis();
        addLabel(0, guiLeft + 7, guiTop + 4, "gui.setdata");
        int min = 2011;
        int max = setCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        addTextField(0, guiLeft + 4, guiTop + 16, 54, 20, "" + day)
                .setMinMaxDefault(year == min && month == 11 ? 18 : 1, max, day)
                .setHoverTexts(Component.translatable("hover.data.day", "" + max));
        max = (year == cal.get(Calendar.YEAR) ? cal.get(Calendar.MONTH) + 1 : 12);
        addTextField(1, guiLeft + 61, guiTop + 16, 54, 20, "" + (month + 1))
                .setMinMaxDefault(year == min ? 11 : 1, max, (month + 1))
                .setHoverTexts(Component.translatable("hover.data.month", "" + max, Component.translatable("month." + month).getString()));
        max = cal.get(Calendar.YEAR);
        addTextField(2, guiLeft + 118, guiTop + 16, 54, 20, "" + year)
                .setMinMaxDefault(min, max, year)
                .setHoverTexts(Component.translatable("hover.data.year", "" + min, "" + max));
        addButton(0, guiLeft + 4, guiTop + 44, "gui.done")
                .setSize(80, 20)
                .setHoverTexts("hover.back");
        addButton(1, guiLeft + 90, guiTop + 44, "gui.cancel")
                .setSize(80, 20);
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 0: day = textField.getInteger(); break;
            case 1: month = textField.getInteger() - 1; break;
            case 2: year = textField.getInteger(); break;
        }
        init();
    }

}
