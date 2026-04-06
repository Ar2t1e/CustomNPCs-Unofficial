package noppes.npcs.mixin.world.level.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Mixin(value = Path.class, priority = 502)
public interface IPathMixin {

    @Accessor List<Node> getNodes();

    @Accessor @Nullable Set<Target> getTargetNodes();

    @Mutable @Accessor void setTargetNodes(@Nullable Set<Target> newTargetNodes);

    @Accessor boolean getReached();

    @Accessor int getNextNodeIndex();

    @Mutable @Accessor void setNextNodeIndex(int newNextNodeIndex);

    @Accessor BlockPos getTarget();

    @Mutable @Accessor void setTarget(BlockPos newTarget);

    @Accessor Node[] getOpenSet();

    @Mutable @Accessor void setOpenSet(Node[] newOpenSet);

    @Accessor Node[] getClosedSet();

    @Mutable @Accessor void setClosedSet(Node[] newClosedSet);

}
