package noppes.npcs.mixin.world;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = World.class, priority = 502)
public interface IWorldMixin {

    @Accessor List<Entity> getUnloadedEntityList();

    @Accessor PathWorldListener getPathListener();

}
