package noppes.npcs.client.gui.global;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketRecipeGet;
import noppes.npcs.packets.server.SPacketRecipeRemove;
import noppes.npcs.packets.server.SPacketRecipeSave;
import noppes.npcs.packets.server.SPacketRecipesGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import org.jetbrains.annotations.NotNull;

public class GuiNpcManageRecipes
        extends GuiContainerNPCInterface2<ContainerManageRecipes>
        implements IScrollData, IGuiData, ICustomScrollListener, ITextfieldListener {

   protected GuiCustomScrollNop scroll;
   protected final Map<Component, ResourceLocation> data = new HashMap<>();
   protected final ContainerManageRecipes container;
   protected final ResourceLocation slot;

   public GuiNpcManageRecipes(ContainerManageRecipes containerIn, Inventory inv, Component titleIn) {
      super(NoppesUtilServer.getEditingNpc(Minecraft.getInstance().player), containerIn, inv, titleIn);
      setBackground("inventorymenu.png");
      drawDefaultBackground = false;
      imageHeight = 200;

      backGui = EnumGuiType.MainMenuGlobal;

      container = containerIn;
      slot = getResource("slot.png");

      Packets.sendServer(new SPacketRecipesGet(containerIn.width));
   }

   @Override
   public void init() {
      super.init();
      int x0 = guiLeft + 81;
      int x1 = x0 + 33;
      int x2 = x1 + 58;
      int x3 = x1 + 190;
      int y = guiTop + 8;
      if (scroll == null) { scroll = addScroll(0).setSize(130, 180); }
      add(scroll.setPos(x2, guiTop + 8));
      addButton(0, x3, y, "menu.global")
              .setSize(84, 20)
              .setIsEnabled(container.width == 4);
      addButton(1, x3, y += 22, Component.translatable("block." + CustomNpcs.MODID + ".npccarpentybench"))
              .setSize(84, 20)
              .setIsEnabled(container.width == 3);
      addButton(3, x3, y += 22, "gui.add")
              .setSize(84, 20);
      addButton(4, x3, y + 22, "gui.remove")
              .setIsEnabled(scroll.hasSelected())
              .setSize(84, 20);
      y = guiTop + 40;
      addLabel(0, x0, y - 10, "gui.ignoreDamage")
              .setSize(88, 10);
      addYesNo(5, x1, y, container.recipe.ignoreDamage)
              .setSize(50, 18)
              .setIsEnabled(false);
      y = guiTop + 90;
      addLabel(1, x0, y - 10, "gui.ignoreNBT")
              .setSize(88, 10);
      addYesNo(6, x1, y, container.recipe.ignoreNBT)
              .setSize(50, 18)
              .setIsEnabled(false);

      addTextField(0, guiLeft + 7, guiTop + 9, 161, 18, container.recipe.name)
              .setResourceLocationType(2)
              .setIsEnabled(false);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0: {
            save();
            NoppesUtil.requestOpenGUI(EnumGuiType.ManageRecipes, new BlockPos(3, 0, 0));
            break;
         }
         case 1: {
            save();
            NoppesUtil.requestOpenGUI(EnumGuiType.ManageRecipes, new BlockPos(4, 0, 0));
            break;
         }
         case 3: {
            save();
            scroll.clear();
            String name = "new";
            while (true) {
               boolean found = false;
               for (Component key : data.keySet()) {
                  if (key.getString().equals(name)) {
                     found = true;
                     name += "_";
                     break;
                  }
               }
               if (!found) { break; }
            }
            name = NoppesUtilServer.validPath(name);
            RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name), name);
            recipe.isGlobal = container.width == 3;
            Packets.sendServer(new SPacketRecipeSave(recipe.writeNBT()));
            break;
         }
         case 4: {
            if (data.containsKey(scroll.getNormalSelected())) {
               Packets.sendServer(new SPacketRecipeRemove(data.get(scroll.getNormalSelected())));
               scroll.clear();
            }
            break;
         }
         case 5: container.recipe.ignoreDamage = ((GuiButtonYesNo) button).getBoolean(); break;
         case 6: container.recipe.ignoreNBT = ((GuiButtonYesNo) button).getBoolean(); break;
      }
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      RecipeCarpentry recipe = RecipeCarpentry.load(compound);
      container.setRecipe(recipe, player.level().registryAccess());
      getTextField(0).setIsEnabled(true)
              .setValue(recipe.name);
      getButton(5).setIsEnabled(true)
              .setDisplay(recipe.ignoreDamage ? 1 : 0);
      getButton(6).setIsEnabled(true)
              .setDisplay(recipe.ignoreNBT ? 1 : 0);
      setSelected(recipe.name);
   }

   @Override
   protected void renderBg(@NotNull GuiGraphics graphics, float f, int x, int y) {
      super.renderBg(graphics, f, x, y);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, slot);
      for(int i = 0; i < container.width; ++i) {
         for(int j = 0; j < container.width; ++j) {
            graphics.blit(slot, guiLeft + i * 18 + 7, guiTop + j * 18 + 34, 0, 0, 18, 18);
         }
      }
      graphics.blit(slot, guiLeft + 86, guiTop + 60, 0, 0, 18, 18);
   }

   @Override
   public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
      Component name = scroll.getNormalSelected();
      data.clear();
      for (String res : dataMap.keySet()) {
         String[] arr = res.split("\\|");
         MutableComponent key = Component.literal(arr[0]);
         if (arr[2].equals("false")) { key.withStyle(ChatFormatting.RED); }
         data.put(key, new ResourceLocation(arr[1]));
      }
      scroll.setNormalList(new ArrayList<>(data.keySet()));
      if (getTextField(0) != null) { getTextField(0).setIsEnabled(!name.getString().isEmpty()); }
      if (getButton(5) != null) { getButton(5).setIsEnabled(!name.getString().isEmpty()); }
      scroll.setSelectedIndex(name);
   }

   @Override
   public void setSelected(String selectedIn) { scroll.setSelectedIndex(selectedIn); }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      if (getButton(4) != null) { getButton(4).setIsEnabled(scroll.hasSelected()); }
      save();
      if (data.containsKey(scroll.getNormalSelected())) {
         Packets.sendServer(new SPacketRecipeGet(data.get(scroll.getNormalSelected())));
      }
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

   @Override
   public void save() {
      GuiTextFieldNop.unfocus();
      if (!container.recipe.name.isEmpty()) {
         container.saveRecipe();
         Packets.sendServer(new SPacketRecipeSave(container.recipe.writeNBT()));
      }
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      String name = textField.getValue();
      if (!name.isEmpty()) {
         String old = container.recipe.name;
         for (Component key : new ArrayList<>(data.keySet())) {
            if (key.getString().equals(old)) {
               data.remove(key);
               break;
            }
         }
         container.recipe.name = name;
         data.put(Component.literal(name), new ResourceLocation(CustomNpcs.MODID, container.recipe.name));
         scroll.replace(old, container.recipe.name);
      }

   }

}
