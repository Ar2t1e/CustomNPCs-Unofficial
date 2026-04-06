package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;

import javax.annotation.Nonnull;
import java.util.ArrayList;

// net.minecraft.client.gui.screens.inventory.InventoryScreen
public class GuiNpcCarpentryBench extends GuiContainerNPCInterface<ContainerCarpentryBench> {

   protected static final ResourceLocation resource = getResource("carpentry.png");
   protected GuiButtonNop button;

   // New from Unofficial (BetaZavr)
   protected final ResourceLocation buttonTexture = new ResourceLocation("minecraft", "textures/gui/recipe_button.png");
   // from GuiCrafting
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public GuiNpcCarpentryBench(ContainerCarpentryBench container, Inventory inv, Component titleIn) {
      super(null, container, inv, titleIn);
      imageHeight = 180;
      //titleLabelX = 97;
   }

   @Override
   public void init() {
      super.init();
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }

      widthTooNarrow = width < 379;
      recipeBookComponent.init(width, height, minecraft, widthTooNarrow, menu);
      guiLeft = recipeBookComponent.updateScreenPosition(width, imageWidth);
      button = new GuiButtonNop(this, 10, "", guiLeft + 5, height / 2 - 49, (b) -> {
         recipeBookComponent.toggleVisibility();
         guiLeft = recipeBookComponent.updateScreenPosition(width, imageWidth);
         b.setPosition(guiLeft + 5, height / 2 - 49);
      })
              .setSize(20, 19)
              .setTexture(buttonTexture)
              .setUV(0, 0, 20 ,19);
      button.isSimple = true;
      add(button);
      setInitialFocus(recipeBookComponent);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      setScreen(new GuiRecipes());
   }

   @Override
   public void containerTick() { recipeBookComponent.tick(); }

   @Override
   protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
      int x = titleLabelX + (recipeBookComponent.isVisible() ? 77 : 0);
      int y = titleLabelY - 2;
      graphics.drawString(font, Component.translatable("tile.npccarpentybench.name"), x, y, CustomNpcResourceListener.DefaultTextColor, false);
      graphics.drawString(font, Component.translatable("container.inventory"), x, y + 84, CustomNpcResourceListener.DefaultTextColor, false);
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      wrapper.graphics = graphics;
      wrapper.mouseX = mouseX;
      wrapper.mouseY = mouseY;
      ArrayList<Slot> slots = new ArrayList<>(menu.slots);
      int x = mouseX;
      int y = mouseY;
      if (hasSubGui()) {
         menu.slots.clear();
         x = 0;
         y = 0;
      }

      renderBackground(graphics);
      if (recipeBookComponent.isVisible() && widthTooNarrow) {
         renderBg(graphics, partialTicks, mouseX, mouseY);
         recipeBookComponent.render(graphics, mouseX, mouseY, partialTicks);
      } else {
         recipeBookComponent.render(graphics, mouseX, mouseY, partialTicks);
         super.render(graphics, mouseX, mouseY, partialTicks);
         recipeBookComponent.renderGhostRecipe(graphics, guiLeft, guiTop, false, partialTicks);
      }
      renderTooltip(graphics, mouseX, mouseY);
      recipeBookComponent.renderTooltip(graphics, guiLeft, guiTop, mouseX, mouseY);

      for (IComponentGui component : new ArrayList<>(wrapper.components)) {
         if (component instanceof Renderable renderable) { renderable.render(graphics, x, y, partialTicks); }
      }
      if (hasSubGui()) {
         menu.slots.addAll(slots);
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 100.0F);
         wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
         graphics.pose().popPose();
      }
      else { renderTooltip(graphics, mouseX, mouseY); }
   }

   @Override
   protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      menu.checkPos(recipeBookComponent.isVisible());
      graphics.blit(resource, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
      super.renderBg(graphics, partialTicks, mouseX, mouseY);
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
   }

   @Override
   protected boolean isHovering(int x, int y, int widthIn, int heightIn, double mouseX, double mouseY) {
      return (!widthTooNarrow || !recipeBookComponent.isVisible()) && super.isHovering(x, y, widthIn, heightIn, mouseX, mouseY);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (recipeBookComponent.mouseClicked(mouseX, mouseY, mouseButton)) {
         setFocused(recipeBookComponent);
         return true;
      }
      return (!widthTooNarrow || !recipeBookComponent.isVisible()) && super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      if (buttonClicked) {
         buttonClicked = false;
         return true;
      }
      return super.mouseReleased(mouseX, mouseY, mouseButton);
   }

   @Override
   protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int mouseButton) {
      boolean flag = mouseX < (double) left || mouseY < (double) top || mouseX >= (double)(left + imageWidth) || mouseY >= (double)(top + imageHeight);
      return recipeBookComponent.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, imageWidth, imageHeight, mouseButton) && flag;
   }

   @Override
   protected void slotClicked(@Nonnull Slot slot, int mouseX, int mouseY, @Nonnull ClickType type) {
      super.slotClicked(slot, mouseX, mouseY, type);
      recipeBookComponent.slotClicked(slot);
   }

   public void recipesUpdated() { recipeBookComponent.recipesUpdated(); }

   public RecipeBookComponent getRecipeBookComponent() { return recipeBookComponent; }

}
