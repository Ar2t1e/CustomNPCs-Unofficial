package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

public class CobblemonHelper {

   public static boolean Enabled = ModList.get().isLoaded("cobblemon");

   public static boolean isPokemon(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         ResourceLocation typeResLoc = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
         return typeResLoc != null && typeResLoc.equals(new ResourceLocation("cobblemon", "pokemon"));
      }
   }

   public static ResourceLocation getType(Entity entity) {
      if (!isPokemon(entity)) {
         return null;
      } else {
         try {
            Object pokemon = entity.getClass().getMethod("getPokemon").invoke(entity);
            Object species = pokemon.getClass().getMethod("getSpecies").invoke(pokemon);
            return (ResourceLocation)species.getClass().getField("resourceIdentifier").get(species);
         } catch (Exception var3) {
            return null;
         }
      }
   }

   public static void setType(Entity entity, ResourceLocation resourceLocation) {
      if (isPokemon(entity)) {
         try {
            Object instance = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies").getField("INSTANCE").get(null);
            Object species = instance.getClass().getMethod("getByIdentifier", ResourceLocation.class).invoke(instance, resourceLocation);
            Object pokemon = entity.getClass().getMethod("getPokemon").invoke(entity);
            pokemon.getClass().getMethod("setSpecies", species.getClass()).invoke(pokemon, species);
         }
         catch (Exception ignored) {}
      }
   }

   public static EntityModel<?> getPokemonModel(Entity entity) {
      ResourceLocation species = getType(entity);
      EntityModel<?> model = null;

      try {
         Object instance = Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository").getField("INSTANCE").get(null);
         model = (EntityModel<?>) instance.getClass().getMethod("getPoser", ResourceLocation.class, Set.class).invoke(instance, species, new HashSet<>());
      } catch (Exception e) {
         LogManager.getLogger().error(e);
      }
      return model;
   }

   @SuppressWarnings("unchecked")
   public static List<String> getTypes() {
      HashSet<String> res = new HashSet<>();
      try {
         Object instance = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies").getField("INSTANCE").get(null);
         List<String> implementedSpecies = (List<String>)instance.getClass().getMethod("getImplemented").invoke(instance);
         for (Object obj : implementedSpecies) {
            res.add(obj.getClass().getMethod("getResourceIdentifier").invoke(obj).toString());
         }
      } catch (Exception e) {
         LogManager.getLogger().error(e);
      }
      return new ArrayList<>(res);
   }
}
