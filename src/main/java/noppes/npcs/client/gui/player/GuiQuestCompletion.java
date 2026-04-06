package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;

public class GuiQuestCompletion extends GuiNPCInterface implements ITopButtonListener {

   protected final IQuest quest;
   protected final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/smallbg.png");

   public GuiQuestCompletion(int questId) {
      super();
      imageWidth = 176;
      imageHeight = 222;
      drawDefaultBackground = false;
      closeOnEsc = false;

      quest = QuestController.instance.get(questId);
   }

   @Override
   public void init() {
      super.init();
      int left = (imageWidth - font.width(Component.translatable(quest.getName()))) / 2;
      addLabel(0, guiLeft + left, guiTop + 4, Component.translatable(quest.getName()));
      addButton(0, guiLeft + 38, guiTop + imageHeight - 24, "quest.complete")
              .setSize(100, 20);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      renderBackground(graphics);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, resource);
      graphics.blit(resource, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
      graphics.hLine(guiLeft + 4, guiLeft + 170, guiTop + 13, -16777216 + CustomNpcResourceListener.DefaultTextColor);
      drawQuestText(graphics);
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

   private void drawQuestText(GuiGraphics graphics) {
      int x = guiLeft + 4;
      TextBlockClient block = new TextBlockClient(Component.translatable(quest.getCompleteText()).getString(), 172, true, player);
      for(int i = 0; i < block.lines.size(); ++i) {
         String text = block.lines.get(i).getString();
         graphics.drawString(font, text, x, guiTop + 16 + i * 9, CustomNpcResourceListener.DefaultTextColor, false);
      }

   }

   @Override
   public void buttonEvent(GuiButtonNop guiButton) {
      if (guiButton.id == 0) {
         Packets.sendServer(new SPacketQuestCompletionCheck(quest.getId(), ItemStack.EMPTY));
         onClose();
      }
   }

}
