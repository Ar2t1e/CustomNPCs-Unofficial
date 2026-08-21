package noppes.npcs.mixin.entity.passive;

import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityVillager.class, priority = 502)
public interface IEntityVillagerMixin {

    @Accessor int getCareerId();

}
