package noppes.npcs.api.wrapper;

import net.minecraft.world.scores.Score;
import noppes.npcs.api.IScoreboardScore;

public class ScoreboardScoreWrapper implements IScoreboardScore {

   private final Score score;

   public ScoreboardScoreWrapper(Score score) {
      this.score = score;
   }

   public int getValue() {
      return this.score.getScore();
   }

   public void setValue(int val) {
      this.score.setScore(val);
   }

   public String getPlayerName() {
      return this.score.getOwner();
   }

}
