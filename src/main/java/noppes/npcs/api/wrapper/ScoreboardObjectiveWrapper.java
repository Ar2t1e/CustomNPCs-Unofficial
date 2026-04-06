package noppes.npcs.api.wrapper;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardObjective;
import noppes.npcs.api.IScoreboardScore;

public class ScoreboardObjectiveWrapper implements IScoreboardObjective {

   private final Objective objective;
   private final Scoreboard board;

   protected ScoreboardObjectiveWrapper(Scoreboard board, Objective objective) {
      this.objective = objective;
      this.board = board;
   }

   public String getName() {
      return this.objective.getName();
   }

   public String getDisplayName() {
      return this.objective.getDisplayName().getString();
   }

   public void setDisplayName(String name) {
      if (!name.isEmpty() && name.length() <= 32) {
         this.objective.setDisplayName(Component.translatable(name));
      } else {
         throw new CustomNPCsException("Score objective display name must be between 1-32 characters: %s", name);
      }
   }

   public String getCriteria() {
      return this.objective.getCriteria().getName();
   }

   public boolean isReadyOnly() {
      return this.objective.getCriteria().isReadOnly();
   }

   public IScoreboardScore[] getScores() {
      Collection<Score> list = this.board.getPlayerScores(this.objective);
      IScoreboardScore[] scores = new IScoreboardScore[list.size()];
      int i = 0;
      for(Iterator<Score> var4 = list.iterator(); var4.hasNext(); ++i) {
         Score score = var4.next();
         scores[i] = new ScoreboardScoreWrapper(score);
      }
      return scores;
   }

   public IScoreboardScore getScore(String player) {
      return !this.hasScore(player) ? null : new ScoreboardScoreWrapper(this.board.getOrCreatePlayerScore(player, this.objective));
   }

   public IScoreboardScore createScore(String player) {
      return new ScoreboardScoreWrapper(this.board.getOrCreatePlayerScore(player, this.objective));
   }

   public void removeScore(String player) {
      this.board.resetPlayerScore(player, this.objective);
   }

   public boolean hasScore(String player) {
      return this.board.hasPlayerScore(player, this.objective);
   }

}
