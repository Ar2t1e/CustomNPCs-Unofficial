package noppes.npcs.api.wrapper.gui;

import java.util.*;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.*;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;

public class GuiComponentsWrapper implements IComponentsWrapper {

    protected List<ICustomGuiComponent> components = new ArrayList<>();
    protected List<IItemSlot> slots = new ArrayList<>();
    protected List<IItemSlot> playerSlots = new ArrayList<>();
    protected IPlayer<?> player;
    public int slotId = 0;

    public GuiComponentsWrapper(IPlayer<?> playerIn) { player = playerIn; }

    @Override
    public CustomGuiButtonWrapper addButton(int id, String label, int x, int y) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiButtonWrapper addButton(int id, String label, int x, int y, int width, int height) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiButtonListWrapper addButtonList(int id, int x, int y, int width, int height) {
        CustomGuiButtonListWrapper component = new CustomGuiButtonListWrapper(id, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiButtonWrapper addTexturedButton(int id, String label, int x, int y, int width, int height, String texture) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiButtonWrapper addTexturedButton(int id, String label, int x, int y, int width, int height, String texture, int textureX, int textureY) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture, textureX, textureY);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiLabelWrapper addLabel(int id, String label, int x, int y, int width, int height) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiLabelWrapper addLabel(int id, String label, int x, int y, int width, int height, int color) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height, color);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiTextFieldWrapper addTextField(int id, int x, int y, int width, int height) {
        CustomGuiTextFieldWrapper component = new CustomGuiTextFieldWrapper(id, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiTextAreaWrapper addTextArea(int id, int x, int y, int width, int height) {
        CustomGuiTextAreaWrapper component = new CustomGuiTextAreaWrapper(id, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiTexturedRectWrapper addTexturedRect(int id, String texture, int x, int y, int width, int height) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiTexturedRectWrapper addTexturedRect(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height, textureX, textureY);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiScrollWrapper addScroll(int id, int x, int y, int width, int height, String... list) {
        CustomGuiScrollWrapper component = new CustomGuiScrollWrapper(id, x, y, width, height, list);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiSliderWrapper addSlider(int id, int x, int y, int width, int height, String format) {
        CustomGuiSliderWrapper component = new CustomGuiSliderWrapper(id, format, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiEntityDisplayWrapper addEntityDisplay(int id, int x, int y, IEntity<?> entity) {
        CustomGuiEntityDisplayWrapper component = new CustomGuiEntityDisplayWrapper(id, entity, x, y);
        addComponent(component);
        return component;
    }

    @Override
    public CustomGuiAssetsSelectorWrapper addAssetsSelector(int id, int x, int y, int width, int height) {
        CustomGuiAssetsSelectorWrapper component = new CustomGuiAssetsSelectorWrapper(id, x, y, width, height);
        addComponent(component);
        return component;
    }

    @Override
    public IColoredLine addColoredLine(int id, int xStart, int yStart, int xEnd, int yEnd, int color, float thickness) {
        IColoredLine line = new CustomGuiColoredLineWrapper(id, xStart, yStart, xEnd, yEnd, color, thickness);
        components.add(line);
        return line;
    }

    @Override
    public IItemRenderer addItemRenderer(int id, int x, int y, int width, int height, IItemStack stack) {
        CustomGuiItemRendererWrapper rendererWrapper = new CustomGuiItemRendererWrapper(id, x, y, width, height, stack);
        components.add(rendererWrapper);
        return rendererWrapper;
    }

    @Override
    public ICustomGuiComponent getComponent(int componentID) {
        for (ICustomGuiComponent component : new ArrayList<>(components)) {
            if (component.getId() == componentID) { return component; }
        }
        return null;
    }

    @Override
    public void addComponent(ICustomGuiComponent component) {
        if (components.stream().anyMatch((t) -> t.getId() == component.getId())) {
            throw new CustomNPCsException("This gui already contains component id:" + component.getId());
        }
        components.add(component);
    }

    @Override
    public void removeComponent(int componentID) { components.removeIf((c) -> c.getId() == componentID); }

    @Override
    public List<ICustomGuiComponent> getComponents() { return components; }

    public NBTTagCompound getComponentNbt() {
        NBTTagCompound comp = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (ICustomGuiComponent component : components) { list.appendTag(((CustomGuiComponentWrapper) component).toNBT(new NBTTagCompound())); }
        comp.setTag("components", list);
        list = new NBTTagList();
        for (ICustomGuiComponent component : slots) { list.appendTag(((CustomGuiComponentWrapper) component).toNBT(new NBTTagCompound())); }
        comp.setTag("slots", list);
        list = new NBTTagList();
        for (ICustomGuiComponent component : playerSlots) { list.appendTag(((CustomGuiComponentWrapper) component).toNBT(new NBTTagCompound())); }
        comp.setTag("playerSlots", list);
        return comp;
    }

    private List<IItemSlot> getNbtSlots(NBTTagCompound tag, String key) {
        List<IItemSlot> slots = new ArrayList<>();
        NBTTagList list = tag.getTagList(key, 10);
        for (NBTBase b : list) {
            CustomGuiItemSlotWrapper component = (CustomGuiItemSlotWrapper) CustomGuiComponentWrapper.createFromNBT((NBTTagCompound) b);
            slots.add(component);
        }
        return slots;
    }

    public void setComponentNbt(NBTTagCompound comp) {
        List<ICustomGuiComponent> componentsIn = new ArrayList<>();
        NBTTagList list = comp.getTagList("components", 10);
        for (NBTBase b : list) { componentsIn.add(CustomGuiComponentWrapper.createFromNBT((NBTTagCompound) b)); }
        components = componentsIn;
        slots = getNbtSlots(comp, "slots");
        playerSlots = getNbtSlots(comp, "playerSlots");
    }

    public ICustomGuiComponent getComponentUuid(UUID id) {
        for (ICustomGuiComponent component : new ArrayList<>(components)) {
            if (component.getUniqueID().equals(id)) { return component; }
        }
        return null;
    }

    @Override
    public List<IItemSlot> getSlots() { return slots; }

    @Override
    public List<IItemSlot> getPlayerSlots() { return playerSlots; }

    @Override
    public IItemSlot addItemSlot(int x, int y) { return addItemSlot(x, y, ItemScriptedWrapper.AIR); }

    @Override
    public IItemSlot addItemSlot(int x, int y, IItemStack stack) {
        CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x, y, stack);
        GuiComponentsWrapper w = this;
        if (this instanceof GuiComponentsScrollableWrapper) { w = ((GuiComponentsScrollableWrapper) this).parent; }
        slot.setId(w.slotId++);
        slots.add(slot);
        return slot;
    }

    @Override
    public void removeItemSlot(IItemSlot slot) { slots.removeIf((s) -> s.getId() == slot.getId()); }

    @Override
    public void showPlayerInventory(int x, int y) { showPlayerInventory(x, y, true); }

    @Override
    public IItemSlot[] showPlayerInventory(int x, int y, boolean full) {
        List<IItemSlot> newPlayerSlots = new ArrayList<>();
        int row;
        if (full) {
            for(row = 0; row < 3; ++row) {
                for(int col = 0; col < 9; ++col) {
                    CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x + col * 18, y + row * 18, player.getMCEntity());
                    slot.setId(9 + row * 9 + col);
                    playerSlots.add(slot);
                }
            }
            y += 58;
        }
        for(row = 0; row < 9; ++row) {
            CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x + row * 18, y, player.getMCEntity());
            slot.setId(row);
            newPlayerSlots.add(slot);
        }
        playerSlots = newPlayerSlots;
        return playerSlots.toArray(new IItemSlot[0]);
    }

    // New from Unofficial (BetaZavr)
    @Override
    public CustomGuiTextFieldWrapper getTextField(int id) {
        for (ICustomGuiComponent component : components) {
            if (component instanceof CustomGuiTextFieldWrapper && component.getId() == id) { return (CustomGuiTextFieldWrapper) component; }
        }
        return null;
    }

    @Override
    public CustomGuiSliderWrapper getSlider(int id) {
        for (ICustomGuiComponent component : components) {
            if (component instanceof CustomGuiSliderWrapper && component.getId() == id) { return (CustomGuiSliderWrapper) component; }
        }
        return null;
    }

}
