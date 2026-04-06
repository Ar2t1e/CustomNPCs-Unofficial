package noppes.npcs.client.gui.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.GuiBasic;
import org.joml.Matrix4f;

public abstract class GuiNPCInterface extends GuiBasic {

   public EntityNPCInterface npc;

   public GuiNPCInterface(EntityNPCInterface npcIn) {
      this();
      npc = npcIn;
   }

   public GuiNPCInterface() {
      super();
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
   }

   @Override
   public void setSubGui(Screen gui) {
      if (gui instanceof GuiNPCInterface guiNpc) { guiNpc.npc = this.npc; }
      if (gui instanceof GuiContainerNPCInterface<?> guiCont) { guiCont.npc = npc; }
      super.setSubGui(gui);
   }

   public void drawNpc(GuiGraphics graphics, int x, int y) {
      if (npc == null) { return; }
      drawNpc(graphics, npc, x, y, 1.0F, 0, 0, 0);
   }

   public static void fill(GuiGraphics graphics, float left, float top, float right, float bottom, int colorLeft, int colorRight) {
      VertexConsumer vertexconsumer = graphics.bufferSource().getBuffer(RenderType.gui());
      float leftA = (float) FastColor.ARGB32.alpha(colorLeft) / 255.0F;
      float leftR = (float) FastColor.ARGB32.red(colorLeft) / 255.0F;
      float leftG = (float) FastColor.ARGB32.green(colorLeft) / 255.0F;
      float leftB = (float) FastColor.ARGB32.blue(colorLeft) / 255.0F;

      float rightA = (float) FastColor.ARGB32.alpha(colorRight) / 255.0F;
      float rightR = (float) FastColor.ARGB32.red(colorRight) / 255.0F;
      float rightG = (float) FastColor.ARGB32.green(colorRight) / 255.0F;
      float rightB = (float) FastColor.ARGB32.blue(colorRight) / 255.0F;

      Matrix4f matrix4f = graphics.pose().last().pose();
      vertexconsumer.vertex(matrix4f, left, top, 0.0f).color(leftR, leftG, leftB, leftA).endVertex();
      vertexconsumer.vertex(matrix4f, left, bottom, 0.0f).color(leftR, leftG, leftB, leftA).endVertex();
      vertexconsumer.vertex(matrix4f, right, bottom, 0.0f).color(rightR, rightG, rightB, rightA).endVertex();
      vertexconsumer.vertex(matrix4f, right, top, 0.0f).color(rightR, rightG, rightB, rightA).endVertex();
   }

}
