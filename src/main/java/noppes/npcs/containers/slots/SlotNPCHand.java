package noppes.npcs.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;

public class SlotNPCHand extends Slot {

    protected final ResourceLocation texture;

    public SlotNPCHand(IInventory iinventory, int index, int xPosition, int yPosition, String part) {
        super(iinventory, index, xPosition, yPosition);
        texture = new ResourceLocation(CustomNpcs.MODID, "textures/items/empty_slot_" + part + ".png");
    }

    @Override
    public int getSlotStackLimit() { return 1; }

    public ResourceLocation getTexture() { return texture; }

}
