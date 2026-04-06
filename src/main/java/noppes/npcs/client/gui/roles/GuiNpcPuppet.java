package noppes.npcs.client.gui.roles;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobPuppet;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

public class GuiNpcPuppet extends GuiNPCInterface implements ISliderListener, ICustomScrollListener {

    protected final GuiScreen parent;
    protected final JobPuppet job;
    protected boolean isStart = true;
    protected GuiCustomScrollNop scroll;
    protected HashMap<Component, JobPuppet.PartConfig> data = new HashMap<>();
    protected Component selected = Component.empty();

    public GuiNpcPuppet(GuiScreen parentIn, EntityCustomNpc npc) {
        super(npc);
        imageHeight = 230;
        imageWidth = 400;

        parent = parentIn;
        job = (JobPuppet)npc.job;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 29: data.get(selected).disabled = button.getValue() == 1; break;
            case 30: job.whileStanding = ((GuiButtonYesNo) button).getBoolean(); break;
            case 31: job.whileMoving = ((GuiButtonYesNo) button).getBoolean(); break;
            case 32: job.whileAttacking = ((GuiButtonYesNo) button).getBoolean(); break;
            case 33: {
                job.animate = ((GuiButtonYesNo) button).getBoolean();
                isStart = true;
                initGui();
                break;
            } // is animate
            case 34: job.animationSpeed = button.getValue(); break;
            case 66: onClose(); break;
            case 67: isStart = true; initGui(); break;
            case 68: isStart = false; initGui(); break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int lId = 0;
        int x0 = guiLeft + 10;
        int x1 = x0 + 100;
        int y = guiTop + 14;
        addLabel(lId++, x0, y + 5, "puppet.standing")
                .setSize(98, 20)
                .setColor(CustomNpcs.MainColor.getRGB());
        addYesNo(30, x1, y, job.whileStanding)
                .setSize(60, 20);
        addLabel(lId++, x0, (y += 22) + 5, "puppet.walking")
                .setSize(98, 20)
                .setColor(CustomNpcs.MainColor.getRGB());
        addYesNo(31, x1, y, job.whileMoving)
                .setSize(60, 20);
        addLabel(lId++, x0, (y += 22)  + 5, "puppet.attacking")
                .setSize(98, 20)
                .setColor(CustomNpcs.MainColor.getRGB());
        addYesNo(32, x1, y, job.whileAttacking)
                .setSize(60, 20);
        addLabel(lId++, x0, (y += 22)  + 5, "puppet.animation")
                .setSize(98, 20)
                .setColor(CustomNpcs.MainColor.getRGB());
        addYesNo(33, x1, y, job.animate)
                .setSize(60, 20);
        if (job.animate) {
            Object[] numbs = new Object[8];
            for (int i = 1; i < 9; i++) { numbs[i - 1] = i; }
            addLabel(lId, x1 + 70, y + 5, Component.translatable("stats.speed").append(":"))
                    .setSize(58, 20)
                    .setColor(CustomNpcs.MainColor.getRGB());
            addButton(34, x1 + 130, y, true, job.animationSpeed, numbs)
                    .setSize(60, 20);
        }
        y += 24;
        HashMap<Component, JobPuppet.PartConfig> dataIn = new HashMap<>();
        dataIn.put(Component.translatable("model.head"), isStart ? job.head : job.head2);
        dataIn.put(Component.translatable("model.body"), isStart ? job.body : job.body2);
        dataIn.put(Component.translatable("model.larm"), isStart ? job.larm : job.larm2);
        dataIn.put(Component.translatable("model.rarm"), isStart ? job.rarm : job.rarm2);
        dataIn.put(Component.translatable("model.lleg"), isStart ? job.lleg : job.lleg2);
        dataIn.put(Component.translatable("model.rleg"), isStart ? job.rleg : job.rleg2);
        data = dataIn;
        if (scroll == null) { scroll = addScroll(0).setSize(80, 100); }
        add(scroll.setPos(guiLeft + 10, y)
                .setNormalList(new ArrayList<>(dataIn.keySet())));
        if (selected != null) {
            scroll.setSelected(selected);
            if (scroll.hasSelected()) { addPartComponents(y, dataIn.get(selected)); }
        }
        addButton(66, guiLeft + imageWidth - 22, guiTop, "X")
                .setSize(20, 20);
        if (job.animate) {
            addButton(67, guiLeft + 10, y + 110, "gui.start")
                    .setSize(70, 20)
                    .setIsEnabled(!isStart);
            addButton(68, guiLeft + 90, y + 110, "gui.end")
                    .setSize(70, 20)
                    .setIsEnabled(isStart);
        }
    }

    private void addPartComponents(int y, JobPuppet.PartConfig config) {
        if (config == null) { return; }
        int x0 = guiLeft + 100;
        int x1 = x0 + 20;
        addButton(29, x1 + 20, y, false, config.disabled ? 1 : 0, "gui.enabled", "gui.disabled")
                .setSize(80, 20);
        addLabel(10, x0, (y += 22) + 5, "X:")
                .setSize(12, 10)
                .setColor(CustomNpcs.MainColor.getRGB());
        addSlider(10, x1, y, (config.rotationX + 1.0F) / 2.0F);
        addLabel(11, x0, (y += 22) + 5, "Y:")
                .setSize(12, 10)
                .setColor(CustomNpcs.MainColor.getRGB());
        addSlider(11, x1, y, (config.rotationY + 1.0F) / 2.0F);
        addLabel(12, x0, (y += 22) + 5, "Z:")
                .setSize(12, 10)
                .setColor(CustomNpcs.MainColor.getRGB());
        addSlider(12, x1, y, (config.rotationZ + 1.0F) / 2.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen( mouseX, mouseY, partialTicks);
        drawNpc(npc, 320, 200, 3.0F, 0, 0, 0);
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(parent);
        Packets.sendServer(new SPacketNpcJobSave(job.save(new NBTTagCompound())));
    }

    @Override
    public void mouseDragged(GuiSliderNop slider) {
        int percent = (int)(slider.sliderValue * 360.0F);
        slider.setString(percent + "%");
        JobPuppet.PartConfig part = data.get(selected);
        switch (slider.id) {
            case 10: part.rotationX = (slider.sliderValue - 0.5F) * 2.0F; break;
            case 11: part.rotationY = (slider.sliderValue - 0.5F) * 2.0F; break;
            case 12: part.rotationZ = (slider.sliderValue - 0.5F) * 2.0F; break;
        }
        npc.updateHitbox();
    }

    @Override
    public void mousePressed(GuiSliderNop slider) { }

    @Override
    public void mouseReleased(GuiSliderNop slider) { }

    @Override
    public void scrollClicked(GuiCustomScrollNop guiCustomScroll) {
        selected = guiCustomScroll.getNormalSelected();
        initGui();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
