package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockRendererInterface<T extends BlockEntity> implements BlockEntityRenderer<T> {
	//protected static final ResourceLocation Steel = new ResourceLocation("customnpcs","textures/models/Steel.png");
	
    public static float colorTable[][] = {
        {
            1.0F, 1.0F, 1.0F
        }, {
            0.95F, 0.7F, 0.2F
        }, {
            0.9F, 0.5F, 0.85F
        }, {
            0.6F, 0.7F, 0.95F
        }, {
            0.9F, 0.9F, 0.2F
        }, {
            0.5F, 0.8F, 0.1F
        }, {
            0.95F, 0.7F, 0.8F
        }, {
            0.3F, 0.3F, 0.3F
        }, {
            0.6F, 0.6F, 0.6F
        }, {
            0.3F, 0.6F, 0.7F
        }, {
            0.7F, 0.4F, 0.9F
        }, {
            0.2F, 0.4F, 0.8F
        }, {
            0.5F, 0.4F, 0.3F
        }, {
            0.4F, 0.5F, 0.2F
        }, {
            0.8F, 0.3F, 0.3F
        }, {
            0.1F, 0.1F, 0.1F
        }
    };

    public BlockRendererInterface(BlockEntityRendererProvider.Context dispatcher) {

    }

    public boolean playerTooFar(BlockEntity tile){
		Minecraft mc = Minecraft.getInstance();
        double d6 = mc.getCameraEntity().getX() - tile.getBlockPos().getX();
        double d7 = mc.getCameraEntity().getY() - tile.getBlockPos().getY();
        double d8 = mc.getCameraEntity().getZ() - tile.getBlockPos().getZ();

        return d6 * d6 + d7 * d7 + d8 * d8 > specialRenderDistance() * specialRenderDistance();
	}
	
	public int specialRenderDistance(){
		return 20;
	}
}
