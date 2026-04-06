package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardTeam;

public class
ScoreboardTeamWrapper implements IScoreboardTeam {

   private final PlayerTeam team;
   private final Scoreboard board;

   protected ScoreboardTeamWrapper(PlayerTeam team, Scoreboard board) {
      this.team = team;
      this.board = board;
   }

   public String getName() {
      return this.team.getName();
   }

   public String getDisplayName() {
      return this.team.getDisplayName().getString();
   }

   public void setDisplayName(String name) {
      if (!name.isEmpty() && name.length() <= 32) {
         this.team.setDisplayName(Component.translatable(name));
      } else {
         throw new CustomNPCsException("Score team display name must be between 1-32 characters: %s", name);
      }
   }

   public void addPlayer(String player) {
      this.board.addPlayerToTeam(player, this.team);
   }

   public void removePlayer(String player) {
      this.board.removePlayerFromTeam(player, this.team);
   }

   public String[] getPlayers() {
      List<String> list = new ArrayList<>(this.team.getPlayers());
      return list.toArray(new String[0]);
   }

   public void clearPlayers() {
      List<String> list = new ArrayList<>(this.team.getPlayers());
      for (String player : list) {
         this.board.removePlayerFromTeam(player, this.team);
      }
   }

   public boolean getFriendlyFire() {
      return this.team.isAllowFriendlyFire();
   }

   public void setFriendlyFire(boolean bo) {
      this.team.setAllowFriendlyFire(bo);
   }

   public void setColor(String color) {
      ChatFormatting enumchatformatting = ChatFormatting.getByName(color);
      if (enumchatformatting != null && !enumchatformatting.isFormat()) {
         this.team.setPlayerPrefix(Component.literal(enumchatformatting.toString()));
         this.team.setPlayerSuffix(Component.literal(ChatFormatting.RESET.toString()));
      } else {
         throw new CustomNPCsException("Not a proper color name: %s", color);
      }
   }

   public String getColor() {
      Component prefix = team.getPlayerPrefix();
      if (!prefix.getString().isEmpty()) {
         ChatFormatting[] var2 = ChatFormatting.values();
         for (ChatFormatting format : var2) {
            if (prefix.getString().equals(format.toString()) && format != ChatFormatting.RESET) {
               return format.getName();
            }
         }
      }
      return null;
   }

   public void setSeeInvisibleTeamPlayers(boolean bo) {
      this.team.setSeeFriendlyInvisibles(bo);
   }

   public boolean getSeeInvisibleTeamPlayers() {
      return this.team.canSeeFriendlyInvisibles();
   }

   public boolean hasPlayer(String player) {
      return this.board.getPlayersTeam(player) != null;
   }
}
