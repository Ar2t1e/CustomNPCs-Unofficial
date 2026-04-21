package noppes.npcs.client.renderer.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.BlockWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.renderer.ModelBuffer;
import noppes.npcs.client.renderer.obj.ParameterizedModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BlockScriptedRenderer<T extends TileScripted> extends TileEntitySpecialRenderer<T> {

	protected static final Map<String, ParameterizedModel> cache = new HashMap<>();
	protected static final Random random = new Random();

	public void render(@Nullable T tile, double x, double y, double z, float partialTicks, int blockDamage, float alpha) {
		if (tile == null) { return; }
		// Default model
		if (overrideModel()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			renderItem(new ItemStack(CustomBlocks.scripted));
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			return;
		}
		// Custom models
        for (ILayerBlockModel layer : new ArrayList<>(tile.getLayers())) {
			IItemStack itemModel = layer.getItemModel();
			BlockWrapper blockModel = (BlockWrapper) layer.getBlockModel();
			String objModel = layer.getOBJModel();
			if (!itemModel.isEmpty() || !blockModel.isEmpty() || objModel != null) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.translate(x + 0.5, y, z + 0.5);
				// offset
				GlStateManager.translate(layer.getOffset(0), layer.getOffset(1), layer.getOffset(2));
				// rotate
				if (layer.isRotate(1)) { GlStateManager.rotate(((float) System.currentTimeMillis() / layer.getRotateSpeed()) % 360, 0.0f, 1.0f, 0.0f); }
				else { GlStateManager.rotate(layer.getRotation(1), 0.0f, 1.0f, 0.0f); }
				if (layer.isRotate(0)) { GlStateManager.rotate(((float) System.currentTimeMillis() / layer.getRotateSpeed()) % 360, 1.0f, 0.0f, 0.0f); }
				else { GlStateManager.rotate(layer.getRotation(0), 1.0f, 0.0f, 0.0f); }
				if (layer.isRotate(2)) { GlStateManager.rotate(((float) System.currentTimeMillis() / layer.getRotateSpeed()) % 360, 0.0f, 0.0f, 1.0f); }
				else { GlStateManager.rotate(layer.getRotation(2), 0.0f, 0.0f, 1.0f); }
				// scale
				GlStateManager.scale(layer.getScale(0), layer.getScale(1), layer.getScale(2));
				// model
				if (!itemModel.isEmpty()) {
					GlStateManager.translate(0.0, 0.5, 0.0);
					renderItem(itemModel.getMCItemStack());
				}
				else if (!blockModel.isEmpty()) {
					renderBlock(tile, blockModel.getState());
				}
				else {
					String key = objModel + layer.getOBJVisibleMeshes() + layer.getOBJMaterialsReplase();
					if (cache.size() > 500) { cache.clear(); }
					if (!cache.containsKey(key)) {
						cache.put(key, ModelBuffer.getParameterizedModel(new ResourceLocation(key),
								layer.getOBJVisibleMeshes(),
								layer.getOBJMaterialsReplase(),
								false, 0, true));
					}
					ModelBuffer.render(cache.get(key));
				}
				GlStateManager.popMatrix();
            }
        }
		// texts
		for (ITextPlane iTextPlane : new ArrayList<>(tile.getTextPlanes())) {
			if(iTextPlane instanceof TileScripted.TextPlane &&
			!iTextPlane.getText().isEmpty()) { drawText((TileScripted.TextPlane) iTextPlane, x, y, z); }
		}
	}

	private void drawText(TileScripted.TextPlane textPlane, double x, double y, double z) {
		if (textPlane.textBlock == null || textPlane.textHasChanged) {
			textPlane.textBlock = new TextBlockClient(textPlane.text, 336, true, null, Minecraft.getMinecraft().player);
			textPlane.textHasChanged = false;
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.rotate(textPlane.rotationY, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(textPlane.rotationX, 1.0f, 0.0f, 0.0f);
		GlStateManager.rotate(textPlane.rotationZ, 0.0f, 0.0f, 1.0f);
		GlStateManager.scale(textPlane.scale, textPlane.scale, 1.0f);
		GlStateManager.translate(textPlane.offsetX, textPlane.offsetY, textPlane.offsetZ);
		RenderHelper.disableStandardItemLighting();
		float f1 = 0.6666667f;
		float f2 = 0.0133f * f1;
		GlStateManager.translate(0.0f, 0.5f, 0.01f);
		GlStateManager.scale(f2, -f2, f2);
		GlStateManager.glNormal3f(0.0f, 0.0f, -1.0f * f2);
		GlStateManager.depthMask(false);
		FontRenderer font = getFontRenderer();
		float lineOffset = 0.0f;
		if (textPlane.textBlock.lines.size() < 14) {
			lineOffset = (14.0f - textPlane.textBlock.lines.size()) / 2.0f;
		}
		for (int i = 0; i < textPlane.textBlock.lines.size(); ++i) {
			String text = textPlane.textBlock.lines.get(i).getString();
			font.drawString(text, -font.getStringWidth(text) / 2,
					(int) ((lineOffset + i) * (font.FONT_HEIGHT - 0.3)), 0);
		}
		GlStateManager.depthMask(true);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.popMatrix();
	}

	private void renderItem(ItemStack item) {
		Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.NONE);
	}

	private void renderBlock(@Nonnull T tile, IBlockState state) {
		GlStateManager.pushMatrix();
		ClientEventHandler.renderBlock(state);
		GlStateManager.popMatrix();
		if (random.nextInt(12) == 1) { state.getBlock().randomDisplayTick(state, tile.getWorld(), tile.getPos(), random); }
	}

	private boolean overrideModel() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return player == null ||
				player.getHeldItemMainhand().getItem() == CustomItems.wand ||
				player.getHeldItemMainhand().getItem() == CustomItems.scripter;
	}

}
