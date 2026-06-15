package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;

import javax.annotation.Nonnull;

public class ContainerChestCustom extends AbstractContainerMenu {

    public BlockPos pos;
    public int height;
    public Player player;
    public CustomTileEntityChest customChest, trueChest;

    public ContainerChestCustom(int containerId, Inventory playerInventory, CustomTileEntityChest chest) {
        super(CustomContainer.container_custom_chest, containerId);
        pos = chest.getBlockPos();
        player = playerInventory.player;
        trueChest = chest;
        customChest = chest;
        trueChest.startOpen(player);
        int size = customChest.getContainerSize();
        int rows = (int) Math.ceil(size / 9.0);
        height = rows > 5 ? 90 : rows * 18;
        // Chest slots
        for (int i = 0; i < size; i++) {
            addSlot(new Slot(customChest, i, 8 + (i % 9) * 18, 15 + (i / 9) * 18));
        }
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 26 + row * 18 + height));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 84 + height));
        }
    }

    public CustomTileEntityChest getTileEntity() { return trueChest; }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return customChest.stillValid(player);
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        trueChest.stopOpen(player);
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < customChest.getContainerSize()) {
                if (!moveItemStackTo(itemstack1, customChest.getContainerSize(), slots.size(), true)) { return ItemStack.EMPTY; }
            }
            else if (!moveItemStackTo(itemstack1, 0, customChest.getContainerSize(), false)) { return ItemStack.EMPTY; }
            if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
            else { slot.setChanged(); }
        }
        return itemstack;
    }

}