package noppes.npcs.client.renderer;

import java.util.HashMap;
import java.util.Map;

import noppes.npcs.api.constants.MarkType;
import noppes.npcs.entity.EntityNPCInterface;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.controllers.data.MarkData;

public class MarkRenderer {

	public static final Map<Boolean, Map<MarkType, ResourceLocation>> locations = new HashMap<>();
	static {
		locations.put(true, new HashMap<>());
		locations.put(false, new HashMap<>());
		for (MarkType mt : MarkType.values()) {
			locations.get(true).put(mt, new ResourceLocation(CustomNpcs.MODID, "models/util/" + mt.name().toLowerCase() + ".obj"));
			locations.get(false).put(mt, new ResourceLocation(CustomNpcs.MODID, "textures/marks/" + mt.name().toLowerCase() + ".png"));
		}
	}

	public static void render(EntityLivingBase entity, double x, double y, double z, MarkData.Mark mark) {
		float alpha = 1.0f;
		GlStateManager.pushMatrix();
		int color = mark.color;
		GlStateManager.translate(x, y + entity.height + 0.6, z);
		GlStateManager.rotate(-entity.rotationYawHead, 0.0f, 1.0f, 0.0f);
		if(entity instanceof EntityNPCInterface) {
			if (entity.isInvisible()) {
				alpha = 0.333333f;
				color = (85 << 24) | (color & 0x00FFFFFF);
			}
			//if (((EntityNPCInterface) entity).display.getSize() > 5) { GlStateManager.scale(4.0f, 4.0f, 4.0f); }
		}
		if (mark.isRotate()) { GlStateManager.rotate(entity.world.getTotalWorldTime() % 360 / 0.25f, 0.0f, 1.0f, 0.0f); }
		if (mark.is3D()) {
			GlStateManager.rotate(180.0f, 1.0f, 1.0f, 0.0f);
			ModelBuffer.render(locations.get(true).get(mark.type), null, null, color, true);
		}
		else {
			GlStateManager.translate(-0.5f, 0, 0);
			float red = (float) (color >> 16 & 255) / 255.0f;
			float green = (float) (color >> 8 & 255) / 255.0f;
			float blue = (float) (color & 255) / 255.0f;
			GlStateManager.color(red, green, blue, alpha);
			Minecraft.getMinecraft().getTextureManager().bindTexture(locations.get(false).get(mark.type));
			GlStateManager.translate(-0.5, 0.0, 0.0);
			Model2DRenderer.renderItemIn2D(Tessellator.getInstance().getBuffer(), 0.0f, 0.0f, 1.0f, 1.0f, 32, 32, 0.0625f);
		}
		GlStateManager.popMatrix();

	}

}
