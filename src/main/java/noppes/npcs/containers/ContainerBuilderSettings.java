package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.containers.slots.SlotCustomPlace;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.util.BuilderData;

import javax.annotation.Nonnull;

public class ContainerBuilderSettings extends AbstractContainerMenu {

    protected final Player player;
    public final BuilderData builderData;

    public ContainerBuilderSettings(int containerId, Inventory inv, BlockPos pos) {
        super(CustomContainer.container_builder, containerId);
        player = inv.player;
        BuilderData base = SyncController.dataBuilder.get(pos.getX());
        if (base != null) { builderData = base; }
        else { builderData = new BuilderData(pos.getX(), pos.getY()); }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inv, i, i * 18 + 8, 194));
        }
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlot(new Slot(inv, x + y * 9 + 9, x * 18 + 8, 136 + y * 18));
            }
        }

        NpcMiscInventory inventory = builderData.inv;
        if (inv.player.level().isClientSide()) { inventory = inventory.copy(); }
        if (builderData.getType() == 2) { addSlot(new Slot(inventory, 0, 62, 113)); }
        SlotCustomPlace.OnPlace onPlace = (stack, slot) -> {
            for (int i = 0; i < 9; i++) {
                Slot slotIn = getSlot(i + (builderData.getType() == 2 ? 37 : 36));
                if (!slot.equals(slotIn) && slotIn.hasItem() && NoppesUtilPlayer.compareItems(stack, slotIn.getItem(), true, false)) { return false; }
            }
            return Block.byItem(stack.getItem()) != Blocks.AIR;
        };
        for (int i = 1; i < 10; i++) {
            addSlot(new SlotCustomPlace(inventory, i, 8 + (i / 6) * 54, 17 + ((i < 6 ? 0 : -5) + i - 1) * 24, onPlace) {
                public int getMaxStackSize() { return 1; }
            });
        }
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slotId < 36) {
            if (Block.byItem(slot.getItem().getItem()) != Blocks.AIR) {
                int start = builderData.getType() == 2 ? 37 : 36;
                boolean found = false;
                for (int i = 0; i < 9; i++) {
                    Slot slotIn = getSlot(i + start);
                    if (!slot.equals(slotIn) && slotIn.hasItem() && NoppesUtilPlayer.compareItems(slot.getItem(), slotIn.getItem(), true, false)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (!moveItemStackTo(slot.getItem(), start, start + 9, true)) { return ItemStack.EMPTY; }
                }
            }
        }
        else if (!moveItemStackTo(slot.getItem(), 0, 36, true)) { return ItemStack.EMPTY; }
        if (player instanceof ServerPlayer sPlayer) { Packets.send(sPlayer, new PacketGuiUpdate()); }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) { return playerIn.isCreative(); }

    @Override
    public void removed(@Nonnull Player playerIn) {
        super.removed(playerIn);
        if (builderData != null) {
            int s = builderData.getType() == 2 ? 0 : 1;
            for (int i = 36; i < slots.size(); i++) {
                builderData.inv.setItem(s + i - 36, getSlot(i).getItem());
            }
        }
    }

}
