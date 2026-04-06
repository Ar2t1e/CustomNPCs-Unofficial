package noppes.npcs.mixin.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import noppes.npcs.CustomPotions;
import noppes.npcs.potions.CustomPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(value = CreativeModeTabs.class, priority = 499)
public class CreativeModeTabsMixin {

    @Redirect(
            method = "generatePotionEffectTypes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"
            ),
            require = 1
    )
    private static <T extends Holder.Reference<Potion>> Stream<T> filterStream(Stream<T> stream, Predicate<? super T> predicate) {
        return stream.filter(holder -> {
            if (!holder.is(Potions.EMPTY_ID)) {
                return !(holder.value() instanceof CustomPotion) && !CustomPotions.CUSTOMS.containsKey(holder.value().getName(""));
            }
            return false;
        });
    }

}
