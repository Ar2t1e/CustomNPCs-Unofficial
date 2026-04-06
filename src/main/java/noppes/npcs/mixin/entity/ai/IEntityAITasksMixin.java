package noppes.npcs.mixin.entity.ai;

import net.minecraft.entity.ai.EntityAITasks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityAITasks.class, priority = 502)
public interface IEntityAITasksMixin {

    @Accessor
    int getTickRate();

    @Accessor
    void setTickRate(int newTickRate);

}
