package noppes.npcs.client.renderer.blocks;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.client.renderer.ShaderProgram;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPortalRenderer<T extends CustomTileEntityPortal> extends TileEntitySpecialRenderer<T> {

	public static Map<String, ShaderProgram> cash = new HashMap<>();

	public BlockPortalRenderer() {
		loadShaders();
		registerReloadListener();
	}

	@Override
	public void render(@Nullable T te, double x, double y, double z,
					   float partialTicks, int destroyStage, float alpha) {
		if (te != null && te.getBlockType() instanceof CustomBlockPortal) {
			ShaderProgram shader = getShader(((CustomBlockPortal) te.getBlockType()).getCustomName());
			if (shader != null) {
				Minecraft mc = Minecraft.getMinecraft();

				int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
				int prevTexUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
				boolean prevBlend = GL11.glIsEnabled(GL11.GL_BLEND);
				int prevBlendSrc = GL11.glGetInteger(GL11.GL_BLEND_SRC);
				int prevBlendDst = GL11.glGetInteger(GL11.GL_BLEND_DST);
				boolean prevLight = GL11.glIsEnabled(GL11.GL_LIGHTING);
				boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

				GlStateManager.disableLighting();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(
						GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
				);
				GlStateManager.depthMask(false);

				shader.use();
				try {
					shader.set1f("Alpha", ValueUtil.correctFloat(te.getAlpha(), 0.15f, 1.0f));
					shader.set1f("GameTime", (Minecraft.getSystemTime() % 800000L) / 800000.0F);
					shader.set1i("PortalLayers", getPasses(x * x + y * y + z * z));

					ShaderProgram.texUnit(GL13.GL_TEXTURE0);
					mc.getTextureManager().bindTexture(te.getSkyTexture());
					shader.set1i("Sampler0", 0);

					ShaderProgram.texUnit(GL13.GL_TEXTURE1);
					mc.getTextureManager().bindTexture(te.getPortalTexture());
					shader.set1i("Sampler1", 1);

					Tessellator tess = Tessellator.getInstance();
					BufferBuilder buf = tess.getBuffer();
					buf.begin(7, DefaultVertexFormats.POSITION_COLOR);

					for (EnumFacing facing : EnumFacing.values()) {
						if (te.shouldRenderFace(facing)) {
							renderFace(buf, facing, x, y, z);
						}
					}
					tess.draw();
				} finally {
					shader.stop();
					ShaderProgram.texUnit(GL13.GL_TEXTURE1);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
					ShaderProgram.texUnit(prevTexUnit);

					if (!prevBlend) {
						GlStateManager.disableBlend();
					} else {
						GlStateManager.enableBlend();
						GL11.glBlendFunc(prevBlendSrc, prevBlendDst);
					}

					GL20.glUseProgram(prevProgram);

					if (prevLight) GlStateManager.enableLighting();
					else GlStateManager.disableLighting();

					GlStateManager.depthMask(prevDepthMask);
				}
			}
		}
	}

	private void registerReloadListener() {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
				.registerReloadListener(manager -> reloadShaders());
	}

	public static void loadShaders() {
		File dir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/shaders/core");
		List<File> list = Util.instance.getFiles(dir, ".vsh");
		for (File file : list) {
			String name = NoppesUtilServer.validPath(
					file.getName().toLowerCase().replace(".vsh", "")
			);
			try {
				cash.put(name, new ShaderProgram(
						new ResourceLocation(CustomNpcs.MODID, "shaders/core/" + name + ".vsh"),
						new ResourceLocation(CustomNpcs.MODID, "shaders/core/" + name + ".fsh")
				));
				LogWriter.debug("Load shader \"" + name + "\"");
			} catch (Exception e) {
				LogWriter.error("Error load shader \"" + name + "\"", e);
			}
		}
	}

	public static void reloadShaders() {
		for (ShaderProgram shader : cash.values()) { shader.delete(); }
		cash.clear();
		loadShaders();
		LogWriter.info("Shaders reloaded");
	}

	public static ShaderProgram getShader(String name) {
		if (!cash.containsKey(name)) {
			ShaderProgram fallback = cash.get("portalexample");
			if (fallback != null) {
				cash.put(name, fallback);
			}
		}
		return cash.get(name);
	}

	protected int getPasses(double distanceSq) {
		if (distanceSq > 36864.0D)  { return 1; }
		else if (distanceSq > 25600.0D)  { return 3; }
		else if (distanceSq > 16384.0D)  { return 5; }
		else if (distanceSq > 9216.0D)  { return 7; }
		else if (distanceSq > 4096.0D)  { return 9; }
		else if (distanceSq > 1024.0D)  { return 11; }
		else if (distanceSq > 576.0D)  { return 13; }
		else if (distanceSq > 256.0D)  { return 14; }
		return 15;
	}

	private void renderFace(BufferBuilder buffer, EnumFacing facing, double x, double y, double z) {
		switch (facing) {
			case SOUTH: {
				buffer.pos(x, y, z + 0.75D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y, z + 0.75D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 1.0D, z + 0.75D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x, y + 1.0D, z + 0.75D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
			case NORTH: {
				buffer.pos(x, y + 1.0D, z + 0.25D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 1.0D, z + 0.25D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y, z + 0.25D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x, y, z + 0.25D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
			case EAST: {
				buffer.pos(x + 0.75D, y + 1.0D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.75D, y + 1.0D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.75D, y, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.75D, y, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
			case WEST: {
				buffer.pos(x + 0.25D, y, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.25D, y, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.25D, y + 1.0D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 0.25D, y + 1.0D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
			case DOWN: {
				buffer.pos(x, y + 0.25D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 0.25D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 0.25D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x, y + 0.25D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
			case UP: {
				buffer.pos(x, y + 0.75D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 0.75D, z + 1.0D).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x + 1.0D, y + 0.75D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				buffer.pos(x, y + 0.75D, z).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
				break;
			}
		}
	}

}