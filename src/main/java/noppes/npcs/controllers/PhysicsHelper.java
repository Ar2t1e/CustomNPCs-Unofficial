package noppes.npcs.controllers;

import java.util.Set;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;

public class PhysicsHelper {

    public static boolean Enabled = Loader.isModLoaded("physicsmod");

    @SuppressWarnings("unchecked")
    public static void resetEntityPhysics(World world, int id) {
        try {
            Class<?> physModClass = Class.forName("net.diebuddies.physics.PhysicsMod");
            Object modInstance = physModClass.getMethod("getInstance", World.class).invoke(null, world);
            Set<Integer> blockified = (Set<Integer>) physModClass.getField("alreadyBlockified").get(modInstance);
            blockified.remove(id);
        }
        catch (Exception e) { LogManager.getLogger().error(e); }
    }

}