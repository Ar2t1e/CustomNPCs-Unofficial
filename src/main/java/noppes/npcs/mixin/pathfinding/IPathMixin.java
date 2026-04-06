package noppes.npcs.mixin.pathfinding;

import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Path.class, priority = 502)
public interface IPathMixin {

    @Accessor
    PathPoint[] getPoints();

    @Accessor
    PathPoint[] getOpenSet();

    @Accessor
    void setOpenSet(PathPoint[] openSet);

    @Accessor
    PathPoint[] getClosedSet();

    @Accessor
    void setClosedSet(PathPoint[] closedSet);

}
