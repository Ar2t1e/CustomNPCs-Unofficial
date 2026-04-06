package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.api.mixin.stats.IRecipeBookMixin;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.packets.client.PacketSyncRecipeRemove;
import noppes.npcs.packets.client.PacketSyncRecipeUpdate;
import noppes.npcs.util.Util;

// Changed by Unofficial (BetaZavr)
public class RecipeController implements IRecipeHandler {

   protected static RecipeController instance;
   public static final RecipeBookCategories CRAFTING_CUSTOM_NPC = RecipeBookCategories.create("CRAFTING_CUSTOM_NPC", new ItemStack(CustomItems.wand), new ItemStack(CustomItems.cloner));
   public static final List<RecipeBookCategories> CRAFTING_MOD_CATEGORIES = ImmutableList.of(RecipeBookCategories.CRAFTING_SEARCH, CRAFTING_CUSTOM_NPC);
   public static final int version = 3;

   protected final HashMap<ResourceLocation, RecipeCarpentry> globalRecipes = new HashMap<>();
   protected final HashMap<ResourceLocation, RecipeCarpentry> anvilRecipes = new HashMap<>();
   public final HashMap<ResourceLocation, RecipeCarpentry> syncRecipes = new HashMap<>();

   public static RecipeController getInstance() {
      if (instance == null) { instance = new RecipeController(); }
      return instance;
   }

