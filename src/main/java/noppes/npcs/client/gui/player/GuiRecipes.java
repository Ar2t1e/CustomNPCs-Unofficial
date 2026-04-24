package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;

@OnlyIn(Dist.CLIENT)
public class GuiRecipes extends GuiNPCInterface {

   protected static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/slot.png");
   protected int page = 0;
   protected GuiLabel label;
   protected GuiButtonNop left;
   protected GuiButtonNop right;
   protected final List<INpcRecipe> recipes = new ArrayList<>();

   public GuiRecipes() {
      super();
      imageHeight = 182;
      imageWidth = 256;
      setBackground("recipes.png");
      recipes.addAll(RecipeController.getInstance().getAllAnvilRecipes());
   }

   public void init() {
      super.init();
      addLabel(0, guiLeft + 5, guiTop + 5, "Recipe List");
      label = addLabel(1, guiLeft + 5, guiTop + 168, null);
      add(left = new GuiButtonNextPage(this, 1, guiLeft + 150, guiTop + 164, true, (b) -> {
         ++page;
         updateButton();
      }));
      add(right = new GuiButtonNextPage(this, 2, guiLeft + 80, guiTop + 164, false, (b) -> {
         --page;
         updateButton();
      }));
      updateButton();
   }

   private void updateButton() {
      right.visible = right.active = page > 0;
      left.visible = left.active = page + 1 < Mth.ceil((float)recipes.size() / 4.0F);
   }

   @Override
   public void render(GuiGraphics graphics, int xMouse, int yMouse, float f) {
      super.render(graphics, xMouse, yMouse, f);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, resource);
      int var10001 = page + 1;
      label.setMessage(Component.literal(var10001 + "/" + Mth.ceil((float)recipes.size() / 4.0F)));
      label.setX(guiLeft + (256 - Minecraft.getInstance().font.width(label.getMessage())) / 2);

      int i;
      int index;
      Recipe<?> irecipe;
      int x;
      int y;
      ItemStack item;
      for(i = 0; i < 4; ++i) {
         index = i + page * 4;
         if (index >= recipes.size()) {
            break;
         }
         irecipe = (Recipe<?>) recipes.get(index);
         if (!irecipe.getResultItem(player.level().registryAccess()).isEmpty()) {
            x = guiLeft + 5 + i / 2 * 126;
            y = guiTop + 15 + i % 2 * 76;
            drawItem(graphics, irecipe.getResultItem(player.level().registryAccess()), x + 98, y + 28);
            if (irecipe instanceof RecipeCarpentry recipe) {
               x += (72 - recipe.getWidth() * 18) / 2;
               y += (72 - recipe.getHeight() * 18) / 2;
               for(int j = 0; j < recipe.getWidth(); ++j) {
                  for(int k = 0; k < recipe.getHeight(); ++k) {
                     RenderSystem.setShader(GameRenderer::getPositionTexShader);
                     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                     RenderSystem.setShaderTexture(0, resource);
                     graphics.blit(resource, x + j * 18, y + k * 18, 0, 0, 18, 18);
                     item = recipe.getCraftingItem(j + k * recipe.getWidth());
                     if (!item.isEmpty()) {
                        drawItem(graphics, item, x + j * 18 + 1, y + k * 18 + 1);
                     }
                  }
               }
            }
         }
      }
      for(i = 0; i < 4; ++i) {
         index = i + page * 4;
         if (index >= recipes.size()) {
            break;
         }
         irecipe = (Recipe<?>) recipes.get(index);
         if (irecipe instanceof RecipeCarpentry recipe) {
            if (!recipe.getResultItem(player.level().registryAccess()).isEmpty()) {
               x = guiLeft + 5 + i / 2 * 126;
               y = guiTop + 15 + i % 2 * 76;
               drawOverlay(graphics, recipe.getResultItem(player.level().registryAccess()), x + 98, y + 22, xMouse, yMouse);
               x += (72 - recipe.getWidth() * 18) / 2;
               y += (72 - recipe.getHeight() * 18) / 2;
               for(int j = 0; j < recipe.getWidth(); ++j) {
                  for(int k = 0; k < recipe.getHeight(); ++k) {
                     item = recipe.getCraftingItem(j + k * recipe.getWidth());
                     if (!item.isEmpty()) {
                        drawOverlay(graphics, item, x + j * 18 + 1, y + k * 18 + 1, xMouse, yMouse);
                     }
                  }
               }
            }
         }
      }
   }

   private void drawItem(GuiGraphics graphics, ItemStack item, int x, int y) {
      graphics.pose().pushPose();
      graphics.pose().translate(0.0D, 0.0D, 100.0D);
      graphics.renderItem(item, x, y);
      graphics.renderItemDecorations(font, item, x, y);
      graphics.pose().popPose();
   }

   private void drawOverlay(GuiGraphics graphics, ItemStack item, int x, int y, int xMouse, int yMouse) {
      if (func_146978_c(x - guiLeft, y - guiTop, xMouse, yMouse)) { graphics.renderTooltip(font, item, xMouse, yMouse); }
   }

   protected boolean func_146978_c(int x, int y, int xMouse, int yMouse) {
      xMouse -= guiLeft;
      yMouse -= guiTop;
      return xMouse >= x - 1 && xMouse < x + 16 + 1 && yMouse >= y - 1 && yMouse < y + 16 + 1;
   }

}
