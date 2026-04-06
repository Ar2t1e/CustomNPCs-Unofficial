package noppes.npcs.potions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nonnull;

// MobEffectInstance; OLD: PotionEffect
public class CustomMobEffectInstance extends MobEffectInstance {

    protected final @Nonnull CompoundTag nbtData;
    protected final @Nonnull ResourceLocation location;

    public CustomMobEffectInstance(MobEffect effect, int durationIn, @Nonnull ResourceLocation name, @Nonnull CompoundTag nbtPotion) {
        super(effect, durationIn);
        nbtData = nbtPotion;
        location = name;
    }

    public ResourceLocation getName() { return location; }

}
