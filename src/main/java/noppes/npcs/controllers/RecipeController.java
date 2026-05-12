package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

// Changed by Unofficial (BetaZavr)
public class RecipeController implements IRecipeHandler {

   public static final RecipeBookType CRAFTING_CUSTOM_GLOBAL = RecipeBookType.create("CRAFTING_CUSTOM_GLOBAL");
   public static final RecipeBookType CRAFTING_CUSTOM_ANVIL = RecipeBookType.create("CRAFTING_CUSTOM_ANVIL");

   public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
           DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CustomNpcs.MODID);
   static {
      RECIPE_SERIALIZERS.register("npcs_carpentry", () -> RecipeCarpentry.CARPENTRY_RECIPE);
   }

   public static final int version = 4;

   protected static RecipeController instance;

   protected final Map<String, List<RecipeCarpentry>> globalRecipes = new HashMap<>(); // { group, group recipes }
   protected final Map<String, List<RecipeCarpentry>> anvilRecipes = new HashMap<>(); // { group, group recipes }
   public final Map<String, List<RecipeCarpentry>> syncRecipes = new HashMap<>();

   public static RecipeController getInstance() {
      if (instance == null) { instance = new RecipeController(); }
      return instance;
   }

   @Override
   public boolean delete(String id) { return delete(new ResourceLocation(id)); }

   @Override
   public List<INpcRecipe> getGlobalRecipes(String group) {
      List<INpcRecipe> list = new ArrayList<>();
      if (globalRecipes.containsKey(group)) { list.addAll(globalRecipes.get(group)); }
      return list;
   }

   @Override
   public List<INpcRecipe> getAnvilRecipes(String group) {
      List<INpcRecipe> list = new ArrayList<>();
      if (anvilRecipes.containsKey(group)) { list.addAll(anvilRecipes.get(group)); }
      return list;
   }

   @Override
   public RecipeCarpentry addRecipe(String name, String group, boolean global, ItemStack result, Object... objects) {
      RecipeCarpentry recipe = RecipeCarpentry.createRecipe(new ResourceLocation(CustomNpcs.MODID, name), group, global, true, result, objects);
      return addAndSaveRecipe(recipe);
   }

   @Override
   public RecipeCarpentry addRecipe(String name, String group, boolean global, ItemStack result, int width, int height, ItemStack... objects) {
      NonNullList<Ingredient> list = NonNullList.create();
      for(ItemStack item : objects) {
         if (!item.isEmpty()) { list.add(Ingredient.of(item)); }
      }
      RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), group, width, height, global, true, list, result);
      return addAndSaveRecipe(recipe);
   }

   @Override
   public List<INpcRecipe> getAllAnvilRecipes() {
      List<INpcRecipe> list = new ArrayList<>();
      for (List<RecipeCarpentry> recipes : new ArrayList<>(anvilRecipes.values())) { list.addAll(recipes); }
      return list;
   }

   @Override
   public List<INpcRecipe> getAllGlobalRecipes() {
      List<INpcRecipe> list = new ArrayList<>();
      for (List<RecipeCarpentry> recipes : new ArrayList<>(globalRecipes.values())) { list.addAll(recipes); }
      return list;
   }

   public void load() {
      loadCategories();
      EventHooks.onGlobalRecipesLoaded(this);
      CustomNpcs.proxy.syncRecipeManager();
   }

   public void reloadGlobalRecipes() {
      globalRecipes.clear();
      globalRecipes.putAll(syncRecipes);
      syncRecipes.clear();
   }

   public void reloadAnvilRecipes() {
      anvilRecipes.clear();
      anvilRecipes.putAll(syncRecipes);
      syncRecipes.clear();
   }

   private void loadCategories() {
      File saveDir = CustomNpcs.getLevelSaveDirectory();
      try {
         File file = new File(saveDir, "recipes.dat");
         if (file.exists()) {
            loadCategories(file);
         }
         else {
            globalRecipes.clear();
            anvilRecipes.clear();
            loadDefaultRecipes(-1);
         }
      } catch (Exception e) {
         LogWriter.error(e);
         try {
            File file = new File(saveDir, "recipes.dat_old");
            if (file.exists()) { loadCategories(file); }
         } catch (Exception err) {
            LogWriter.error(err);
         }
      }
   }

   private void loadDefaultRecipes(int versionIn) {
      if (versionIn != version) {
         RecipesDefault.loadDefaultRecipes(versionIn);
      }
   }

   private void loadCategories(File file) throws Exception {
      CompoundTag compound = NbtIo.readCompressed(new FileInputStream(file));
      globalRecipes.clear();
      anvilRecipes.clear();
      int version = compound.getInt("Version");
      if (version < 3) {
         ListTag list = compound.getList("Data", 10);
         for(int i = 0; i < list.size(); ++i) {
            RecipeCarpentry recipe = RecipeCarpentry.create(list.getCompound(i));
            Map<String, List<RecipeCarpentry>> map = recipe.isGlobal ? globalRecipes : anvilRecipes;
            if (!map.containsKey("main")) { map.put("main", new ArrayList<>()); }
            map.get("main").add(recipe);
         }
      }
      else if (version == 3) {
         ListTag list = compound.getList("Data", 10);
         for (int i = 0; i < list.size(); i++) {
            CompoundTag nbtG = list.getCompound(i);
            if (!nbtG.contains("GroupName", 8)) { continue; }
            Map<String, List<RecipeCarpentry>> map = nbtG.getBoolean("isGlobal") ? globalRecipes : anvilRecipes;
            if (!map.containsKey(nbtG.getString("GroupName"))) { map.put(nbtG.getString("GroupName"), new ArrayList<>()); }
            for (int j = 0; j < nbtG.getList("Recipes", 10).size(); j++) {
               map.get(nbtG.getString("GroupName"))
                       .add(RecipeCarpentry.create(nbtG.getList("Recipes", 10).getCompound(j)));
            }
         }
      }
      else {
         for (int i = 0; i < 2; i++) {
            CompoundTag groups = compound.getCompound(i == 0 ? "GlobalRecipes" : "AnvilRecipes");
            for(String group : groups.getAllKeys()) {
               ListTag list = groups.getList(group, 10);
               for(int j = 0; j < list.size(); ++j) {
                  RecipeCarpentry recipe = RecipeCarpentry.create(list.getCompound(j));
                  Map<String, List<RecipeCarpentry>> map = i == 0 ? globalRecipes : anvilRecipes;
                  if (!map.containsKey(group)) { map.put(group, new ArrayList<>()); }
                  map.get(group).add(recipe);
               }
            }
         }
      }
      loadDefaultRecipes(version);
   }

   private void saveCategories() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         CompoundTag compound = new CompoundTag();
         compound.putInt("Version", version);
         for (int i = 0; i < 2; i++) {
            Map<String, List<RecipeCarpentry>> map = i == 0 ? globalRecipes : anvilRecipes;
            CompoundTag groups = new CompoundTag();
            for(Map.Entry<String, List<RecipeCarpentry>> entry : map.entrySet()) {
               ListTag list = new ListTag();
               for(RecipeCarpentry recipe : new ArrayList<>(entry.getValue())) { list.add(recipe.saveTo()); }
               groups.put(entry.getKey(), list);
            }
            compound.put(i == 0 ? "GlobalRecipes" : "AnvilRecipes", groups);
         }
         File file = new File(saveDir, "recipes.dat_new");
         File file1 = new File(saveDir, "recipes.dat_old");
         File file2 = new File(saveDir, "recipes.dat");
         NbtIo.writeCompressed(compound, new FileOutputStream(file));
         //if (CustomNpcs.VerboseDebug) { NBTJsonUtil.SaveFile(new File(saveDir, "recipes.json"), compound); }
         if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
         if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
         if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
      }
      catch (Exception e) { LogWriter.error(e); }
   }

   public RecipeCarpentry getRecipe(ResourceLocation id) {
      for (int i = 0; i < 2; i++) {
         for (Map.Entry<String, List<RecipeCarpentry>> entry : (i == 0 ? globalRecipes : anvilRecipes).entrySet()) {
            for(RecipeCarpentry recipe : new ArrayList<>(entry.getValue())) {
               if (recipe.getId().equals(id)) { return recipe; }
            }
         }
      }
      return null;
   }

   public RecipeCarpentry addAndSaveRecipe(RecipeCarpentry recipe) {
      if (recipe.getGroup().isEmpty()) { recipe.setGroup("npc_default"); }
      Map<String, List<RecipeCarpentry>> parent = recipe.isGlobal ? globalRecipes : anvilRecipes;
      Map<String, List<RecipeCarpentry>> invert = recipe.isGlobal ? anvilRecipes : globalRecipes;
      if (!parent.containsKey(recipe.getGroup())) { parent.put(recipe.getGroup(), new ArrayList<>()); }
      boolean found = false;
      for (RecipeCarpentry r : new ArrayList<>(parent.get(recipe.getGroup()))) {
         if (r.getId().equals(recipe.getId())) {
            r.loadFrom(recipe.saveTo());
            recipe = r;
            found = true;
            break;
         }
      }
      if (!found) { parent.get(recipe.getGroup()).add(recipe); }
      for (Map.Entry<String, List<RecipeCarpentry>> entry : new ArrayList<>(invert.entrySet())) {
         for (RecipeCarpentry r : new ArrayList<>(entry.getValue())) {
            if (r.getId().equals(recipe.getId())) {
               entry.getValue().remove(r);
               break;
            }
         }
         if (entry.getValue().isEmpty()) { invert.remove(entry.getKey()); }
      }
      saveCategories();
      updateToAll();
      return recipe;
   }

   public boolean delete(ResourceLocation id) {
      if (CustomNpcs.Server != null) {
         RecipeCarpentry recipe = getRecipe(id);
         if (recipe != null) {
            List<RecipeCarpentry> list;
            boolean isRemoved = false;
            if (globalRecipes.containsKey(recipe.getGroup())) {
               list = globalRecipes.get(recipe.getGroup());
               isRemoved = list.remove(recipe);
               if (list.isEmpty()) { globalRecipes.remove(recipe.getGroup()); }
            }
            if (anvilRecipes.containsKey(recipe.getGroup())) {
               list = anvilRecipes.get(recipe.getGroup());
               isRemoved = isRemoved || list.remove(recipe);
               if (list.isEmpty()) { anvilRecipes.remove(recipe.getGroup()); }
            }
            if (isRemoved) {
               saveCategories();
               updateToAll();
            }
            return isRemoved;
         }
      }
      return false;
   }

   public void sendTo(ServerPlayer player) {
      // global recipes
      for (int i = 11; i < 13; i++) {
         for (List<RecipeCarpentry> group : new ArrayList<>((i == 11 ? globalRecipes : anvilRecipes).values())) {
            for (RecipeCarpentry recipe : new ArrayList<>(group)) {
               Packets.send(player, new PacketSync(i, recipe.saveTo(), false));
            }
         }
         Packets.send(player, new PacketSync(i, new CompoundTag(), true));
      }
      Packets.send(player, new PacketSync(18, new CompoundTag(), false));
   }

   public List<Recipe<?>> getKnownRecipes() {
      List<Recipe<?>> list = new ArrayList<>();
      RecipeManager manager = CustomNpcs.proxy.getRecipeManager();
      if (manager != null) {
         List<CraftingRecipe> crafting = manager.getAllRecipesFor(RecipeType.CRAFTING);
         for (int i = 0; i < 2; i++) {
            for (List<RecipeCarpentry> group : (i == 0 ? globalRecipes.values() : anvilRecipes.values())) {
               for (RecipeCarpentry recipe : new ArrayList<>(group)) {
                  if (recipe.isKnown() && crafting.contains(recipe)) { list.add(recipe); }
               }
            }
         }
      }
      return list;
   }

   public void deleteGroup(boolean isGlobal, String group) {
      if (CustomNpcs.Server != null) {
         Map<String, List<RecipeCarpentry>> map = (isGlobal ? globalRecipes : anvilRecipes);
         if (map.containsKey(group)) {
            map.remove(group);
            updateToAll();
         }
      }
   }

   public void addGroup(boolean isGlobal, @Nonnull String group) {
      if (CustomNpcs.Server != null) {
         Map<String, List<RecipeCarpentry>> map = (isGlobal ? globalRecipes : anvilRecipes);
         while (map.containsKey(group)) { group += "_"; }
         map.put(group, new ArrayList<>());
         map.get(group).add(createNewRecipe(isGlobal, group));
         updateToAll();
      }
   }

   private RecipeCarpentry createNewRecipe(boolean isGlobal, String group) {
      String name = "new";
      while (containsName(name)) { name += "_"; }
      return new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), group,
              isGlobal ? 3 : 4, isGlobal ? 3 : 4, isGlobal, true,
              NonNullList.create(),
              new ItemStack(Blocks.COBBLESTONE));
   }

   public void renameGroup(boolean isGlobal, @Nonnull String oldGroup, @Nonnull String newGroup) {
      if (CustomNpcs.Server != null) {
         Map<String, List<RecipeCarpentry>> map = (isGlobal ? globalRecipes : anvilRecipes);
         while (map.containsKey(newGroup)) { newGroup += "_"; }
         List<RecipeCarpentry> list = new ArrayList<>();
         if (map.containsKey(oldGroup)) {
            list = map.get(oldGroup);
            map.remove(oldGroup);
         }
         if (list.isEmpty()) { list.add(createNewRecipe(isGlobal, newGroup)); }
         else {
            for (RecipeCarpentry r : list) { r.setGroup(newGroup); }
         }
         map.put(newGroup, list);
      }
   }

   public void renameRecipe(@Nonnull String oldName, @Nonnull String newName) {
      if (CustomNpcs.Server != null) {
         RecipeCarpentry recipe = getRecipe(new ResourceLocation(CustomNpcs.MODID, oldName));
         if (recipe != null) {
            while (containsName(newName)) { newName += "_"; }
            recipe.setId(new ResourceLocation(CustomNpcs.MODID, newName));
         }
      }
   }

   public void updateToAll() {
      if (CustomNpcs.Server != null) {
         for (ServerPlayer pl : CustomNpcs.Server.getPlayerList().getPlayers()) {
            CustomNpcs.proxy.syncRecipeManager();
            sendTo(pl);
         } // update all
      }
   }

   public boolean containsName(@Nonnull String name) {
      for (int i = 0; i < 2; i++) {
         for (Map.Entry<String, List<RecipeCarpentry>> entry : (i == 0 ? globalRecipes : anvilRecipes).entrySet()) {
            for(RecipeCarpentry recipe : new ArrayList<>(entry.getValue())) {
               if (recipe.getId().getPath().equals(name)) { return true; }
            }
         }
      }
      return false;
   }

   public List<String> getGroups(boolean isGlobal) {
      return new ArrayList<>(isGlobal ? globalRecipes.keySet() : anvilRecipes.keySet());
   }

}
