package noppes.npcs.controllers;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.fml.ModList;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import org.apache.logging.log4j.LogManager;

public class PixelmonHelper {

   public static boolean Enabled = ModList.get().isLoaded("pixelmon");
   public static EventBus EVENT_BUS;
   public static Field storageManager;
   private static Object partyStorage;
   private static Method getPartyStorage;
   private static Object pcStorage;
   private static Method getPcStorage;
   private static Method getPokemonData;
   private static Method getPixelmonModel = null;
   private static Class<?> modelSetupClass;
   private static Method modelSetupMethod;
   private static Class<?> pixelmonClass;

   public static void load() {
      if (Enabled) {
         try {
            Class<?> c;
            if (isReforged()) {
               c = Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
               EVENT_BUS = (EventBus)c.getDeclaredField("EVENT_BUS").get(null);
               c = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageProxy");
               storageManager = c.getDeclaredField("storageManager");
               storageManager.setAccessible(true);
               c = Class.forName("com.pixelmonmod.pixelmon.api.storage.StorageManager");
               getPartyStorage = c.getMethod("getParty", UUID.class);
               getPcStorage = c.getMethod("getPCForPlayer", UUID.class);
               pixelmonClass = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.AbstractBaseEntity");
               getPokemonData = pixelmonClass.getMethod("getPokemon");
            } else {
               c = Class.forName("com.pixelmongenerations.core.storage.PixelmonStorage");
               partyStorage = c.getDeclaredField("pokeBallManager").get(null);
               pcStorage = c.getDeclaredField("computerManager").get(null);
               c = Class.forName("com.pixelmongenerations.core.storage.PokeballManager");
               getPartyStorage = c.getMethod("getPlayerStorage", Player.class);
               c = Class.forName("com.pixelmongenerations.core.storage.ComputerManager");
               getPcStorage = c.getMethod("getPlayerStorage", Player.class);
               pixelmonClass = Class.forName("com.pixelmongenerations.common.entity.pixelmon.Entity1Base");
            }
         } catch (Exception e) {
            LogManager.getLogger().error(e);
            Enabled = false;
         }
      }
   }

   public static boolean isReforged() {
      if (!Enabled) {
         throw new CustomNPCsException("No pixelmon installed");
      } else {
         try {
            Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            return true;
         } catch (Exception var1) {
            return false;
         }
      }
   }

   public static void loadClient() {
      if (Enabled) {
         try {
            Class<?> c;
            if (isReforged()) {
               c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.AbstractClientEntity");
               getPixelmonModel = c.getMethod("getModel");
               modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
            } else {
               c = Class.forName("com.pixelmongenerations.common.entity.pixelmon.Entity3HasStats");
               getPixelmonModel = c.getMethod("getModel");
               modelSetupClass = Class.forName("com.pixelmongenerations.client.models.PixelmonModelSmd");
            }
             modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
         } catch (Exception e) {
            LogManager.getLogger().error(e);
            Enabled = false;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static List<String> getPixelmonList() {
      List<String> list = new ArrayList<>();
      if (Enabled) {
         try {
            Class<?> c;
            if (isReforged()) {
               c = Class.forName("com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies");
               Field getAll = c.getDeclaredField("ENGLISH_NAMES");
               getAll.setAccessible(true);
               Object2IntOpenHashMap<String> names = (Object2IntOpenHashMap<String>) getAll.get(null);
               list = new ArrayList<>(names.keySet());
            } else {
               c = Class.forName("com.pixelmongenerations.core.enums.EnumSpecies");
               for (Object ob : c.getEnumConstants()) {
                  list.add(ob.toString());
               }
            }
         } catch (Exception e) {
            LogManager.getLogger().error("getPixelmonList", e);
         }
      }
      return list;
   }

   public static boolean isPixelmon(Entity entity) {
      if (!Enabled) {
         return false;
      } else {
         String s = entity.getEncodeId();
         return s != null && s.equals("pixelmon:pixelmon");
      }
   }

   public static String getName(LivingEntity entity) {
      if (Enabled && isPixelmon(entity)) {
         try {
            if (isReforged()) {
               Object species = pixelmonClass.getMethod("getSpecies").invoke(entity);
               return NoppesStringUtils.stripSpecialCharacters(((String)species.getClass().getMethod("getName").invoke(species)).toLowerCase());
            } else {
               Method m = entity.getClass().getMethod("getName");
               return m.invoke(entity).toString();
            }
         } catch (Exception e) {
            LogManager.getLogger().error("getName", e);
            return "";
         }
      } else {
         return "";
      }
   }

   public static Object getModel(LivingEntity entity) {
      try {
         return getPixelmonModel.invoke(entity);
      } catch (Exception e) {
         LogManager.getLogger().error("getModel", e);
         return null;
      }
   }

   public static void setupModel(LivingEntity entity, Object model) {
      try {
         if (modelSetupClass.isAssignableFrom(model.getClass())) {
            modelSetupMethod.invoke(model, entity);
         }
      } catch (Exception var3) {
         LogManager.getLogger().error("setupModel", var3);
      }

   }

   public static Object getPokemonData(Entity entity) {
      try {
         return getPokemonData.invoke(entity);
      } catch (Exception e) {
         LogManager.getLogger().error(e);
         return null;
      }
   }

   public static Object getParty(Player player) {
      try {
         if (isReforged()) {
            return getPartyStorage.invoke(storageManager.get(null), player.getUUID());
         }
         Optional<?> p = ((Optional<?>) getPartyStorage.invoke(partyStorage, player));
         if (p.isPresent()) { return p.get(); }
      } catch (Exception e) {
         LogManager.getLogger().error(e);
      }
      return null;
   }

   public static Object getPc(Player player) {
      try {
         return isReforged() ? getPcStorage.invoke(storageManager.get(null), player.getUUID()) : getPcStorage.invoke(pcStorage, player);
      } catch (Exception e) {
         LogManager.getLogger().error(e);
         return null;
      }
   }

   public static Class<?> getPixelmonClass() {
      return pixelmonClass;
   }

   @SuppressWarnings("unchecked")
   public static void initEntity(LivingEntity entity, String name) {
      try {
         if (isReforged()) {
            Class<?> c = Class.forName("com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies");
            Field f = c.getDeclaredField("ENGLISH_NAMES");
            f.setAccessible(true);
            Object2IntOpenHashMap<String> names = (Object2IntOpenHashMap<String>)f.get(null);
            f = c.getDeclaredField("REGISTERED_SPECIES");
            f.setAccessible(true);
            Int2ObjectOpenHashMap<Object> species = (Int2ObjectOpenHashMap<Object>)f.get(null);
            Object specie = species.get(names.getInt(name));
            c = Class.forName("com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory");
            Object pokemon = c.getMethod("create", specie.getClass()).invoke(null, specie);
            pixelmonClass.getMethod("setPokemon", pokemon.getClass()).invoke(entity, pokemon);
         }
      } catch (Exception var8) {
         LogManager.getLogger().error("initEntity", var8);
      }
   }

}
