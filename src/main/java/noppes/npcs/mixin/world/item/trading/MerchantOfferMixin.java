package noppes.npcs.mixin.world.item.trading;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import noppes.npcs.api.mixin.world.item.trading.IMerchantOfferMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MerchantOffer.class, priority = 499)
public class MerchantOfferMixin implements IMerchantOfferMixin {

    @Mutable
    @Final
    @Shadow
    private ItemStack baseCostA;

    @Mutable
    @Final
    @Shadow
    private ItemStack costB;

    @Mutable
    @Final
    @Shadow
    private ItemStack result;

    @Override
    public void npcs$setItems(ItemStack stackA, ItemStack stackB, ItemStack resultStack) {
        if (stackA != null) { baseCostA = stackA; }
        if (stackA != null) { costB = stackB; }
        if (stackA != null) { result = resultStack; }
    }

}
