package noppes.npcs.api;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.api.item.IItemStack;

public interface ISlot {

    int getX();

    int getY();

    int getId();

    int getIndex();

    IItemStack getItem();

    Slot getMCSlot();

    IContainer getIContainer();

    Container getMCInventory();

}
