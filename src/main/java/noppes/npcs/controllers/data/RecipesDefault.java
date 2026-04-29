package noppes.npcs.controllers.data;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.RecipeController;

import javax.annotation.Nonnull;

// Changed by Unofficial (BetaZavr)
public class RecipesDefault {

   /**
    * @param nameIn - ResourceLocation path name
    * @param group - ResourceLocation namespace name
    * @param result - product
    * @param isShaped - strict recipe according to the crafting table
    * @param isKnown - players can know it without being detected
    * @param wight - grid wight
    * @param height - grid height
    * @param stacks - [slot ID][ingredients]
    */
   public static void addRecipe(String nameIn, String group, @Nonnull ItemStack result, boolean isShaped, boolean isKnown, int wight, int height, @Nonnull ItemStack[][] stacks) {
      String name = NoppesUtilServer.validPath(nameIn);
      RecipeController rData = RecipeController.getInstance();
      while (rData.containsName(name)) { name += "_"; }
      NonNullList<Ingredient> ingredients = NonNullList.create();
      for (int slotId = 0; slotId < stacks.length; slotId ++) { ingredients.add(slotId, Ingredient.of(stacks[slotId])); }
      RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), group, wight, height,
              wight <= 3 && height <= 3, isShaped, ingredients, result);
      recipe.isKnown = isKnown;
      recipe.availability.clear();
      if (name.equals("npc_soul_stone")) { recipe.availability.setDaytime(1); }
      else if (recipe.isGlobal) { recipe.availability.setGMOnly(true); }
      RecipeController.getInstance().addAndSaveRecipe(recipe);
   }

   public static void loadDefaultRecipes(int versionIn) {
      if (versionIn < 0) {
         ItemStack bread = new ItemStack(Items.BREAD);
         ItemStack stick = new ItemStack(Items.STICK);
         ItemStack potato = new ItemStack(Items.POTATO);
         ItemStack carrot = new ItemStack(Items.CARROT);
         ItemStack paper = new ItemStack(Items.PAPER);
         ItemStack empty = ItemStack.EMPTY;
         // wand
         ItemStack[][] stacks = new ItemStack[6][];
         stacks[0] = new ItemStack[] { bread }; stacks[1] = new ItemStack[] { bread };
         stacks[2] = new ItemStack[] { empty }; stacks[3] = new ItemStack[] { stick };
         stacks[4] = new ItemStack[] { empty }; stacks[5] = new ItemStack[] { stick };
         addRecipe("npc_wand", "npc_wand", new ItemStack(CustomItems.wand), true, true, 2, 3, stacks);
         // cloner
         stacks = new ItemStack[6][];
         ItemStack[] variants = new ItemStack[] { bread, potato, carrot };
         stacks[0] = variants; stacks[1] = variants;
         stacks[2] = variants; stacks[3] = new ItemStack[] { stick };
         stacks[4] = new ItemStack[] { empty }; stacks[5] = new ItemStack[] { stick };
         addRecipe("npc_cloner", "npc_cloner", new ItemStack(CustomItems.cloner), true, true, 2, 3, stacks);
         // soul stone
         stacks = new ItemStack[4][];
         stacks[0] = new ItemStack[] { new ItemStack(Items.DIAMOND) };
         stacks[1] = new ItemStack[] { new ItemStack(Items.GLOWSTONE_DUST, 6) };
         stacks[2] = new ItemStack[] { new ItemStack(Items.REDSTONE, 8) };
         stacks[3] = new ItemStack[] { new ItemStack(Items.LAPIS_LAZULI, 2) };
         addRecipe("npc_soul_stone", "npc_soul_stone", new ItemStack(CustomItems.soulstoneEmpty), false, true, 2, 2, stacks);
         // metal mailbox
         stacks = new ItemStack[16][];
         variants = new ItemStack[] { new ItemStack(Items.IRON_INGOT) };
         stacks[0] = variants; stacks[1] = variants; stacks[2] = variants; stacks[3] = variants;
         stacks[4] = variants; stacks[5] = new ItemStack[] { paper }; stacks[6] = new ItemStack[] { new ItemStack(Items.WHITE_DYE) }; stacks[7] = variants;
         stacks[8] = variants; stacks[9] = variants; stacks[10] = variants; stacks[11] = variants;
         stacks[12] = new ItemStack[] { new ItemStack(Items.LAPIS_LAZULI, 3) }; stacks[13] = new ItemStack[] { empty }; stacks[14] = new ItemStack[] { empty }; stacks[15] = new ItemStack[] { new ItemStack(Items.LAPIS_LAZULI, 3) };
         addRecipe("npc_mailbox_metal", "npc_mailbox", new ItemStack(CustomBlocks.mailbox_item), true, true, 4, 4, stacks);
         // stone mailbox
         variants = new ItemStack[] { new ItemStack(Items.STONE) };
         stacks = new ItemStack[12][];
         stacks[0] = variants; stacks[1] = variants; stacks[2] = variants;
         stacks[3] = variants; stacks[4] = new ItemStack[] { paper }; stacks[5] = variants;
         stacks[6] = variants; stacks[7] = variants; stacks[8] = variants;
         stacks[9] = new ItemStack[] { new ItemStack(Items.RED_DYE, 2) }; stacks[10] = new ItemStack[] { new ItemStack(Blocks.COBBLESTONE) }; stacks[11] = new ItemStack[] { new ItemStack(Items.IRON_INGOT) };
         addRecipe("npc_mailbox_stone", "npc_mailbox", new ItemStack(CustomBlocks.mailbox2_item), true, true, 3, 4, stacks);
         // wooden mailbox
         variants = new ItemStack[] { new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.SPRUCE_PLANKS), new ItemStack(Items.BIRCH_PLANKS),
                 new ItemStack(Items.JUNGLE_PLANKS), new ItemStack(Items.ACACIA_PLANKS), new ItemStack(Items.DARK_OAK_PLANKS),
                 new ItemStack(Items.CHERRY_PLANKS), new ItemStack(Items.MANGROVE_PLANKS), new ItemStack(Items.BAMBOO_PLANKS),
                 new ItemStack(Items.CRIMSON_PLANKS), new ItemStack(Items.WARPED_PLANKS) };
         stacks[0] = variants; stacks[1] = variants; stacks[2] = variants;
         stacks[3] = variants; stacks[4] = new ItemStack[] { paper }; stacks[5] = variants;
         stacks[6] = variants; stacks[7] = variants; stacks[8] = variants;
         stacks[9] = new ItemStack[] { new ItemStack(Items.BLACK_DYE, 2) }; stacks[10] = new ItemStack[] { new ItemStack(Blocks.ANDESITE) }; stacks[11] = new ItemStack[] { new ItemStack(Items.IRON_INGOT) };
         addRecipe("npc_mailbox_wooden", "npc_mailbox", new ItemStack(CustomBlocks.mailbox3_item), true, true, 3, 4, stacks);
      }
   }

}
