package noppes.npcs.api.wrapper.client;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.client.renderer.obj.ParameterizedModel;
import noppes.npcs.shared.client.gui.components.custom.CustomGuiEntityDisplay;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.client.IRenderSystem;
import noppes.npcs.client.renderer.ModelBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class WrapperGlStateManager
implements IRenderSystem {

	private final Minecraft minecraft;
	private static final Map<String, ParameterizedModel> cache = new HashMap<>();
	
	public WrapperGlStateManager(Minecraft mc) { this.minecraft = mc; }

	@Override
	public void enableBlend() { GlStateManager.enableBlend(); }

	@Override
	public void disableBlend() { GlStateManager.disableBlend(); }

	@Override
	public void enableAlpha() { GlStateManager.enableAlpha(); }

	@Override
	public void disableAlpha() { GlStateManager.disableAlpha(); }

	@Override
	public void pushMatrix() { GlStateManager.pushMatrix(); }

	@Override
	public void popMatrix() { GlStateManager.popMatrix(); }

	@Override
	public void color(float red, float green, float blue, float alpha) { GlStateManager.color(red, green, blue, alpha); }

	@Override
	public void translate(float x, float y, float z) { GlStateManager.translate(x, y, z); }

	@Override
	public void scale(float x, float y, float z) { GlStateManager.scale(x, y, z); }

	@Override
	public void rotate(float angle, float x, float y, float z) { GlStateManager.rotate(angle, x, y, z); }
	
	@Override
	public void drawString(String text, float u, float y, int color, boolean dropShadow) {
		if (text == null || !text.isEmpty()) { return; }
		this.minecraft.fontRenderer.drawString(text, u, y, color, dropShadow);
	}

	@Override
	public void draw(double left, double top, double width, double height, int color, float alpha) {
		this.draw(left, top, width, height, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
	}
	
	@Override
	public void draw(double left, double top, double width, double height, float red, float green, float blue, float alpha) {
		if (alpha <= 0.0f) { return; } else if (alpha > 1.0f) { alpha = 1.0f; }
		if (red < 0.0f) { red = 0.0f; } else if (red > 1.0f) { red = 1.0f; }
		if (green < 0.0f) { green = 0.0f; } else if (green > 1.0f) { green = 1.0f; }
		if (blue < 0.0f) { blue = 0.0f; } else if (blue > 1.0f) { blue = 1.0f; }
		
		double right = left + width;
		double bottom = top + height;
		
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
	
	@Override
	public void drawTexture(String resourceLocation, double x, double y, double z, double u, double v, double width, double height, boolean revers) {
		if (resourceLocation == null || resourceLocation.isEmpty()) { return; }
		ResourceLocation loc = new ResourceLocation(resourceLocation);
		minecraft.getTextureManager().bindTexture(loc);
        minecraft.getTextureManager().getTexture(loc);
        float w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		float h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		if (w > 256.0f) {
			w = 256.0f;
			width *= 256.0f / w;
		}
		if (h > 256.0f) {
			h = 256.0f;
			height *= 256.0f / h;
		}
		float f = 1.0f / w;
		float f1 = 1.0f / h;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		double us = (revers ? u + width : u) * f;
		double ue = (revers ? u : u + width) * f;
		bufferbuilder.pos(x, y + height, z).tex(us, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y + height, z).tex(ue, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y, z).tex(ue, v * f1).endVertex();
		bufferbuilder.pos(x, y, z).tex(us, v * f1).endVertex();
		tessellator.draw();
	}

	@Override
	public void renderEntity(Entity entity, int x, int y, float zoomed, int yaw, int pitch, float guiLeft, float guiTop, int followCursor) {
		CustomGuiEntityDisplay.drawEntity(entity, x, y, zoomed, yaw, pitch, Mouse.getX(), Mouse.getY(), guiLeft, guiTop, followCursor);
	}
	
	@Override
	public void drawOBJ(String resourceLocation, List<String> visibleMeshes, Map<String, String> materialTextures) {
		if (resourceLocation == null || !resourceLocation.isEmpty()) { return; }
		Map<String, ResourceLocation> map = null;
		if (materialTextures != null) {
			map = new HashMap<>();
			for (Map.Entry<String, String> entry : materialTextures.entrySet()) {
				map.put(entry.getKey(), new ResourceLocation(entry.getValue()));
			}
		}
		String key = resourceLocation + visibleMeshes + materialTextures;
		if (cache.containsKey(key)) {
			cache.put(key,
					ModelBuffer.getParameterizedModel(new ResourceLocation(resourceLocation), visibleMeshes, map,
							false, 0, false));
		}
		ModelBuffer.render(cache.get(key));
	}

}
