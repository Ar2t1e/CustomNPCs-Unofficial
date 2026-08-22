package noppes.npcs.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import noppes.npcs.api.item.IItemStack;

@SuppressWarnings("unused")
public interface ISlot {

    int getX();

    int getY();

    int getId();

    int getIndex();

    IItemStack getItem();

    Slot getMCSlot();

    IContainer getIContainer();

    IInventory getMCInventory();

}
