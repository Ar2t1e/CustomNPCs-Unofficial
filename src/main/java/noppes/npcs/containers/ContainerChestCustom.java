package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;

import javax.annotation.Nonnull;

public class ContainerChestCustom extends AbstractContainerMenu {

    public BlockPos pos;
    public int height;
    public Player player;
    public CustomTileEntityChest customChest;

    public ContainerChestCustom(int containerId, Inventory inv, Container chest) {
        super(CustomContainer.container_custom_chest, containerId);

    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int slotId) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) { return false; }

}
