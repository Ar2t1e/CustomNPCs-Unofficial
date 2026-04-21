package noppes.npcs;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nullable;

public class CommonProxy {

   public void load() { }

   public void postload() { }

   public void openGui(Player player, Object guiscreen) { }

   public void openGui(EntityNPCInterface npc, EnumGuiType gui, @Nullable FriendlyByteBuf buffer) { }

   public void spawnParticle(LivingEntity player, String string, Object... ob) { }

   public @Nullable Player getPlayer() { return null; }

   public void spawnParticle(ParticleOptions type, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) { }

   public PlayerData getPlayerData(Player player) {
      if (player == null) { return null; }
      return PlayerData.get(player);
   }

   // New from Unofficial (BetaZavr)
   public String getTranslateLanguage(Player player) {
      if (player instanceof ServerPlayer sPlayer) {
         String lang = sPlayer.getLanguage();
         if (lang.contains("_")) { lang = lang.substring(0, lang.indexOf("_")); }
         return lang;
      }
      return "en";
   }

   public void init() { }

   public String getLanguage(Player entity) {
      if (entity instanceof ServerPlayer) { return ((ServerPlayer) entity).getLanguage(); }
      return "en_en";
   }

   public void updateKeys() { }

   public void loadAnimationModel(AnimationConfig animationConfig) { }

   public void createAllFiles(ICustomElement customElement) {
      if (customElement instanceof Block) { NoppesUtilServer.createAllBlockFiles(customElement); }
      if (customElement instanceof Item) { NoppesUtilServer.createAllItemFiles(customElement); }
   }

   public void playSound(SoundSource category, String sound, double x, double y, double z, float volume, float pitch, boolean streaming, boolean looping) {  }

   public void stopSound(int category, String sound) { }

   public @Nullable Level overworld() {
      if (CustomNpcs.Server != null) { return CustomNpcs.Server.overworld(); }
      return null;
   }

}
