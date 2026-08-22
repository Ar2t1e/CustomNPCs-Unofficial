package noppes.npcs.mixin.scoreboard;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Scoreboard.class, priority = 502)
public interface IScoreboardMixin {

    @Accessor Map<String, ScorePlayerTeam> getTeams();

}
