package noppes.npcs.controllers;

import java.util.Set;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;

public class PhysicsHelper {

   public static boolean Enabled = ModList.get().isLoaded("physicsmod");

   @SuppressWarnings("unchecked")
   public static void resetEntityPhysics(Level level, int id) {
      try {
         Class<?> physModClass = Class.forName("net.diebuddies.physics.PhysicsMod");
         Object modInstance = physModClass.getMethod("getInstance", Level.class).invoke(null, level);
         Set<Integer> blockified = (Set<Integer>) physModClass.getField("alreadyBlockified").get(modInstance);
         blockified.remove(id);
      }
      catch (Exception e) { LogManager.getLogger().error(e); }
   }

}
