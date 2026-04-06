package noppes.npcs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.EntityModel;
import noppes.npcs.entity.EntityNPCInterface;
import org.jetbrains.annotations.NotNull;

public class RenderNpcDragon<T extends EntityNPCInterface, M extends EntityModel<T>> extends RenderNPCInterface<T, M>{

	public RenderNpcDragon(EntityRendererProvider.Context manager, M model, float f) {
		super(manager, model, f);
	}

	@Override
    protected void scale(@NotNull T npc, PoseStack matrixScale, float f){
		matrixScale.translate(0, 0, 0.6f / 5 * npc.display.getSize());
    	super.scale(npc, matrixScale, f);
    }
}
