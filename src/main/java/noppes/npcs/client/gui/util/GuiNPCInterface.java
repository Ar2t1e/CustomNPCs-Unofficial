package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.client.gui.GuiBasic;

import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

public abstract class GuiNPCInterface extends GuiBasic {

	public EntityNPCInterface npc;

	public GuiNPCInterface(EntityNPCInterface npcIn) {
		this();
		npc = npcIn;
		if (npc == null) { npc = NoppesUtilServer.getEditingNpc(player); }
	}

	public GuiNPCInterface() {
		super();
	}

	@Override
	public void setSubGui(GuiScreen gui) {
		if (gui instanceof GuiNPCInterface) { ((GuiNPCInterface) gui).npc = npc; }
		if (gui instanceof GuiContainerNPCInterface) { ((GuiContainerNPCInterface<?>) gui).npc = npc; }
		super.setSubGui(gui);
	}

	public void drawNpc(int x, int y) {
		if (npc == null) { return; }
		drawNpc(npc, x, y, 1.0F, 0, 0, 0);
	}

	public static void fill(int left, int top, int right, int bottom, float zLevel, int startColor, int endColor) {
		float alpha_0 = (float)(startColor >> 24 & 255) / 255.0F;
		float red_0 = (float)(startColor >> 16 & 255) / 255.0F;
		float green_0 = (float)(startColor >> 8 & 255) / 255.0F;
		float blue_0 = (float)(startColor & 255) / 255.0F;
		float alpha_1 = (float)(endColor >> 24 & 255) / 255.0F;
		float red_1 = (float)(endColor >> 16 & 255) / 255.0F;
		float green_1 = (float)(endColor >> 8 & 255) / 255.0F;
		float blue_1 = (float)(endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(right, top, zLevel).color(red_1, green_1, blue_1, alpha_1).endVertex();
		bufferbuilder.pos(left, top, zLevel).color(red_0, green_0, blue_0, alpha_0).endVertex();
		bufferbuilder.pos(left, bottom, zLevel).color(red_0, green_0, blue_0, alpha_0).endVertex();
		bufferbuilder.pos(right, bottom, zLevel).color(red_1, green_1, blue_1, alpha_1).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

}