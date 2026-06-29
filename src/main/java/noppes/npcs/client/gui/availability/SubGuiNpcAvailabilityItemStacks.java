package noppes.npcs.client.gui.availability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumAvailabilityStackData;
import noppes.npcs.containers.ContainerNpcAvailabilityItem;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.AvailabilityStackData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSetSlotIndex;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// New from Unofficial (BetaZavr)
public class SubGuiNpcAvailabilityItemStacks
        extends GuiContainerNPCInterface<ContainerNpcAvailabilityItem>
        implements ICustomScrollListener {

    public static GuiScreen parent;
    public static SubGuiNpcAvailability setting;

    protected final Availability availability;
    protected final ContainerNpcAvailabilityItem cont;
    protected GuiCustomScrollNop scroll;
    protected final Map<Component, Integer> dataIDs = new HashMap<>();
    protected int reset = 0;

    public SubGuiNpcAvailabilityItemStacks(ContainerNpcAvailabilityItem container) {
        super(NoppesUtilServer.getEditingNpc(Minecraft.getMinecraft().player), container, Component.empty());
        setBackground("itemsetup.png");
        drawDefaultBackground = true;
        xSize = 176;
        ySize = 202;

        cont = container;
        availability = container.availability;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        AvailabilityStackData aData = availability.stacksData.get(cont.slot.getSlotIndex());
        switch (button.id) {
            case 0: {
                aData.ignoreDamage = !((GuiButtonYesNo) button).getBoolean();
                button.setHoverTexts("gui.ignoreDamage." + button.getValue());
                break;
            }
            case 1: {
                aData.ignoreNBT = !((GuiButtonYesNo) button).getBoolean();
                button.setHoverTexts("gui.ignoreNBT." + button.getValue());
                break;
            }
            case 2: {
                aData.type = EnumAvailabilityStackData.values()[(aData.type.ordinal() + 1) % EnumAvailabilityStackData.values().length];
                button.setHoverTexts("availability.hover.stack.type." + aData.type.name().toLowerCase());
                initGui();
                break;
            }
            case 66: {
                onClose();
                break;
            }
        }
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
        if (background != null) {
            minecraft.getTextureManager().bindTexture(background);
            // add up
            drawTexturedModalRect(guiLeft, guiTop - 12, 0, 0, xSize, 16);
            // add down
            drawTexturedModalRect(guiLeft, guiTop + ySize - 4, 0, 188, xSize, 14);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (reset > 0) {
            reset--;
            if (reset == 0) { initGui(); }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = guiLeft + 7;
        int y = guiTop + 4;
        int lId = 0;
        // title
        addLabel(lId++, x - 1, y - 12, "availability.available.8")
                .setSize(xSize - 12, 12)
                .setCenter(xSize - 12);
        // exit
        addButton(66, guiLeft + xSize / 2 - 35, guiTop + 189, "gui.done")
                .setSize(70, 18)
                .setHoverTexts("hover.back");
        // data
        List<Component> list = new ArrayList<>();
        List<Component> suffixes = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        Component select = Component.empty();
        dataIDs.clear();
        for (int i = 0; i < cont.inv.getSizeInventory(); i++) {
            ItemStack stack = cont.inv.getStackInSlot(i);
            AvailabilityStackData aData = availability.stacksData.get(i);
            Component name;
            if (stack.isEmpty()) { name = Component.literal(Component.translatable("info.item.cloner.empty.0").getFormattedText()); }
            else {
                name = Component.literal(stack.getDisplayName());
                if (stack.getCount() > 1) { name.append(Component.literal(" x" + stack.getCount())); }
            }
            Component suffix;
            switch (aData.type) {
                case Always: suffix = Component.literal("A").withStyle(TextFormatting.GREEN); break;
                case Contains: suffix = Component.literal("C").withStyle(TextFormatting.AQUA); break;
                default: suffix = Component.literal("E").withStyle(TextFormatting.RED); break;
            }
            Component key = Component.empty()
                    .append(Component.literal((i + 1) + ": ").withStyle(TextFormatting.GRAY))
                    .append(name);
            list.add(key);
            stacks.add(stack);
            suffixes.add(suffix);
            dataIDs.put(key, i);
            if (i == cont.slot.getSlotIndex()) { select = key; }
        }
        if (scroll == null) { scroll = addScroll(0).setSize(102, 107); }
        scroll.setUnsortedList(list)
                .setStacks(stacks)
                .setSuffixes(suffixes);
        if (!select.getFormattedText().isEmpty()) { scroll.setSelected(select); }
        add(scroll.setPos(guiLeft + 70, guiTop + 4));
        // ignore damage
        AvailabilityStackData aData = availability.stacksData.get(cont.slot.getSlotIndex());
        addLabel(lId++, x + 1, y, "gui.ignoreDamage")
                .setSize(60, 12);
        addYesNo(0, x, y += 10, aData != null && aData.ignoreDamage)
                .setSize(50, 14)
                .setHoverTexts("gui.ignoreDamage." + (aData != null && aData.ignoreDamage ? 1 : 0));
        // ignore nbt
        addLabel(lId++, x + 1, y += 15, "gui.ignoreNBT")
                .setSize(60, 12);
        addYesNo(1, x, y += 10, aData != null && aData.ignoreNBT)
                .setSize(50, 14)
                .setHoverTexts("gui.ignoreNBT." + (aData != null && aData.ignoreNBT ? 1 : 0));
        // type
        addLabel(lId++, x + 1, y += 15, "gui.type")
                .setSize(60, 12);
        addButton(2, x, y + 10, "availability." + (aData == null ? "always" : aData.type.name().toLowerCase()))
                .setSize(50, 14)
                .setHoverTexts("availability.hover.stack.type." + (aData == null ? "always" : aData.type.name().toLowerCase()));
        // id slot
        addLabel(lId, x + 20, y + 31, "ID: " + cont.slot.getSlotIndex())
                .setSize(40, 12);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent != null) { setScreen(parent); }
    }

    @Override
    public void save() {
        if (setting != null) {
            NBTTagCompound compound = new NBTTagCompound();
            availability.save(compound); // temp availability
            setting.availability.load(compound); // edit availability
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean bo = super.mouseClicked(mouseX, mouseY, mouseButton);
        for(int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.inventorySlots.get(i);
            if (isMouseHover(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY) && slot == cont.slot) {
                reset = 3;
                return bo;
            }
        }
        return bo;
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (!dataIDs.containsKey(scroll.getNormalSelected())) { return; }
        cont.slot.setSlotIndex(dataIDs.get(scroll.getNormalSelected()), true);
        scroll.setSelect(cont.slot.getSlotIndex());
        Packets.sendServer(new SPacketSetSlotIndex(cont.slot.getSlotIndex()));
        initGui();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