   public void load() {
      loadCategories();
      EventHooks.onGlobalRecipesLoaded(this);
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
         } else {
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
         saveCategories();
      }
   }

   private void loadCategories(File file) throws Exception {
      CompoundTag compound1 = NbtIo.readCompressed(new FileInputStream(file));
      ListTag list = compound1.getList("Data", 10);
      globalRecipes.clear();
      anvilRecipes.clear();
      for(int i = 0; i < list.size(); ++i) {
         RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
         if (recipe.isGlobal) { globalRecipes.put(recipe.getId(), recipe); }
         else { anvilRecipes.put(recipe.getId(), recipe); }
      }
      loadDefaultRecipes(compound1.getInt("Version"));
   }

   @SuppressWarnings("all")
   private void saveCategories() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         ListTag list = new ListTag();
         Iterator<RecipeCarpentry> var3 = globalRecipes.values().iterator();
         RecipeCarpentry recipe;
         while(var3.hasNext()) {
            recipe = var3.next();
            if (recipe.savesRecipe) {
               list.add(recipe.writeNBT());
            }
         }
         var3 = anvilRecipes.values().iterator();
         while(var3.hasNext()) {
            recipe = var3.next();
            if (recipe.savesRecipe) {
               list.add(recipe.writeNBT());
            }
         }

         CompoundTag compound = new CompoundTag();
         compound.put("Data", list);
         compound.putInt("Version", 1);
         File file = new File(saveDir, "recipes.dat_new");
         File file1 = new File(saveDir, "recipes.dat_old");
         File file2 = new File(saveDir, "recipes.dat");
         NbtIo.writeCompressed(compound, new FileOutputStream(file));
         if (file1.exists()) { file1.delete(); }
         file2.renameTo(file1);
         if (file2.exists()) { file2.delete(); }
         file.renameTo(file2);
         if (file.exists()) { file.delete(); }
      }
      catch (Exception e) { LogWriter.error(e); }
   }

   public RecipeCarpentry findMatchingRecipe(CraftingContainer inventoryCrafting) {
      Iterator<RecipeCarpentry> var2 = anvilRecipes.values().iterator();
      RecipeCarpentry recipe;
      do {
         if (!var2.hasNext()) {
            return null;
         }
         recipe = var2.next();
      } while(!recipe.isValid() || !recipe.matches(inventoryCrafting, null));
      return recipe;
   }

   public RecipeCarpentry getRecipe(ResourceLocation id) {
      return globalRecipes.getOrDefault(id, anvilRecipes.getOrDefault(id, null));
   }

   public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) {
      RecipeCarpentry current = getRecipe(recipe.getId());
      if (current != null && !current.name.equals(recipe.name)) {
         while(containsRecipeName(recipe.name)) { recipe.name = recipe.name + "_"; }
      }
      if (recipe.isGlobal) {
         globalRecipes.remove(recipe.getId());
         globalRecipes.put(recipe.getId(), recipe);
      }
      else {
         anvilRecipes.remove(recipe.getId());
         anvilRecipes.put(recipe.getId(), recipe);
      }
      if (Util.instance.getSide() == Dist.DEDICATED_SERVER) {
         Packets.sendAll(new PacketSyncRecipeUpdate(recipe.getId(), recipe.isGlobal, recipe.writeNBT()));
      }
      saveCategories();
      return recipe;
   }

   private boolean containsRecipeName(String name) {
      for (RecipeCarpentry recipe : globalRecipes.values()) {
         if (recipe.name.equalsIgnoreCase(name)) { return true; }
      }
      for (RecipeCarpentry recipe : anvilRecipes.values()) {
         if (recipe.name.equalsIgnoreCase(name)) { return true; }
      }
      return false;
   }

   @Override
   public RecipeCarpentry delete(String id) { return delete(new ResourceLocation(id)); }

   public RecipeCarpentry delete(ResourceLocation id) {
      RecipeCarpentry recipe = getRecipe(id);
      if (recipe != null) {
         globalRecipes.remove(recipe.getId());
         anvilRecipes.remove(recipe.getId());
         if (Util.instance.getSide() == Dist.DEDICATED_SERVER) { Packets.sendAll(new PacketSyncRecipeRemove(id)); }
         saveCategories();
      }
      return recipe;
   }

   public List<IRecipe> getGlobalList() {
      return new ArrayList<>(globalRecipes.values());
   }

   public List<IRecipe> getCarpentryList() {
      return new ArrayList<>(anvilRecipes.values());
   }

   public IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects) {
      RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), name);
      recipe.isGlobal = global;
      recipe = RecipeCarpentry.createRecipe(new ResourceLocation(CustomNpcs.MODID, name), recipe, result, objects);
      return saveRecipe(recipe);
   }

   public IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... objects) {
      NonNullList<Ingredient> list = NonNullList.create();
      for(ItemStack item : objects) {
         if (!item.isEmpty()) { list.add(Ingredient.of(item)); }
      }
      RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), width, height, list, result);
      recipe.isGlobal = global;
      recipe.name = name;
      return saveRecipe(recipe);
   }

   public void checkSaves() {}

   public Map<ResourceLocation, RecipeCarpentry> getAnvilRecipes() { return new HashMap<>(anvilRecipes); }

   public HashMap<ResourceLocation, RecipeCarpentry> getGlobalRecipes() { return new HashMap<>(globalRecipes); }

   public void putAnvilRecipes(ResourceLocation id, RecipeCarpentry recipe) { anvilRecipes.put(id, recipe); }

   public void putGlobalRecipes(ResourceLocation id, RecipeCarpentry recipe) { globalRecipes.put(id, recipe); }

    public void sendTo(ServerPlayer player) {
       // global recipes
       ListTag list = new ListTag();
       CompoundTag compound;
       for (RecipeCarpentry category : RecipeController.getInstance().getGlobalRecipes().values()) {
          list.add(category.writeNBT());
          if (list.size() > 10) {
             compound = new CompoundTag();
             compound.put("Data", list);
             Packets.send(player, new PacketSync(11, compound, false));
             list = new ListTag();
          }
       }
       compound = new CompoundTag();
       compound.put("Data", list);
       Packets.send(player, new PacketSync(11, compound, true));

       // mod recipes
       list = new ListTag();
       for (RecipeCarpentry category : RecipeController.getInstance().getAnvilRecipes().values()) {
          list.add(category.writeNBT());
          if (list.size() > 10) {
             compound = new CompoundTag();
             compound.put("Data", list);
             Packets.send(player, new PacketSync(12, compound, false));
             list = new ListTag();
          }
       }
       compound = new CompoundTag();
       compound.put("Data", list);
       Packets.send(player, new PacketSync(12, compound, true));
    }

   public void checkRecipeBook(ServerPlayer player) {
      if (player != null && ((IRecipeBookMixin) player.getRecipeBook()).npcs$checkRecipes()) {
         player.getRecipeBook().sendInitialRecipeBook(player); // send ClientboundRecipePacket
      }
   }

   public List<Recipe<?>> getKnownRecipes() {
      List<Recipe<?>> list = new ArrayList<>();
      for (int i = 0; i < 2; i++) {
         /*for (INpcRecipe recipe : (i == 0 ? globalRecipes.values() : anvilRecipes.values())) {
            if (recipe.isKnown() && RecipeController.Registry.getValue(((IRecipe) recipe).getRegistryName()) != null) {
               list.add((IRecipe) recipe);
            }
         }*/
      }
      return list;
   }

   public List<RecipeBookCategories> getCategories(boolean isMod) {
      LogWriter.info("TEST: "+CRAFTING_MOD_CATEGORIES+" / "+RecipeBookCategories.CRAFTING_CATEGORIES);
      LogWriter.info("TEST: keySet "+RecipeBookCategories.AGGREGATE_CATEGORIES.keySet());
      return isMod ? CRAFTING_MOD_CATEGORIES : RecipeBookCategories.CRAFTING_CATEGORIES;
   }

}
