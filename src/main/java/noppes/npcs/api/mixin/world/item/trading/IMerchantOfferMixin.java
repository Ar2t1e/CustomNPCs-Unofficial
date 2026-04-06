package noppes.npcs.api.mixin.world.item.trading;

import net.minecraft.world.item.ItemStack;

public interface IMerchantOfferMixin {

    void npcs$setItems(ItemStack stackA, ItemStack stackB, ItemStack resultStack);

}
