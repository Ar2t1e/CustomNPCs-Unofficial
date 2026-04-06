package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class SlotCustomPlace extends Slot {

    protected final OnPlace onPlace;

    public SlotCustomPlace(Container containerIn, int slotIn, int xIn, int yIn, OnPlace onPlaceIn) {
        super(containerIn, slotIn, xIn, yIn);
        onPlace = onPlaceIn != null ? onPlaceIn : (stack, slot) -> Block.byItem(stack.getItem()) != Blocks.AIR;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) { return onPlace.mayPlace(stack, this); }

    public interface OnPlace {  boolean mayPlace(@Nonnull ItemStack stack, SlotCustomPlace slot); }

}
