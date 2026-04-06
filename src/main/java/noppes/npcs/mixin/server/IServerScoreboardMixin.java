package noppes.npcs.mixin.server;

import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = ServerScoreboard.class, priority = 502)
public interface IServerScoreboardMixin {

    @Accessor Set<Objective> getTrackedObjectives();

}
