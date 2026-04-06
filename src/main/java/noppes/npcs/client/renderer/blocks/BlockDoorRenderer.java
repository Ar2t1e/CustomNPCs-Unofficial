package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.data.ModelData;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileDoor;

import java.util.Random;

public class BlockDoorRenderer extends BlockRendererInterface<TileDoor> {
	
	private static Random random = new Random();

    public BlockDoorRenderer(BlockEntityRendererProvider.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TileDoor tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        BlockState original = tile.getLevel().getBlockState(tile.getBlockPos());
        if(original.isAir()){
            return;
        }

        BlockPos lowerPos = tile.getBlockPos();

        if(original.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER){
            lowerPos = tile.getBlockPos().below();
        }

        BlockPos upperPos = lowerPos.above();

        TileDoor lowerTile = (TileDoor) tile.getLevel().getBlockEntity(lowerPos);
        TileDoor upperTile = (TileDoor) tile.getLevel().getBlockEntity(upperPos);

        if(lowerTile==null || upperTile==null)
            return;

        BlockState lowerState = lowerTile.getBlockState();
        BlockState upperState = upperTile.getBlockState();


        Block b = lowerTile.blockModel;

        if (overrideModel()) {
            b = CustomBlocks.scripted_door;
        }
        BlockState state = b.defaultBlockState();

        state = state.setValue(DoorBlock.HALF, original.getValue(DoorBlock.HALF));
        state = state.setValue(DoorBlock.FACING, lowerState.getValue(DoorBlock.FACING));
        state = state.setValue(DoorBlock.OPEN, lowerState.getValue(DoorBlock.OPEN));
        state = state.setValue(DoorBlock.HINGE, upperState.getValue(DoorBlock.HINGE));
        state = state.setValue(DoorBlock.POWERED, upperState.getValue(DoorBlock.POWERED));

        matrixStack.pushPose();

        //RenderHelper.enableStandardItemLighting();
        //matrixStack.translate(0.5, 0, 0.5);

        //matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
        renderBlock(matrixStack, buffer, tile, lowerState.getBlock(), state, light, overlay);

        matrixStack.popPose();
    }

	
	private void renderBlock(PoseStack matrixStack, MultiBufferSource buffer, TileDoor tile, Block b, BlockState state, int light, int overlay){
        //this.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
        //RenderSystem.translatef(-0.5F, -0, 0.5F);
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel ibakedmodel = dispatcher.getBlockModel(state);
        if(ibakedmodel == null){
            dispatcher.renderSingleBlock(state, matrixStack, buffer, light, overlay);
        }
        else{
            dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)), state, ibakedmodel, 1, 1, 1, light, overlay, ModelData.EMPTY, ItemBlockRenderTypes.getRenderType(state, false));
        }
	}
	
	private boolean overrideModel(){
		ItemStack held = Minecraft.getInstance().player.getMainHandItem();
		if(held == null)
			return false;
		
		return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter || held.getItem() == CustomBlocks.scripted_door_item;
	}
}
