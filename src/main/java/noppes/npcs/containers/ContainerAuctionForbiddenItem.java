package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.controllers.data.AuctionHouse;
import noppes.npcs.controllers.data.AuctionLot;

import javax.annotation.Nonnull;

public class ContainerAuctionForbiddenItem extends Container {

    public final AuctionHouse auction;
    public final int pos;
    public final AuctionHouse.ForbiddenItem forbiddenItem;

    public ContainerAuctionForbiddenItem(EntityPlayer player, BlockPos blockPos) {
        auction = AuctionController.getInstance().getAuction(blockPos.getX());
        AuctionHouse.ForbiddenItem forbiddenItemIn = auction.getForbiddenItem(pos = blockPos.getY());
        if (pos<0 || forbiddenItemIn == null) { forbiddenItemIn = new AuctionHouse.ForbiddenItem(ItemStack.EMPTY, false, false); }
        forbiddenItem = forbiddenItemIn;
        addSlotToContainer(new Slot(forbiddenItem.inventory, 0, 8, 103));
        // Player Inventory
        for (int r = 0; r < 3; ++r) {
            for (int p = 0; p < 9; ++p) {
                addSlotToContainer(new Slot(player.inventory, p + r * 9 + 9, 8 + p * 18, 103 + r * 18));
            }
        }
        for (int p = 0; p < 9; ++p) {
            addSlotToContainer(new Slot(player.inventory, p, 8 + p * 18, 161));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) { return true; }

}
