package noppes.npcs.mixin.init;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import noppes.npcs.CustomItems;
import noppes.npcs.items.custom.CustomItemLingeringPotion;
import noppes.npcs.items.custom.CustomItemPotion;
import noppes.npcs.items.custom.CustomItemSplashPotion;
import noppes.npcs.items.custom.CustomItemTippedArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Items.class, priority = 499)
public class ItemsMixin {

    /**
     * @author BetaZavr
     * @reason Substitution for custom potions for scripts
     */
    @Inject(method = "getRegisteredItem", at = @At("HEAD"), cancellable = true)
    private static void npcs$getRegisteredItem(String name, CallbackInfoReturnable<Item> cir) {
        switch (name) {
            case "tipped_arrow":
                if (CustomItems.itemTippedArrow == null) { CustomItems.itemTippedArrow = new CustomItemTippedArrow(); }
                cir.setReturnValue(CustomItems.itemTippedArrow);
                cir.cancel();
                break;
            case "potion":
                if (CustomItems.itemPotion == null) { CustomItems.itemPotion = new CustomItemPotion(); }
                cir.setReturnValue(CustomItems.itemPotion);
                cir.cancel();
                break;
            case "splash_potion":
                if (CustomItems.itemSplashPotion == null) { CustomItems.itemSplashPotion = new CustomItemSplashPotion(); }
                cir.setReturnValue(CustomItems.itemSplashPotion);
                cir.cancel();
                break;
            case "lingering_potion":
                if (CustomItems.itemLingeringPotion == null) { CustomItems.itemLingeringPotion = new CustomItemLingeringPotion(); }
                cir.setReturnValue(CustomItems.itemLingeringPotion);
                cir.cancel();
                break;
        }
    }

}
