package noppes.npcs.mixin.block.material;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Material.class, priority = 502)
public interface IMaterialMixin {

    @Accessor void setMobilityFlag(EnumPushReaction newEnumPushReaction);

}
