package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScripted.TextPlane;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.shared.common.util.LogWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockScriptedRenderer<T extends TileScripted> extends BlockRendererInterface<T> {
	
	private static RandomSource random = RandomSource.create();

	public BlockScriptedRenderer(BlockEntityRendererProvider.Context dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(@NotNull T tile, float partialTicks, PoseStack matrixStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
		matrixStack.pushPose();

        if(overrideModel()){
			matrixStack.translate(0.5f, 0.5f, 0.5f);
			matrixStack.scale(2, 2, 2);
        	renderItem(new ItemStack(CustomBlocks.scripted), matrixStack, buffer, light, overlay);
        }
        else{
			matrixStack.mulPose(Axis.YP.rotationDegrees(tile.rotationY));
			matrixStack.mulPose(Axis.XP.rotationDegrees(tile.rotationX));
			matrixStack.mulPose(Axis.ZP.rotationDegrees(tile.rotationZ));
			matrixStack.translate(0.5f, 0.0f, 0.5f);
			matrixStack.scale(tile.scaleX, tile.scaleY, tile.scaleZ);
			matrixStack.translate(-0.5f, 0.0f, -0.5f);
        	Block b = tile.blockModel;
        	if(b == null || b == Blocks.AIR || b == CustomBlocks.scripted) {
				matrixStack.translate(0.5f, 0.5f, 0.5f);
				matrixStack.scale(2, 2, 2);
        		renderItem(tile.itemModel, matrixStack, buffer, light, overlay);
        	}
        	else{
                BlockState state = b.defaultBlockState();
        		renderBlock(tile, b, state, matrixStack, buffer, light, overlay);
                if(state.hasBlockEntity() && !tile.renderTileErrored) {
                	try{
	                	if(tile.renderTile == null){
	                    	BlockEntity entity = ((EntityBlock)b).newBlockEntity(tile.getBlockPos(), state);
							entity.setLevel(tile.getLevel());
	                    	//ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, tile.itemModel.getItemDamage(), 5);
	                    	//ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, b, 6);
							tile.renderTile = entity;
							tile.renderState = state;
							tile.renderTileUpdate = (BlockEntityTicker<BlockEntity>) ((EntityBlock) b).getTicker(tile.getLevel(), state, entity.getType());
	                	}
						BlockEntityRenderer renderer =  Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tile.renderTile);
	                    if(renderer != null){
	                    	renderer.render(tile.renderTile, partialTicks, matrixStack, buffer, light, overlay);
	                    }
	                    else { tile.renderTileErrored = true; }
                	}
                	catch (Exception e){ tile.renderTileErrored = true; }
                }
        	}
        }
		matrixStack.popPose();

        if(!tile.text1.text.isEmpty()) {
        	drawText(matrixStack, tile.text1, buffer, light, overlay);
        }
        if(!tile.text2.text.isEmpty()) {
        	drawText(matrixStack, tile.text2, buffer, light, overlay);
        }
        if(!tile.text3.text.isEmpty()) {
        	drawText(matrixStack, tile.text3, buffer, light, overlay);
        }
        if(!tile.text4.text.isEmpty()) {
        	drawText(matrixStack, tile.text4, buffer, light, overlay);
        }
        if(!tile.text5.text.isEmpty()) {
        	drawText(matrixStack, tile.text5, buffer, light, overlay);
        }
        if(!tile.text6.text.isEmpty()) {
        	drawText(matrixStack, tile.text6, buffer, light, overlay);
        }
	}
	
	private void drawText(PoseStack matrixStack, TextPlane text1, MultiBufferSource buffer, int light, int overlay) {
		if(text1.textBlock == null || text1.textHasChanged){
			text1.textBlock = new TextBlockClient(text1.text, 336, true, Minecraft.getInstance().player);
			text1.textHasChanged = false;
		}
		matrixStack.pushPose();
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.mulPose(Axis.YP.rotationDegrees(text1.rotationY));
		matrixStack.mulPose(Axis.XP.rotationDegrees(text1.rotationX));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(text1.rotationZ));
		matrixStack.scale(text1.scale, text1.scale, 1);
		matrixStack.translate(text1.offsetX, text1.offsetY, text1.offsetZ);
        float f1 = 0.6666667F;
        float f3 = 0.0133F * f1;
		matrixStack.translate(0.0F, 0.5f, 0.01F);
		matrixStack.scale(f3, -f3, f3);
//        RenderSystem.normal3f(0.0F, 0.0F, -1.0F * f3);
//        RenderSystem.depthMask(false);
        Font fontrenderer = Minecraft.getInstance().font;
        
        float lineOffset = 0;
        if(text1.textBlock.lines.size() < 14)
        	lineOffset = (14f - text1.textBlock.lines.size()) / 2;
    	for(int i = 0; i < text1.textBlock.lines.size(); i++){
			Component text = text1.textBlock.lines.get(i);
    		fontrenderer.drawInBatch(text, -fontrenderer.width(text) / 2, (int)((lineOffset + i) * (fontrenderer.lineHeight - 0.3)), 0, false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL, light, overlay);
    	}

//        RenderSystem.depthMask(true);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.popPose();
	}
	
	private void renderItem(ItemStack item, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay){
		Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, null, 0);
		//Minecraft.getInstance().getItemRenderer().render(item, ItemTransforms.TransformType.NONE);
	}
	
	private void renderBlock(TileScripted tile, Block b, BlockState state, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay){
		matrixStack.pushPose();
		//matrixStack.translate(-5F, -0, 5F);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStack, buffer, light, OverlayTexture.NO_OVERLAY);
        if(random.nextInt(12) == 1)
        	state.getBlock().animateTick(state, tile.getLevel(), tile.getBlockPos(),  random);
		matrixStack.popPose();
	}
	
	private boolean overrideModel(){
		ItemStack held = Minecraft.getInstance().player.getMainHandItem();
		if(held == null)
			return false;
		
		return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter;
	}
}
