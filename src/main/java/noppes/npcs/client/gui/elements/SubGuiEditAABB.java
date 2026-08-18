package noppes.npcs.client.gui.elements;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class SubGuiEditAABB extends GuiBasic {

    protected final NBTTagCompound nbtData;
    protected final NBTTagList firstProp;

    public SubGuiEditAABB(NBTTagCompound parentNbt) {
        super();
        setBackground("smallbg.png");
        closeOnEsc = true;
        imageWidth = 200;
        imageHeight = 91;
        title = Component.translatable("element.edit.aabb.title");

        nbtData = parentNbt;
        firstProp = parentNbt.getTagList("AABB", 6).copy();
    }

    @Override
    public void initGui() {
        super.initGui();
        int x0 = guiLeft + 5;
        int x1 = x0 + 22;
        int x2 = guiLeft + imageWidth / 2;
        int x3 = x2 + 22;
        int y = guiTop + 17;
        NBTTagList aabb = nbtData.getTagList("AABB", 6);
        double[] values = new double[6];
        for (int i = 0; i < 6 && i < aabb.tagCount(); i++) {
            double d0 = i < 3 ? 0.0d : 1.0d;
            if (aabb.tagCount() < 1) { d0 = aabb.getDoubleAt(i); }
            values[i] = d0;
        }
        String[] labels = { "minX", "minY", "minZ", "maxX", "maxY", "maxZ" };
        for (int i = 0; i < 3; i++) {
            // min
            addLabel(i, x0, y + 2 + i * 18, labels[i])
                    .setSize(20, 10);
            addTextField(i, x1, y + i * 18, 69, 14, values[i])
                    .setMinMaxDefault(-2.5f, 2.5f, 0.0f);
            // max
            addLabel(i + 3, x2, y + 2 + i * 18, labels[i + 3])
                    .setSize(20, 10);
            addTextField(i + 3, x3, y + i * 18, 69, 14, values[i + 3])
                    .setMinMaxDefault(-2.5f, 2.5f, 0.0f);
        }
        y = guiTop + imageHeight - 21;
        addButton(0, x0, y, "gui.done")
                .setSize(60, 16)
                .setHoverTexts("element.hover.aabb.done");
        addButton(1, x2 - 30, y, "gui.clear")
                .setSize(60, 16)
                .setHoverTexts("element.hover.aabb.clear");
        addButton(66, guiLeft + imageWidth - 65, y, "gui.cancel")
                .setSize(60, 16)
                .setHoverTexts("hover.exit");
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                NBTTagList list = new NBTTagList();
                for (int i = 0; i < 6; i++) { list.appendTag(new NBTTagDouble(getTextField(i).getDouble())); }
                nbtData.setTag("AABB", list);
                onClose();
            } // done
            case 1: {
                nbtData.removeTag("AABB");
                initGui();
                break;
            } // clear
            case 66: {
                if (firstProp.hasNoTags()) { nbtData.removeTag("AABB"); } else { nbtData.setTag("AABB", firstProp); }
                onClose();
                break;
            } // cancel
        }
    }

}
