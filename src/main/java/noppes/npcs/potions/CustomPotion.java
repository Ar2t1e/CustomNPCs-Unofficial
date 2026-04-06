package noppes.npcs.potions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import javax.annotation.Nonnull;

// Potions
public class CustomPotion extends Potion {

    protected final @Nonnull CompoundTag nbtData;

    public CustomPotion(@Nonnull String name, @Nonnull CompoundTag nbtPotion, @Nonnull MobEffectInstance... effects) {
        super(name, effects);
        nbtData = nbtPotion;
    }

}
